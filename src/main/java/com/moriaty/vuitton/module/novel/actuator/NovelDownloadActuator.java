package com.moriaty.vuitton.module.novel.actuator;

import com.alibaba.fastjson2.TypeReference;
import com.moriaty.vuitton.dao.model.Novel;
import com.moriaty.vuitton.dao.model.NovelChapter;
import com.moriaty.vuitton.library.actuator.Actuator;
import com.moriaty.vuitton.library.actuator.ActuatorMeta;
import com.moriaty.vuitton.library.actuator.ActuatorSnapshot;
import com.moriaty.vuitton.library.actuator.step.Step;
import com.moriaty.vuitton.module.novel.actuator.step.DownloadStep;
import com.moriaty.vuitton.module.novel.actuator.step.FixDownloadStep;
import com.moriaty.vuitton.module.novel.actuator.step.StorageStep;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloader;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloaderMeta;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * <p>
 * 小说下载执行器
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午5:27
 */
@Slf4j
public class NovelDownloadActuator extends Actuator {

    private final String novelName;

    private final String novelCatalogueUrl;

    private final NovelDownloaderMeta novelDownloaderMeta;

    private final BiPredicate<Novel, List<NovelChapter>> storageStepStrategy;

    private final BiConsumer<ActuatorSnapshot, Map<String, Map<String, Object>>> beforeEndStrategy;

    public NovelDownloadActuator(String novelName, String novelCatalogueUrl, NovelDownloader novelDownloader,
                                 BiPredicate<Novel, List<NovelChapter>> storageStepStrategy,
                                 BiConsumer<ActuatorSnapshot, Map<String, Map<String, Object>>> beforeEndStrategy) {
        this.novelName = novelName;
        this.novelCatalogueUrl = novelCatalogueUrl;
        this.novelDownloaderMeta = novelDownloader.getMeta();
        this.storageStepStrategy = storageStepStrategy;
        this.beforeEndStrategy = beforeEndStrategy;
        initStepData(Map.of("novelName", novelName, "novelCatalogueUrl", novelCatalogueUrl,
                "novelDownloader", novelDownloader));
        super.init();
    }

    @Override
    protected ActuatorMeta initMeta() {
        return new NovelDownloadActuatorMeta("小说下载器", this.novelName,
                this.novelCatalogueUrl, this.novelDownloaderMeta);
    }

    @Override
    protected List<Step> initStep() {
        return List.of(
                new DownloadStep(),
                new FixDownloadStep(),
                new StorageStep(storageStepStrategy));
    }

    @Override
    protected void beforeEnd() {
        beforeEndStrategy.accept(snapshot(), snapshotStepData());
        super.beforeEnd();
    }

    @Override
    public Map<String, Map<String, Object>> snapshotStepData() {
        HashMap<String, Map<String, Object>> snapshot = new HashMap<>();
        super.stepDataMap.forEach((step, data) -> {
            List<NovelChapter> chapterList = data.getObject("chapterList", new TypeReference<>() {
            });
            if (chapterList != null) {
                chapterList = chapterList.stream().map(chapter -> new NovelChapter()
                        .setIndex(chapter.getIndex())
                        .setTitle(chapter.getTitle())).toList();
                data.put("chapterList", chapterList);
            }
            snapshot.put(step, data);
        });
        Map<String, Object> currentData = currentStep.snapshotStepData();
        Object chapterList = currentData.get("chapterList");
        if (chapterList instanceof List<?> list) {
            chapterList = list.stream().map(chapter -> {
                if (chapter instanceof NovelChapter c) {
                    return new NovelChapter()
                            .setIndex(c.getIndex())
                            .setTitle(c.getTitle());
                } else {
                    return null;
                }
            }).toList();
            currentData.put("chapterList", chapterList);
        }
        snapshot.put(getProgress().getCurrentStepIndex() + "-" + currentStep.getMeta().getName(), currentData);
        return snapshot;
    }
}
