package com.moriaty.vuitton.dao.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 视频分集表
 * </p>
 *
 * @author Moriaty
 * @since 2024-02-07 20:49:54
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("video_episode")
public class VideoEpisode {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer video;

    @TableField("`index`")
    private Integer index;

    @TableField("`name`")
    private String name;

    private String url;

    private Integer opStart;

    private Integer opEnd;

    private Integer edStart;

    private Integer edEnd;
}
