package com.moriaty.vuitton.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moriaty.vuitton.bean.PageResp;
import com.moriaty.vuitton.bean.video.VideoAroundEpisode;
import com.moriaty.vuitton.bean.video.VideoPlayHistoryInfo;
import com.moriaty.vuitton.bean.video.VideoPlayHistoryRedisInfo;
import com.moriaty.vuitton.bean.video.VideoSearchInfo;
import com.moriaty.vuitton.bean.video.req.*;
import com.moriaty.vuitton.constant.Constant;
import com.moriaty.vuitton.dao.mysql.mapper.VideoEpisodeMapper;
import com.moriaty.vuitton.dao.mysql.mapper.VideoMapper;
import com.moriaty.vuitton.dao.mysql.mapper.VideoPlayHistoryMapper;
import com.moriaty.vuitton.dao.mysql.model.Video;
import com.moriaty.vuitton.dao.mysql.model.VideoEpisode;
import com.moriaty.vuitton.dao.mysql.model.VideoPlayHistory;
import com.moriaty.vuitton.library.wrap.WrapMapper;
import com.moriaty.vuitton.library.wrap.Wrapper;
import com.moriaty.vuitton.module.video.VideoModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 视频 Service
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/4 下午10:28
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoModule videoModule;

    private final VideoMapper videoMapper;

    private final VideoEpisodeMapper videoEpisodeMapper;

    private final VideoPlayHistoryMapper videoPlayHistoryMapper;

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${file-server.video.video-folder}")
    private String videoFolderUrl;

    @Value("${file-server.video.img-folder}")
    private String imgFolderUrl;


    public Wrapper<Void> importVideo(ImportVideoReq req) {
        Optional<String> imgOptional = videoModule.importImgUrl(req.getName(), imgFolderUrl);
        if (imgOptional.isEmpty()) {
            return WrapMapper.failure("图片不存在");
        }
        Video video = new Video()
                .setName(req.getName())
                .setDescription(req.getDescription())
                .setImgUrl(imgOptional.get())
                .setCreateTime(LocalDateTime.now());
        videoMapper.insert(video);
        Optional<List<VideoEpisode>> episodeOptional = videoModule.importEpisode(
                videoFolderUrl + req.getName() + "/", video.getId());
        episodeOptional.ifPresent(videoEpisodeMapper::batchInsert);
        return WrapMapper.ok();
    }

    public Wrapper<PageResp<VideoSearchInfo>> findVideo(FindVideoReq req) {
        int startIndex = (req.getPageNum() - 1) * req.getPageSize();
        if (StringUtils.hasText(req.getName())) {
            req.setName("%" + req.getName() + "%");
        }
        if (req.getSearchType() != null && req.getSearchType() == Constant.Video.SEARCH_TYPE_PLAY_HISTORY) {
            List<VideoSearchInfo> videoList = videoMapper.findHistorySearchVideoPage(req.getName(),
                    startIndex, req.getPageSize());
            long total = videoMapper.findHistorySearchVideoPageTotal(req.getName());
            return WrapMapper.ok(new PageResp<>(req, videoList, total));
        }
        List<VideoSearchInfo> videoList = videoMapper.findSearchVideoPage(req.getName(),
                startIndex, req.getPageSize());
        long total = videoMapper.findSearchVideoPageTotal(req.getName());
        return WrapMapper.ok(new PageResp<>(req, videoList, total));
    }

    public Wrapper<PageResp<VideoEpisode>> findEpisode(FindEpisodeReq req) {
        Page<VideoEpisode> episodePage = videoEpisodeMapper.selectPage(
                new Page<>(req.getPageNum(), req.getPageSize()), new LambdaQueryWrapper<VideoEpisode>()
                        .eq(VideoEpisode::getVideo, req.getVideoId()));
        return WrapMapper.ok(new PageResp<>(req, episodePage));
    }

    public Wrapper<VideoAroundEpisode> findAroundEpisode(FindAroundEpisodeReq req) {
        List<VideoEpisode> episodeList = videoEpisodeMapper.selectList(new LambdaQueryWrapper<VideoEpisode>()
                .eq(VideoEpisode::getVideo, req.getVideoId()));
        if (episodeList.isEmpty()) {
            return WrapMapper.failure("视频分集不存在");
        }
        Optional<VideoAroundEpisode> optional =
                videoModule.findAroundEpisode(episodeList, req.getEpisodeIndex());
        return optional.map(WrapMapper::ok).orElseGet(() -> WrapMapper.failure("视频当前分集不存在"));
    }

    public Wrapper<List<VideoPlayHistoryInfo>> findPlayHistory(FindPlayHistoryReq req) {
        if (req.getLimitNum() == null || req.getLimitNum() <= 0) {
            req.setLimitNum(100);
        }
        List<VideoPlayHistoryInfo> playHistoryList = videoPlayHistoryMapper.findPlayHistory(req.getVideoId(),
                req.getLimitNum());
        return WrapMapper.ok(playHistoryList);
    }

    public Wrapper<Void> insertPlayHistory(InsertPlayHistoryReq req) {
        String redisKey = Constant.Video.REDIS_PREFIX_PLAY_HISTORY + req.getVideoId();
        String redisValueJson = stringRedisTemplate.opsForValue().get(redisKey);
        VideoPlayHistoryRedisInfo redisValue = new VideoPlayHistoryRedisInfo();
        if (StringUtils.hasText(redisValueJson)) {
            redisValue = JSON.parseObject(redisValueJson, VideoPlayHistoryRedisInfo.class);
            redisValue.setEpisodePlaySecond(req.getEpisodePlaySecond());
        } else {
            Video video = videoMapper.selectById(req.getVideoId());
            if (video == null) {
                return WrapMapper.failure("视频不存在");
            }
            VideoEpisode episode = videoEpisodeMapper.selectById(req.getEpisodeId());
            if (episode == null || !video.getId().equals(episode.getVideo())) {
                return WrapMapper.failure("视频分集不存在");
            }
            redisValue.setKey(redisKey);
            redisValue.setEpisodePlaySecond(req.getEpisodePlaySecond());
            redisValue.setVideo(video);
            redisValue.setVideoEpisode(episode);
        }

        if (Boolean.FALSE.equals(req.getStore())) {
            stringRedisTemplate.opsForValue().set(redisKey, JSON.toJSONString(redisValue),
                    Constant.Video.REDIS_TTL_PLAY_HISTORY);
        } else {
            stringRedisTemplate.delete(redisKey);
            List<VideoPlayHistory> playHistoryList = videoPlayHistoryMapper.selectList(
                    new LambdaQueryWrapper<VideoPlayHistory>().eq(VideoPlayHistory::getVideo, req.getVideoId()));
            if (!playHistoryList.isEmpty()) {
                videoPlayHistoryMapper.deleteBatchIds(playHistoryList.stream().map(VideoPlayHistory::getId).toList());
            }
            int episodePlayMinute = req.getEpisodePlaySecond() / 60;
            int remainEpisodePlaySecond = req.getEpisodePlaySecond() - episodePlayMinute * 60;
            videoPlayHistoryMapper.insert(new VideoPlayHistory()
                    .setVideo(req.getVideoId())
                    .setEpisode(req.getEpisodeId())
                    .setEpisodePlaySecond(req.getEpisodePlaySecond())
                    .setEpisodePlaySecondStr(episodePlayMinute + "分" + remainEpisodePlaySecond + "秒")
                    .setPlayTime(LocalDateTime.now()));
        }
        return WrapMapper.ok();
    }
}
