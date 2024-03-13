package com.moriaty.vuitton.module.novel.downloader;

import com.moriaty.vuitton.bean.novel.network.*;
import com.moriaty.vuitton.constant.Constant;
import com.moriaty.vuitton.dao.model.Novel;
import com.moriaty.vuitton.dao.model.NovelChapter;
import com.moriaty.vuitton.util.NovelUtil;
import com.moriaty.vuitton.util.RandomUtil;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

/**
 * <p>
 * 小说下载器
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午12:26
 */
@Slf4j
public abstract class NovelDownloader {

    public abstract NovelDownloaderMeta getMeta();

    public abstract NovelNetworkInfo findInfo(String catalogueUrl);

    public abstract List<NovelNetworkChapter> findChapterList(String catalogueUrl);

    public abstract NovelNetworkContent findContent(String title, String contentUrl);

    public abstract boolean skipContent(String content);

    public abstract String removeAbnormalContent(String content);

    protected List<NovelNetworkChapter> exploreChapterList(Elements domList, @Nonnull String contentPrefix) {
        List<NovelNetworkChapter> chapterList = new ArrayList<>();
        for (int i = 0; i < domList.size(); i++) {
            Element domDd = domList.get(i);
            Elements domA = domDd.getElementsByTag("a");
            if (domA.isEmpty()) {
                log.warn("章节 {} 获取失败", domDd);
                continue;
            }
            chapterList.add(new NovelNetworkChapter()
                    .setIndex(i)
                    .setTitle(domA.text())
                    .setContentUrl(contentPrefix + domA.attr("href")));
        }
        return chapterList;
    }

    protected NovelNetworkContent exploreContent(String title, String contentUrl, String contentDomId) {
        try {
            Thread.sleep(Duration.ofSeconds(RandomUtil.randomInt(0, 2)));
        } catch (InterruptedException e) {
            log.error("休眠被打断", e);
            Thread.currentThread().interrupt();
        }
        try {
            Document doc = Jsoup.connect(contentUrl)
                    .timeout(Constant.Network.CONNECT_TIMEOUT)
                    .headers(Constant.Network.CHROME_HEADERS)
                    .ignoreHttpErrors(true)
                    .get();
            Element content = doc.getElementById(contentDomId);
            if (content == null) {
                return new NovelNetworkContent()
                        .setErrorMsg("正文不存在");
            }
            if (skipContent(content.text())) {
                return new NovelNetworkContent()
                        .setErrorMsg("本章未更新");
            }
            return new NovelNetworkContent()
                    .setTitle(title)
                    .setContent(removeAbnormalContent(content.text()))
                    .setContentHtml(removeAbnormalContent(content.html()));
        } catch (IOException e) {
            return new NovelNetworkContent()
                    .setErrorMsg("获取小说内容发生异常, " + e.getLocalizedMessage());
        }
    }

    private NovelNetworkDownloadResult writeInfo(String name, String catalogueUrl) {
        try {
            NovelNetworkInfo info = findInfo(catalogueUrl);
            if (info == null) {
                return null;
            }
            info.setName(name);
            File file = NovelUtil.writeInfoToFile(name, info.getAuthor(), info.getIntro());
            return new NovelNetworkDownloadResult()
                    .setInfo(info)
                    .setFile(file);
        } catch (IOException e) {
            log.error("写入小说信息异常", e);
            return null;
        }
    }

