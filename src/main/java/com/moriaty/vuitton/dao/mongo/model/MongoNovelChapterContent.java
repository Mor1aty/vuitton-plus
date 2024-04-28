package com.moriaty.vuitton.dao.mongo.model;

import com.anwen.mongo.annotation.ID;
import com.anwen.mongo.annotation.collection.CollectionField;
import com.anwen.mongo.annotation.collection.CollectionName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 小说章节内容
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/28 上午8:35
 */
@Data
@Accessors(chain=true)
@CollectionName("novel_chapter_content")
public class MongoNovelChapterContent {

    @ID
    private String id;

    @CollectionField("novelName")
    private String novelName;

    @CollectionField("chapterTitle")
    private String chapterTitle;

    @CollectionField("content")
    private String content;

    @CollectionField("contentHtml")
    private String contentHtml;

}
