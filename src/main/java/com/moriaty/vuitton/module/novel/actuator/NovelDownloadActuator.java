package com.moriaty.vuitton.module.novel.actuator;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.moriaty.vuitton.constant.Constant;
import com.moriaty.vuitton.dao.model.Novel;
import com.moriaty.vuitton.dao.model.NovelChapter;
import com.moriaty.vuitton.library.actuator.Actuator;
import com.moriaty.vuitton.library.actuator.ActuatorMeta;
import com.moriaty.vuitton.library.actuator.ActuatorSnapshot;
import com.moriaty.vuitton.library.actuator.plugin.RedisStepDataPlugin;
import com.moriaty.vuitton.library.actuator.plugin.StepDataPlugin;
import com.moriaty.vuitton.library.actuator.step.Step;
import com.moriaty.vuitton.module.novel.actuator.step.DownloadStep;
import com.moriaty.vuitton.module.novel.actuator.step.FixDownloadStep;
import com.moriaty.vuitton.module.novel.actuator.step.StorageStep;
import com.moriaty.vuitton.module.novel.downloader.NovelDownloader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
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

    private final String id;

    private final String novelName;

    private final String novelCatalogueUrl;

    private final boolean parallel;

    private final StringRedisTemplate stringRedisTemplate;

    private final String redisKeyPrefix;

    private final Duration redisTtl;

    private final NovelDownloader novelDownloader;

    private final BiPredicate<Novel, List<NovelChapter>> storageStepStrategy;

    private final BiConsumer<ActuatorSnapshot, Map<String, Map<String, Object>>> beforeEndStrategy;

    public NovelDownloadActuator(String id, String novelName, String novelCatalogueUrl, boolean parallel,
                                 StringRedisTemplate stringRedisTemplate, String redisKeyPrefix, Duration redisTtl,
                                 NovelDownloader novelDownloader, String defaultNovelImg,
                                 BiPredicate<Novel, List<NovelChapter>> storageStepStrategy,
                                 BiConsumer<ActuatorSnapshot, Map<String, Map<String, Object>>> beforeEndStrategy) {
        this.id = id;
        this.novelName = novelName;
        this.novelCatalogueUrl = novelCatalogueUrl;
        this.parallel = parallel;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisKeyPrefix = redisKeyPrefix;
        this.redisTtl = redisTtl;
        this.novelDownloader = novelDownloader;
        this.storageStepStrategy = storageStepStrategy;
        this.beforeEndStrategy = beforeEndStrategy;
        super.init();
        initStepData(Map.of("novelName", novelName, "novelCatalogueUrl", novelCatalogueUrl,
                "parallel", parallel, "redisTtl", Constant.Actuator.REDIS_TTL_ACTUATOR_NOVEL_DOWNLOAD,
                "defaultNovelImg", defaultNovelImg));
    }

    @Override
    protected ActuatorMeta initMeta() {
        return new NovelDownloadActuatorMeta(id, "小说下载器", this.novelName, this.parallel,
                this.novelCatalogueUrl, this.novelDownloader.getMeta());
    }

    @Override
    protected List<Step> initStep() {
        return List.of(
                new DownloadStep(novelDownloader),
                new FixDownloadStep(novelDownloader),
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
        super.stepDataPlugin.snapshotStepData().forEach((step, data) -> {
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
            List<NovelChapter> novelChapterList = JSON.parseObject(JSON.toJSONString(list), new TypeReference<>() {
            });
            chapterList = novelChapterList.stream().map(chapter -> new NovelChapter()
                    .setIndex(chapter.getIndex())
                    .setTitle(chapter.getTitle())).toList();
            currentData.put("chapterList", chapterList);
        }
        snapshot.put(getProgress().getCurrentStepIndex() + "-" + currentStep.getMeta().getName(), currentData);
        return snapshot;
    }

    @Override
    protected StepDataPlugin loadStepDataPlugin() {
        return new RedisStepDataPlugin(stringRedisTemplate, redisKeyPrefix, redisTtl);
    }
}
