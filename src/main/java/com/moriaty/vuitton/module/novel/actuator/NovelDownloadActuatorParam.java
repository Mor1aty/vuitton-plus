package com.moriaty.vuitton.module.novel.actuator;

import com.moriaty.vuitton.dao.model.Novel;
import com.moriaty.vuitton.dao.model.NovelChapter;
import com.moriaty.vuitton.library.actuator.ActuatorSnapshot;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * <p>
 * 小说下载执行器参数
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/10 下午7:15
 */
public record NovelDownloadActuatorParam(String id, String novelName, String novelCatalogueUrl, boolean parallel,
                                         StringRedisTemplate stringRedisTemplate, String redisKeyPrefix,
                                         Duration redisTtl,
                                         BaseNovelDownloader novelDownloader, String defaultNovelImg,
                                         BiPredicate<Novel, List<NovelChapter>> storageStepStrategy,
                                         BiConsumer<ActuatorSnapshot,
                                                 Map<String, Map<String, Object>>> beforeEndStrategy) {
}
