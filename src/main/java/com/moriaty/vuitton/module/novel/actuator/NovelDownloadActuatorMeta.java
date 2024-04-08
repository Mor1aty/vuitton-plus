package com.moriaty.vuitton.module.novel.actuator;

import com.moriaty.vuitton.library.actuator.ActuatorMeta;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderMeta;
import lombok.Getter;

/**
 * <p>
 * 小说下载执行器元数据
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/1 下午9:02
 */
@Getter
public class NovelDownloadActuatorMeta extends ActuatorMeta {

    private final String novelName;

    private final String novelCatalogueUrl;

    private final boolean parallel;

    private final NovelDownloaderMeta novelDownloaderMeta;

    public NovelDownloadActuatorMeta(String id, String name, String novelName, boolean parallel,
                                     String novelCatalogueUrl, NovelDownloaderMeta novelDownloaderMeta) {
        super(id, name);
        this.novelName = novelName;
        this.novelCatalogueUrl = novelCatalogueUrl;
        this.parallel = parallel;
        this.novelDownloaderMeta = novelDownloaderMeta;
    }
}
