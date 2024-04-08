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
 * 小说章节表
 * </p>
 *
 * @author Moriaty
 * @since 2024-04-08 17:58:39
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("novel_chapter")
public class NovelChapter {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer novel;

    @TableField("`index`")
    private Integer index;

    private String title;

    private String content;

    private String contentHtml;
}
