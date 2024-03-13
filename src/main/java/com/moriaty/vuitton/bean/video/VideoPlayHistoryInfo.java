package com.moriaty.vuitton.bean.video;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 视频播放历史信息
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/6 上午11:33
 */
@Data
@Accessors(chain = true)
public class VideoPlayHistoryInfo {

    private Integer videoId;

    private String videoName;

    private String videoDescription;

    private String videoImgUrl;

    private Integer episodeId;

    private Integer episodeIndex;

    private String episodeName;

    private LocalDateTime playTime;

}
