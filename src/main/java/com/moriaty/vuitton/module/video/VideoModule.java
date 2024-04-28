package com.moriaty.vuitton.module.video;

import com.moriaty.vuitton.ServerInfo;
import com.moriaty.vuitton.bean.video.VideoAroundEpisode;
import com.moriaty.vuitton.dao.mysql.model.VideoEpisode;
import com.moriaty.vuitton.module.Module;
import com.moriaty.vuitton.module.ModuleFactory;
import com.moriaty.vuitton.util.FileServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 视频模块
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 上午11:31
 */
@Component
@Slf4j
public class VideoModule implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        ModuleFactory.addModule(new Module()
                .setId(0)
                .setName("视频"));
    }

    public Optional<String> importImgUrl(String name, String imgFolderUrl) {
        List<String> imgFileList =
                FileServerUtil.findFileFromFolder(ServerInfo.INFO.getFileServerUrl() + imgFolderUrl);
        String img = null;
        for (String imgFile : imgFileList) {
            String[] split = imgFile.split("\\.");
            if (name.equals(split[0])) {
                img = imgFile;
                break;
            }
        }
        if (img == null) {
            return Optional.empty();
        }
        return Optional.of(imgFolderUrl + img);
    }

    public Optional<List<VideoEpisode>> importEpisode(String episodeFolderUrl, Integer videoId) {
        List<String> episodeFileList = FileServerUtil.findFileFromFolder(
                ServerInfo.INFO.getFileServerUrl() + episodeFolderUrl);
        List<VideoEpisode> episodeList = new ArrayList<>();
        for (int i = 0; i < episodeFileList.size(); i++) {
            String episodeFile = episodeFileList.get(i);
            String episodeName = episodeFile;
            if (episodeName.endsWith(".mp4")) {
                episodeName = episodeName.substring(0, episodeName.lastIndexOf(".mp4"));
            }
            episodeList.add(new VideoEpisode()
                    .setVideo(videoId)
                    .setIndex(i + 1)
                    .setName(episodeName)
                    .setUrl(episodeFolderUrl + episodeFile));
        }
        if (episodeList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(episodeList);
    }

    public Optional<VideoAroundEpisode> findAroundEpisode(List<VideoEpisode> episodeList, int episodeIndex) {
        for (int i = 0; i < episodeList.size(); i++) {
            VideoEpisode episode = episodeList.get(i);
            if (episodeIndex == episode.getIndex()) {
                VideoAroundEpisode aroundEpisode = new VideoAroundEpisode().setEpisode(episode);
                if (i - 1 >= 0) {
                    aroundEpisode.setPreEpisode(episodeList.get(i - 1));
                }
                if (i + 1 < episodeList.size()) {
                    aroundEpisode.setNextEpisode(episodeList.get(i + 1));
                }
                return Optional.of(aroundEpisode);
            }
        }
        return Optional.empty();
    }
}
