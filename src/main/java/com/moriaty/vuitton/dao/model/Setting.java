package com.moriaty.vuitton.dao.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Moriaty
 * @since 2024-04-08 18:04:34
 */
@Getter
@Setter
@Accessors(chain = true)
public class Setting {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("`group`")
    private Integer group;

    @TableField("`name`")
    private String name;

    private String content;

    @TableField("`description`")
    private String description;
}
