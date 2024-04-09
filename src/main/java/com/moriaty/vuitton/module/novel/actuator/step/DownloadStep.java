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

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

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

    private final NovelDownloader novelDownloader;

    public DownloadStep(NovelDownloader novelDownloader) {
        this.novelDownloader = novelDownloader;
    }

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
        Boolean parallel = super.getStepData("parallel", new TypeReference<>() {
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
        List<NovelChapter> downloadChapterList;
        if (Boolean.TRUE.equals(parallel)) {
            // 并行下载
            downloadChapterList = parallelDownload(novelDownloader, chapterList);
        } else {
            // 串行下载
            downloadChapterList = serialDownload(novelDownloader, chapterList);
        }
        super.putStepData("chapterList", downloadChapterList);
        log.info("小说 {} 下载成功, 章节数: {}, 成功章节数: {}", novel.getName(),
                chapterList.size(), downloadChapterList.size());
        return true;
    }

    private List<NovelChapter> serialDownload(NovelDownloader novelDownloader, List<NovelNetworkChapter> chapterList) {
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
                log.info("{} 串行下载 {} {}", novelDownloader.getMeta().getMark(), chapter.getIndex(), chapter.getTitle());
                downloadChapterList.add(new NovelChapter()
                        .setIndex(chapter.getIndex())
                        .setTitle(content.getTitle())
                        .setContent(content.getContent())
                        .setContentHtml(content.getContentHtml()));
            }
        }
        return downloadChapterList;
    }

    private List<NovelChapter> parallelDownload(NovelDownloader novelDownloader, List<NovelNetworkChapter> chapterList) {
        List<NovelChapter> downloadChapterList = new ArrayList<>();
        Map<Integer, NovelNetworkContent> contentMap = new TreeMap<>();
        try {
            ThreadFactory factory = Thread.ofVirtual().name("actuator-novel-downloader", 0).factory();
            CountDownLatch countDownLatch = new CountDownLatch(chapterList.size());
            log.info("共 {} 章, 开启虚拟线程: {}", chapterList.size(), countDownLatch.getCount());
            for (int i = 0; i < chapterList.size(); i++) {
                int index = i;
                int sleepSecond = i % 10;
                factory.newThread(() -> {
                    try {
                        Thread.sleep(Duration.ofSeconds(sleepSecond));
                    } catch (InterruptedException e) {
                        log.error("sleep is interrupted", e);
                        Thread.currentThread().interrupt();
                    }
                    NovelNetworkChapter chapter = chapterList.get(index);
                    NovelNetworkContent content = novelDownloader.findContent(chapter.getTitle(), chapter.getContentUrl());

                    if (content != null && !StringUtils.hasText(content.getErrorMsg())) {
                        log.info("{} 并行下载 {} {}", novelDownloader.getMeta().getMark(), chapter.getIndex(), chapter.getTitle());
                        contentMap.put(chapter.getIndex(), content);
                    }
                    countDownLatch.countDown();
                }).start();
            }
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("下载小说被打断", e);
            Thread.currentThread().interrupt();
        }
        log.info("共下载章节: {}", contentMap.size());
        if (!contentMap.isEmpty()) {
            contentMap.forEach((chapterIndex, content) -> downloadChapterList.add(new NovelChapter()
                    .setIndex(chapterIndex)
                    .setTitle(content.getTitle())
                    .setContent(content.getContent())
                    .setContentHtml(content.getContentHtml())));
        }
        return downloadChapterList;
    }
}
