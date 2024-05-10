package com.moriaty.vuitton.module.novel.actuator.step;

import com.alibaba.fastjson2.TypeReference;
import com.moriaty.vuitton.bean.novel.local.NovelChapterWithContent;
import com.moriaty.vuitton.bean.novel.network.NovelNetworkFixDownloadResult;
import com.moriaty.vuitton.dao.mongo.model.MongoNovelChapterContent;
import com.moriaty.vuitton.dao.mysql.model.Novel;
import com.moriaty.vuitton.dao.mysql.model.NovelChapter;
import com.moriaty.vuitton.library.actuator.step.BaseRepeatStep;
import com.moriaty.vuitton.library.actuator.step.StepMeta;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;

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

    private final BaseNovelDownloader novelDownloader;

    private static final String KEY_MISSING_CHAPTER_NUM = "missingChapterNum";

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
        return "缺失章节数: " + missingChapterNum;
    }

    @Override
    protected boolean isStopRepeat() {
        Boolean fixDownloadFailure = super.getStepData("fixDownloadFailure", new TypeReference<>() {
        });
        if (fixDownloadFailure != null) {
            return true;
        }
        Integer missingChapterNum = super.getStepData(KEY_MISSING_CHAPTER_NUM, new TypeReference<>() {
        });
        return missingChapterNum != null && missingChapterNum == 0;
    }

    @Override
    protected void repeatRunContent() {

        List<NovelChapterWithContent> chapterList = super.getStepData("chapterList", new TypeReference<>() {
        });

        NovelNetworkFixDownloadResult fixDownloadResult = novelDownloader.fixDownload(novel, chapterList, -1,
                (missChapterNum) -> super.putStepData(KEY_MISSING_CHAPTER_NUM, missChapterNum));
        if (fixDownloadResult == null) {
            log.error("修补下载失败");
            super.putStepData("fixDownloadFailure", true);
            return;
        }
        super.putStepData(KEY_MISSING_CHAPTER_NUM, fixDownloadResult.getMissingChapterList().size());
        if (!fixDownloadResult.getFixContentList().isEmpty()) {
            chapterList.addAll(fixDownloadResult.getFixContentList().stream()
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

    @Override
    protected int initRepeatSleepSecond() {
        return 5;
    }

    @Override
    protected void beforeRun() {
        novel = super.getStepData("novel", new TypeReference<>() {
        });
    }
}
