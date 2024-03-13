package com.moriaty.vuitton.bean.novel.network;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 网络小说章节
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午12:28
 */
@Data
@Accessors(chain = true)
public class NovelNetworkChapter {

    private int index;

    private String title;

    private String contentUrl;
}
