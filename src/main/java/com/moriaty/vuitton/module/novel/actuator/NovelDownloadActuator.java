package com.moriaty.vuitton.module.novel.actuator;

import com.moriaty.vuitton.bean.novel.local.NovelChapterWithContent;
import com.moriaty.vuitton.constant.Constant;
import com.moriaty.vuitton.dao.mysql.model.Novel;
import com.moriaty.vuitton.library.actuator.BaseActuator;
import com.moriaty.vuitton.library.actuator.ActuatorMeta;
import com.moriaty.vuitton.library.actuator.ActuatorSnapshot;
import com.moriaty.vuitton.library.actuator.plugin.RedisStepDataPlugin;
import com.moriaty.vuitton.library.actuator.plugin.StepDataPlugin;
import com.moriaty.vuitton.library.actuator.step.BaseStep;
import com.moriaty.vuitton.module.novel.actuator.step.DownloadStep;
import com.moriaty.vuitton.module.novel.actuator.step.FixDownloadStep;
import com.moriaty.vuitton.module.novel.actuator.step.StorageStep;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
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
public class NovelDownloadActuator extends BaseActuator {

    private final String id;

    private final String novelName;

    private final String novelCatalogueUrl;

    private final boolean parallel;

    private final StringRedisTemplate stringRedisTemplate;

    private final String redisKeyPrefix;

    private final Duration redisTtl;

    private final BaseNovelDownloader novelDownloader;

    private final BiPredicate<Novel, List<NovelChapterWithContent>> storageStepStrategy;

    private final BiConsumer<ActuatorSnapshot, Map<String, Map<String, Object>>> beforeEndStrategy;

    public NovelDownloadActuator(NovelDownloadActuatorParam param) {
        this.id = param.id();
        this.novelName = param.novelName();
        this.novelCatalogueUrl = param.novelCatalogueUrl();
        this.parallel = param.parallel();
        this.stringRedisTemplate = param.stringRedisTemplate();
        this.redisKeyPrefix = param.redisKeyPrefix();
        this.redisTtl = param.redisTtl();
        this.novelDownloader = param.novelDownloader();
        this.storageStepStrategy = param.storageStepStrategy();
        this.beforeEndStrategy = param.beforeEndStrategy();
        super.init();
        initStepData(Map.of("novelName", novelName, "novelCatalogueUrl", novelCatalogueUrl,
                "parallel", parallel, "redisTtl", Constant.Actuator.REDIS_TTL_ACTUATOR_NOVEL_DOWNLOAD,
                "defaultNovelImg", param.defaultNovelImg()));
    }

    @Override
    protected ActuatorMeta initMeta() {
        return new NovelDownloadActuatorMeta(id, "小说下载器", this.novelName, this.parallel,
                this.novelCatalogueUrl, this.novelDownloader.getMeta());
    }

    @Override
    protected List<BaseStep> initStep() {
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
        Map<String, Map<String, Object>> snapshot = new HashMap<>(super.stepDataPlugin.snapshotStepData());
        Map<String, Object> currentData = currentStep.snapshotStepData();
        snapshot.put(getProgress().getCurrentStepIndex() + "-" + currentStep.getMeta().getName(), currentData);
        return snapshot;
    }

    @Override
    protected StepDataPlugin loadStepDataPlugin() {
        return new RedisStepDataPlugin(stringRedisTemplate, redisKeyPrefix, redisTtl);
    }
}
