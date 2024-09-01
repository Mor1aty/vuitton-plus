package com.moriaty.vuitton.module.novel.actuator.step;

import com.alibaba.fastjson2.TypeReference;
import com.moriaty.vuitton.bean.novel.local.NovelChapterWithContent;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkChapter;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkContent;
import com.moriaty.vuitton.dao.mongo.model.MongoNovelChapterContent;
import com.moriaty.vuitton.dao.mysql.model.Novel;
import com.moriaty.vuitton.dao.mysql.model.NovelChapter;
import com.moriaty.vuitton.library.actuator.step.BaseRepeatStep;
import com.moriaty.vuitton.library.actuator.step.StepMeta;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import com.moriaty.vuitton.util.NovelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * 修补下载步骤
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午8:41
 */
@Slf4j
public class FixDownloadStep extends BaseRepeatStep {

    private Novel novel;

    private Boolean parallel = false;

    private final BaseNovelDownloader novelDownloader;

    private static final String KEY_FIX_DOWNLOAD_FAILURE = "fixDownloadFailure";

    private static final String KEY_MISSING_CHAPTER_NUM = "missingChapterNum";

    private static final String KEY_ALREADY_FIX_CHAPTER_NUM = "alreadyFixChapterNum";

    public FixDownloadStep(BaseNovelDownloader novelDownloader) {
        this.novelDownloader = novelDownloader;
    }

    @Override
    protected StepMeta initMeta() {
        return new StepMeta().setName("修补下载小说");
    }

    @Override
    public String getProgress() {
        Integer missingChapterNum = super.getStepData(KEY_MISSING_CHAPTER_NUM, new TypeReference<>() {
        });
        if (missingChapterNum == null) {
            return "正在检查缺失章节";
        }

        Integer alreadyFixChapterNum = super.getStepData(KEY_ALREADY_FIX_CHAPTER_NUM, new TypeReference<>() {
        });
        return "第" + super.repeatNum + "次修补, 缺失章节数: " + missingChapterNum
               + (alreadyFixChapterNum != null ? ", 已修复章节数: " + alreadyFixChapterNum : "");
    }

    @Override
    protected boolean isStopRepeat() {
        Boolean fixDownloadFailure = super.getStepData(KEY_FIX_DOWNLOAD_FAILURE, new TypeReference<>() {
        });
        if (fixDownloadFailure != null) {
            return true;
        }
        Integer missingChapterNum = super.getStepData(KEY_MISSING_CHAPTER_NUM, new TypeReference<>() {
        });
        return missingChapterNum != null && missingChapterNum == 0;
    }

//    @Override
//    protected void repeatRunContent() {
//
//        List<NovelChapterWithContent> chapterList = super.getStepData("chapterList", new TypeReference<>() {
//        });
//
//        NovelNetworkFixDownloadResult fixDownloadResult = novelDownloader.fixDownload(novel, chapterList, -1,
//                (missChapterNum) -> super.putStepData(KEY_MISSING_CHAPTER_NUM, missChapterNum));
//        if (fixDownloadResult == null) {
//            log.error("修补下载失败");
//            super.putStepData("fixDownloadFailure", true);
//            return;
//        }
//        super.putStepData(KEY_MISSING_CHAPTER_NUM, fixDownloadResult.getMissingChapterList().size());
//        if (!fixDownloadResult.getFixContentList().isEmpty()) {
//            chapterList.addAll(fixDownloadResult.getFixContentList().stream()
//                    .map(content -> new NovelChapterWithContent()
//                            .setChapter(new NovelChapter()
//                                    .setIndex(content.getIndex())
//                                    .setTitle(content.getTitle()))
//                            .setContent(new MongoNovelChapterContent()
//                                    .setContent(content.getContent())
//                                    .setContentHtml(content.getContentHtml()))
//                    ).toList());
//            chapterList.sort(Comparator.comparingInt(chapter -> chapter.getChapter().getIndex()));
//            super.putStepData("chapterList", chapterList);
//        }
//    }

