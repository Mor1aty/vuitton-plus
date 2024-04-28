package com.moriaty.vuitton.dao.mysql.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 视频表
 * </p>
 *
 * @author Moriaty
 * @since 2024-04-10 17:13:56
 */
@Getter
@Setter
@Accessors(chain = true)
public class Video {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("`name`")
    private String name;

    @TableField("`description`")
    private String description;

    private String imgUrl;

    private LocalDateTime createTime;
}
