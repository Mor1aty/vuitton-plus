package com.moriaty.vuitton.bean.novel.local;

import com.moriaty.vuitton.dao.mongo.model.MongoNovelChapterContent;
import com.moriaty.vuitton.dao.mysql.model.NovelChapter;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 带内容的小说章节
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/28 上午8:46
 */
@Data
@Accessors(chain = true)
public class NovelChapterWithContent {

    private NovelChapter chapter;

    private MongoNovelChapterContent content;

}
