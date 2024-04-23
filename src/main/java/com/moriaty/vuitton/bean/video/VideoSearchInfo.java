package com.moriaty.vuitton.bean.video;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 视频搜索信息
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/23 下午7:46
 */
@Data
@Accessors(chain = true)
public class VideoSearchInfo {

    private Integer videoId;

    private String videoName;

    private String videoDescription;

    private String videoImgUrl;

    private LocalDateTime videoCreateTime;

    private Integer lastPlayEpisodeId;

    private String lastPlayEpisodeName;

    private Integer lastPlayEpisodeSecond;

    private String lastPlayEpisodeSecondStr;

    private LocalDateTime lastPlayTime;

}
