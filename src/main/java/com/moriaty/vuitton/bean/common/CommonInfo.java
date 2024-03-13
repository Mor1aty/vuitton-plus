package com.moriaty.vuitton.bean.common;

import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderMeta;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 通用信息
 * </p>
 *
 * @author Moriaty
 * @since 2023/12/8 1:26
 */
@Data
@Accessors(chain = true)
public class CommonInfo {

    private String fileServerUrl;

    private String fileServerUploadUrl;

    private List<NovelDownloaderMeta> novelDownloaderList;
}
