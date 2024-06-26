package com.moriaty.vuitton.dao.mysql.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 视频播放历史表
 * </p>
 *
 * @author Moriaty
 * @since 2024-04-08 22:11:52
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("video_play_history")
public class VideoPlayHistory {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer video;

    private Integer episode;

    private Integer episodePlaySecond;

    private String episodePlaySecondStr;

    private LocalDateTime playTime;
}
