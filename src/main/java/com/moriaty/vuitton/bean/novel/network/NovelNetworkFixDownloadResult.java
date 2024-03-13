package com.moriaty.vuitton.bean.novel.network;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 网络小说修补下载结果
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午8:35
 */
@Data
@Accessors(chain = true)
public class NovelNetworkFixDownloadResult {

    private List<NovelNetworkContent> fixContentList;

    private List<NovelNetworkContent> failureContentList;

    private List<NovelNetworkChapter> missingChapterList;

}
