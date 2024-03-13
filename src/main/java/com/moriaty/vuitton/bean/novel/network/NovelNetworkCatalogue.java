package com.moriaty.vuitton.bean.novel.network;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 网络小说目录
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午1:34
 */
@Data
@Accessors(chain = true)
public class NovelNetworkCatalogue {

    private NovelNetworkInfo info;

    private List<NovelNetworkChapter> chapterList;

}
