package com.moriaty.vuitton.bean.novel.network;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.List;

/**
 * <p>
 * 网络小说下载结果
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午4:24
 */
@Data
@Accessors(chain = true)
public class NovelNetworkDownloadResult {

    private NovelNetworkInfo info;

    private List<NovelNetworkContent> contentList;

    private File file;

}
