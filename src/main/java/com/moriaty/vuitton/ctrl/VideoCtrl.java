package com.moriaty.vuitton.ctrl;

import com.moriaty.vuitton.bean.PageResp;
import com.moriaty.vuitton.bean.video.VideoAroundEpisode;
import com.moriaty.vuitton.bean.video.VideoPlayHistoryInfo;
import com.moriaty.vuitton.bean.video.req.*;
import com.moriaty.vuitton.dao.model.Video;
import com.moriaty.vuitton.dao.model.VideoEpisode;
import com.moriaty.vuitton.library.wrap.Wrapper;
import com.moriaty.vuitton.service.VideoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 视频 Ctrl
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/4 下午7:49
 */
@RestController
@RequestMapping("video")
@AllArgsConstructor
@Slf4j
public class VideoCtrl {

    private final VideoService videoService;

    @PostMapping("importVideo")
    public Wrapper<Void> importVideo(@RequestBody @Validated ImportVideoReq req) {
        return videoService.importVideo(req);
    }

    @PostMapping("findVideo")
    public Wrapper<PageResp<Video>> findVideo(@RequestBody @Validated FindVideoReq req) {
        return videoService.findVideo(req);
    }

    @PostMapping("findEpisode")
    Wrapper<PageResp<VideoEpisode>> findEpisode(@RequestBody @Validated FindEpisodeReq req) {
        return videoService.findEpisode(req);
    }

    @PostMapping("findAroundEpisode")
    Wrapper<VideoAroundEpisode> findAroundEpisode(@RequestBody @Validated FindAroundEpisodeReq req) {
        return videoService.findAroundEpisode(req);
    }

    @PostMapping("findPlayHistory")
    Wrapper<List<VideoPlayHistoryInfo>> findPlayHistory(@RequestBody @Validated FindPlayHistoryReq req) {
        return videoService.findPlayHistory(req);
    }

    @PostMapping("insertPlayHistory")
    Wrapper<Void> insertPlayHistory(@RequestBody @Validated InsertPlayHistoryReq req) {
        return videoService.insertPlayHistory(req);
    }

}
