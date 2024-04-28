package com.moriaty.vuitton.bean.video;

import com.moriaty.vuitton.dao.mysql.model.Video;
import com.moriaty.vuitton.dao.mysql.model.VideoEpisode;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 视频播放历史 Redis 信息
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/8 下午11:56
 */
@Data
@Accessors(chain = true)
public class VideoPlayHistoryRedisInfo {

    private String key;

    private Integer episodePlaySecond;

    private Video video;

    private VideoEpisode videoEpisode;

}
