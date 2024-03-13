package com.moriaty.vuitton.module.novel.actuator.step;

import com.alibaba.fastjson2.TypeReference;
import com.moriaty.vuitton.bean.novel.network.*;
import com.moriaty.vuitton.dao.model.Novel;
import com.moriaty.vuitton.dao.model.NovelChapter;
import com.moriaty.vuitton.library.actuator.step.Step;
import com.moriaty.vuitton.library.actuator.step.StepMeta;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 小说下载步骤
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午5:45
 */
@Slf4j
public class DownloadStep extends Step {

    @Override
    protected StepMeta initMeta() {
        return new StepMeta().setName("下载小说");
    }

    @Override
    public String getProgress() {
        Integer totalChapterNum = super.getStepData("totalChapterNum", new TypeReference<>() {
        });
        if (totalChapterNum == null) {
            return "正在查询小说目录";
        }
        Integer currentChapterIndex = super.getStepData("currentChapterIndex", new TypeReference<>() {
        });
        return "正在下载: " + currentChapterIndex + "/" + totalChapterNum;
    }

    @Override
    public boolean runContent() {
        String novelName = super.getStepData("novelName", new TypeReference<>() {
        });
        String novelCatalogueUrl = super.getStepData("novelCatalogueUrl", new TypeReference<>() {
        });
        NovelDownloader novelDownloader = super.getStepData("novelDownloader", new TypeReference<>() {
        });
        NovelNetworkInfo info = novelDownloader.findInfo(novelCatalogueUrl);
        if (info == null) {
            log.error("{} 获取小说 {} 信息失败", novelDownloader.getMeta().getMark(), novelName);
            return false;
        }
        Novel novel = new Novel()
                .setName(info.getName())
                .setAuthor(info.getAuthor())
                .setIntro(info.getIntro())
                .setImgUrl(info.getImgUrl())
                .setDownloaderMark(info.getDownloaderMark())
                .setDownloaderCatalogueUrl(info.getDownloaderCatalogueUrl());
        super.putStepData("novel", novel);
        List<NovelNetworkChapter> chapterList = novelDownloader.findChapterList(novelCatalogueUrl);
        if (chapterList.isEmpty()) {
            log.error("{} 获取 {} 目录失败", novelDownloader.getMeta().getMark(), novelName);
            return false;
        }
        super.putStepData("totalChapterNum", chapterList.size());
        List<NovelChapter> downloadChapterList = new ArrayList<>();
        for (NovelNetworkChapter chapter : chapterList) {
            if (super.isInterrupt()) {
                log.error("下载小说章节被打断");
                break;
            }
            NovelNetworkContent content = novelDownloader.findContent(chapter.getTitle(),
                    chapter.getContentUrl());
            super.putStepData("currentChapterIndex", chapter.getIndex());
            if (content != null && !StringUtils.hasText(content.getErrorMsg())) {
                log.info("{} 获取 {} {}", novelDownloader.getMeta().getMark(), chapter.getIndex(), chapter.getTitle());
                downloadChapterList.add(new NovelChapter()
                        .setIndex(chapter.getIndex())
                        .setTitle(content.getTitle())
                        .setContent(content.getContent())
                        .setContentHtml(content.getContentHtml()));
            }
        }
        super.putStepData("chapterList", downloadChapterList);
        log.info("小说 {} 下载成功, 章节数: {}, 成功章节数: {}", novel.getName(),
                chapterList.size(), downloadChapterList.size());
        return true;
    }
}
