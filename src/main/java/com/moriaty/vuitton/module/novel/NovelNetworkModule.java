package com.moriaty.vuitton.module.novel;

import com.moriaty.vuitton.bean.novel.local.NovelChapterWithContent;
import com.moriaty.vuitton.bean.novel.network.*;
import com.moriaty.vuitton.dao.mysql.model.Novel;
import com.moriaty.vuitton.module.Module;
import com.moriaty.vuitton.module.ModuleFactory;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 网络小说模块
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 上午11:31
 */
@Component
@Slf4j
public class NovelNetworkModule implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        ModuleFactory.addModule(new Module()
                .setId(2)
                .setName("网络小说"));
    }

    public Optional<NovelNetworkCatalogue> findCatalogue(@Nonnull BaseNovelDownloader novelDownloader, String catalogueUrl) {
        NovelNetworkInfo info = novelDownloader.findInfo(catalogueUrl);
        if (info == null) {
            log.error("{} 获取 {} 信息失败", novelDownloader.getMeta().getMark(), catalogueUrl);
            return Optional.empty();
        }
        List<NovelNetworkChapter> chapterList = novelDownloader.findChapterList(catalogueUrl);
        if (chapterList.isEmpty()) {
            log.error("{} 获取 {} 目录失败", novelDownloader.getMeta().getMark(), catalogueUrl);
            return Optional.empty();
        }
        return Optional.of(new NovelNetworkCatalogue()
                .setInfo(info)
                .setChapterList(chapterList));
    }

    public Optional<NovelNetworkContent> findContent(@Nonnull BaseNovelDownloader novelDownloader,
                                                     String title, String contentUrl) {
        NovelNetworkContent content = novelDownloader.findContent(title, contentUrl);
        return Optional.of(content);
    }

    public Optional<NovelNetworkDownloadResult> download(@Nonnull BaseNovelDownloader novelDownloader,
                                                         String name, String catalogueUrl, boolean parallel) {
        NovelNetworkDownloadResult result = parallel ? novelDownloader.parallelDownload(name, catalogueUrl)
                : novelDownloader.serialDownload(name, catalogueUrl);
        if (result == null) {
            log.error("下载小说失败");
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public Optional<NovelNetworkFixDownloadResult> fixDownload(@Nonnull BaseNovelDownloader novelDownloader, Novel novel,
                                                               List<NovelChapterWithContent> existedChapterList, int fixNum) {
        NovelNetworkFixDownloadResult result = novelDownloader.fixDownload(novel, existedChapterList, fixNum);
        if (result == null) {
            log.error("修补下载小说失败");
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public Optional<List<NovelNetworkChapter>> checkMissing(@Nonnull BaseNovelDownloader novelDownloader, Novel novel,
                                                            List<NovelChapterWithContent> existedChapterList) {
        List<Integer> existedIndexList = existedChapterList.stream().map(chapter -> chapter.getChapter().getIndex())
                .toList();
        List<NovelNetworkChapter> chapterList = novelDownloader.findChapterList(novel.getDownloaderCatalogueUrl());
        List<NovelNetworkChapter> missingChapterList = chapterList.stream()
                .filter(chapter -> !existedIndexList.contains(chapter.getIndex()))
                .toList();
        return Optional.of(missingChapterList);
    }
}
