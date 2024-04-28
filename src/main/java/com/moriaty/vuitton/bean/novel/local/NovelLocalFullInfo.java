package com.moriaty.vuitton.bean.novel.local;

import com.moriaty.vuitton.dao.mysql.model.Novel;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 本地小说全部信息
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 上午12:22
 */
@Data
@Accessors(chain = true)
public class NovelLocalFullInfo {

    private Novel novel;

    private List<NovelChapterWithContent> chapterContentList;

    private BaseNovelDownloader novelDownloader;

}
