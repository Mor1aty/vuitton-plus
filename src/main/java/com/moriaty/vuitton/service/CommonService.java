package com.moriaty.vuitton.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.moriaty.vuitton.ServerInfo;
import com.moriaty.vuitton.bean.common.CommonInfo;
import com.moriaty.vuitton.bean.common.SettingInfo;
import com.moriaty.vuitton.bean.common.VideoPlayerSetting;
import com.moriaty.vuitton.constant.Constant;
import com.moriaty.vuitton.dao.mysql.mapper.SettingMapper;
import com.moriaty.vuitton.dao.mysql.model.Setting;
import com.moriaty.vuitton.library.wrap.WrapMapper;
import com.moriaty.vuitton.library.wrap.Wrapper;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * <p>
 * 通用 Service
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午12:20
 */
@Service
@AllArgsConstructor
@Slf4j
public class CommonService {

    private final SettingMapper settingMapper;

    public Wrapper<CommonInfo> info() {
        return WrapMapper.ok(new CommonInfo()
                .setFileServerUrl(ServerInfo.INFO.getFileServerUrl())
                .setFileServerUploadUrl(ServerInfo.INFO.getFileServerUploadUrl())
                .setNovelDownloaderList(NovelDownloaderFactory.getAllDownloaderInfo()));
    }

    public Wrapper<SettingInfo> setting(Integer group) {
        LambdaQueryWrapper<Setting> queryWrapper = new LambdaQueryWrapper<>();
        if (group != null) {
            queryWrapper.eq(Setting::getGroup, group);
        }

        // 播放器设置
        VideoPlayerSetting videoPlayer = new VideoPlayerSetting()
                .setGroup(Constant.Setting.GroupVideoPlayer.GROUP);

        settingMapper.selectList(queryWrapper).forEach(setting -> {
            if (Constant.Setting.GroupVideoPlayer.GROUP == setting.getGroup()) {
                if (Constant.Setting.GroupVideoPlayer.SKIP_OP_ED.equals(setting.getName())) {
                    videoPlayer.setSkipOpEdName(setting.getName());
                    videoPlayer.setSkipOpEd("true".equals(setting.getContent()));
                }
                if (Constant.Setting.GroupVideoPlayer.AUTO_PLAY_NEXT.equals(setting.getName())) {
                    videoPlayer.setAutoPlayNextName(setting.getName());
                    videoPlayer.setAutoPlayNext("true".equals(setting.getContent()));
                }
            }
        });
        return WrapMapper.ok(new SettingInfo()
                .setVideoPlayer(videoPlayer));
    }

    public Wrapper<Void> updateSetting(SettingInfo settingInfo) {
        if (settingInfo == null) {
            return WrapMapper.ok();
        }

        // 更新播放器设置
        VideoPlayerSetting videoPlayer = settingInfo.getVideoPlayer();
        if (videoPlayer != null) {
            if (StringUtils.hasText(videoPlayer.getSkipOpEdName())) {
                settingMapper.update(new LambdaUpdateWrapper<Setting>()
                        .eq(Setting::getGroup, videoPlayer.getGroup())
                        .eq(Setting::getName, videoPlayer.getSkipOpEdName())
                        .set(Setting::getContent, videoPlayer.isSkipOpEd() ? "true" : "false"));
            }
            if (StringUtils.hasText(videoPlayer.getAutoPlayNextName())) {
                settingMapper.update(new LambdaUpdateWrapper<Setting>()
                        .eq(Setting::getGroup, videoPlayer.getGroup())
                        .eq(Setting::getName, videoPlayer.getAutoPlayNextName())
                        .set(Setting::getContent, videoPlayer.isAutoPlayNext() ? "true" : "false"));
            }
        }
        return WrapMapper.ok();
    }
}
