package com.moriaty.vuitton.dao.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 小说表
 * </p>
 *
 * @author Moriaty
 * @since 2024-04-08 17:58:39
 */
@Getter
@Setter
@Accessors(chain = true)
public class Novel {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("`name`")
    private String name;

    private String author;

    private String intro;

    private String imgUrl;

    private String fileUrl;

    private String downloaderMark;

    private String downloaderCatalogueUrl;
}