    private List<NovelNetworkContent> execDownload(String name, File file, List<NovelNetworkChapter> chapterList,
                                                   Map<Integer, NovelNetworkContent> contentMap) {
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            List<NovelNetworkContent> contentList = new ArrayList<>(chapterList.size());
            for (int i = 0; i < chapterList.size(); i++) {
                NovelNetworkChapter chapter = chapterList.get(i);
                NovelNetworkContent content = contentMap == null ?
                        findContent(chapter.getTitle(), chapter.getContentUrl()) : contentMap.get(chapter.getIndex());
                int index = i + 1;
                boolean isNormal = NovelUtil.writeContentToFile(fileWriter, index, content);
                if (isNormal) {
                    content.setIndex(chapter.getIndex());
                    contentList.add(content);
                }
            }
            log.info("{} 写入完成", name);
            return contentList;
        } catch (IOException e) {
            log.error("下载小说异常", e);
            return Collections.emptyList();
        }
    }

    public NovelNetworkDownloadResult serialDownload(String name, String catalogueUrl) {
        NovelNetworkDownloadResult result = writeInfo(name, catalogueUrl);
        if (result == null) {
            log.error("下载小说 {} 失败", name);
            return null;
        }
        List<NovelNetworkChapter> chapterList = findChapterList(catalogueUrl);
        List<NovelNetworkContent> contentList = execDownload(name, result.getFile(), chapterList, null);
        result.setContentList(contentList);
        return result;
    }

    public NovelNetworkDownloadResult parallelDownload(String name, String catalogueUrl) {
        NovelNetworkDownloadResult result = writeInfo(name, catalogueUrl);
        if (result == null) {
            log.error("下载小说 {} 失败", name);
            return null;
        }
        try {
            List<NovelNetworkChapter> chapterList = findChapterList(catalogueUrl);
            Map<Integer, NovelNetworkContent> contentMap = HashMap.newHashMap(chapterList.size());
            List<NovelNetworkContent> errorContentList = new ArrayList<>();
            ThreadFactory factory = Thread.ofVirtual().name("novel-downloader", 0).factory();
            CountDownLatch countDownLatch = new CountDownLatch(chapterList.size());
            log.info("共 {} 章, 开启虚拟线程: {}", countDownLatch.getCount(), countDownLatch.getCount());
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
                    NovelNetworkContent content = findContent(chapter.getTitle(), chapter.getContentUrl());
                    contentMap.put(chapter.getIndex(), content);
                    if (StringUtils.hasText(content.getErrorMsg())) {
                        errorContentList.add(content);
                    }
                    countDownLatch.countDown();
                }).start();
            }
            countDownLatch.await();
            log.info("章节: {}", contentMap.size());
            log.info("错误章节: {}", errorContentList.size());
            List<NovelNetworkContent> contentList = execDownload(name, result.getFile(), chapterList, contentMap);
            result.setContentList(contentList);
            return result;
        } catch (InterruptedException e) {
            log.error("下载小说被打断", e);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public NovelNetworkFixDownloadResult fixDownload(Novel novel, List<NovelChapter> existedChapterList, int fixNum) {
        List<NovelNetworkContent> fixContentList = new ArrayList<>();
        List<NovelNetworkContent> failureContentList = new ArrayList<>();
        List<Integer> existedIndexList = existedChapterList.stream().map(NovelChapter::getIndex).toList();
        List<NovelNetworkChapter> chapterList = findChapterList(novel.getDownloaderCatalogueUrl());
        List<NovelNetworkChapter> missingChapterList = chapterList.stream()
                .filter(chapter -> !existedIndexList.contains(chapter.getIndex()))
                .toList();
        int needFixNum = fixNum == -1 ? missingChapterList.size() : Math.min(missingChapterList.size(), fixNum);
        log.info("缺失小说章节: {}, 需要修复章节数: {}", missingChapterList.size(), needFixNum);
        for (int i = 0; i < missingChapterList.size(); i++) {
            if (fixNum != -1 && i >= fixNum) {
                break;
            }
            NovelNetworkChapter missingChapter = missingChapterList.get(i);
            NovelNetworkContent downloadContent = findContent(missingChapter.getTitle(),
                    missingChapter.getContentUrl());
            if (downloadContent != null && !StringUtils.hasText(downloadContent.getErrorMsg())) {
                log.info("修复下载小说 {}: [{}/{}] {} {}", novel.getName(), i + 1, missingChapterList.size(),
                        missingChapter.getIndex(), downloadContent.getTitle());
                downloadContent.setIndex(missingChapter.getIndex());
                fixContentList.add(downloadContent);
            } else {
                failureContentList.add(downloadContent != null ?
                        downloadContent.setTitle(missingChapter.getTitle()) :
                        new NovelNetworkContent().setTitle(missingChapter.getTitle()));
            }
        }
        log.info("缺失小说章节: {}, 需要修复章节数: {}, 已修复章节数: {}", missingChapterList.size(),
                needFixNum, fixContentList.size());
        return new NovelNetworkFixDownloadResult()
                .setFixContentList(fixContentList)
                .setFailureContentList(failureContentList)
                .setMissingChapterList(missingChapterList);
    }

}
