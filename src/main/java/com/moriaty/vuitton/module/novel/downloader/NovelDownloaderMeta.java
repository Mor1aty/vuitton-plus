package com.moriaty.vuitton.module.novel.downloader;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 小说下载器信息
 * </p>
 *
 * @author Moriaty
 * @since 2023/11/17 4:53
 */
@Data
@Accessors(chain = true)
public class NovelDownloaderMeta {

    private String webName;

    private String website;

    private String contentBaseUrl;

    private String catalogueBaseUrl;

    private String mark;

    private String charset;

    private Boolean disable;
}
