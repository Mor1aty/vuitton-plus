package com.moriaty.vuitton.bean.novel.local;

import com.moriaty.vuitton.dao.model.NovelChapter;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 本地小说环绕章节
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/2 下午11:16
 */
@Data
@Accessors(chain = true)
public class NovelLocalAroundChapter {

    private NovelChapter chapter;

    private NovelChapter preChapter;

    private NovelChapter nextChapter;
}