    @Override
    protected void repeatRunContent() {

        List<NovelChapterWithContent> chapterList = super.getStepData("chapterList", new TypeReference<>() {
        });

        List<NovelNetworkChapter> missingChapterList = findMissingChapterList(chapterList);
        super.putStepData(KEY_MISSING_CHAPTER_NUM, missingChapterList.size());
        log.info("缺失小说章节: {}", missingChapterList.size());
        if (missingChapterList.isEmpty()) {
            log.info("缺失小说章节已修复");
            return;
        }
        List<NovelNetworkContent> fixContentList;
        if (Boolean.TRUE.equals(parallel)) {
            // 并行下载
            fixContentList = parallelFixDownload(missingChapterList);
        } else {
            // 串行下载
            fixContentList = serialFixDownload(missingChapterList);
        }
        log.info("缺失小说章节: {}, 已修复章节数: {}", missingChapterList.size(), fixContentList.size());
        if (!fixContentList.isEmpty()) {
            chapterList.addAll(fixContentList.stream()
                    .map(content -> new NovelChapterWithContent()
                            .setChapter(new NovelChapter()
                                    .setIndex(content.getIndex())
                                    .setTitle(content.getTitle()))
                            .setContent(new MongoNovelChapterContent()
                                    .setContent(content.getContent())
                                    .setContentHtml(content.getContentHtml()))
                    ).toList());
            chapterList.sort(Comparator.comparingInt(chapter -> chapter.getChapter().getIndex()));
            super.putStepData("chapterList", chapterList);
        }
    }

    private List<NovelNetworkChapter> findMissingChapterList(List<NovelChapterWithContent> existedChapterList) {
        List<Integer> existedIndexList = existedChapterList.stream().map(chapter -> chapter.getChapter().getIndex())
                .toList();
        List<NovelNetworkChapter> chapterList = novelDownloader.findChapterList(novel.getDownloaderCatalogueUrl());
        return chapterList.stream().filter(chapter -> !existedIndexList.contains(chapter.getIndex()))
                .toList();
    }

    private List<NovelNetworkContent> serialFixDownload(List<NovelNetworkChapter> missingChapterList) {
        List<NovelNetworkContent> fixContentList = new ArrayList<>();
        super.putStepData(KEY_ALREADY_FIX_CHAPTER_NUM, 0);
        for (int i = 0; i < missingChapterList.size(); i++) {
            NovelNetworkChapter missingChapter = missingChapterList.get(i);
            NovelNetworkContent downloadContent = novelDownloader.findContent(missingChapter.getTitle(),
                    missingChapter.getContentUrl());
            if (downloadContent != null && !StringUtils.hasText(downloadContent.getErrorMsg())) {
                log.info("修复下载小说 {}: [{}/{}] {} {}", novel.getName(), i + 1, missingChapterList.size(),
                        missingChapter.getIndex(), downloadContent.getTitle());
                downloadContent.setIndex(missingChapter.getIndex());
                fixContentList.add(downloadContent);
                super.putStepData(KEY_ALREADY_FIX_CHAPTER_NUM,
                        super.getStepData(KEY_ALREADY_FIX_CHAPTER_NUM, new TypeReference<Integer>() {
                        }) + 1);
            }
        }

        return fixContentList;
    }

    private List<NovelNetworkContent> parallelFixDownload(List<NovelNetworkChapter> missingChapterList) {
        Map<Integer, NovelNetworkContent> fixContentMap = new HashMap<>();
        NovelUtil.parallelOperation("actuator-novel-downloader-step-fix-download", missingChapterList.size(),
                (index) -> {
                    NovelNetworkChapter chapter = missingChapterList.get(index);
                    NovelNetworkContent content = novelDownloader.findContent(chapter.getTitle(),
                            chapter.getContentUrl());
                    if (content != null && !StringUtils.hasText(content.getErrorMsg())) {
                        log.info("{} 并行修复下载 {} {}", novelDownloader.getMeta().getMark(), chapter.getIndex(),
                                chapter.getTitle());
                        fixContentMap.put(chapter.getIndex(), content);
                    }
                });
        return fixContentMap.values().stream().toList();
    }

    @Override
    protected int initRepeatSleepSecond() {
        return 5;
    }

    @Override
    protected void beforeRun() {
        novel = super.getStepData("novel", new TypeReference<>() {
        });
        parallel = super.getStepData("parallel", new TypeReference<>() {
        });
    }
}
