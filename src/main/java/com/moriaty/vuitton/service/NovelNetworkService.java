package com.moriaty.vuitton.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moriaty.vuitton.ServerInfo;
import com.moriaty.vuitton.bean.WrapMapperExt;
import com.moriaty.vuitton.bean.novel.local.NovelLocalFullInfo;
import com.moriaty.vuitton.bean.novel.network.*;
import com.moriaty.vuitton.bean.novel.network.req.*;
import com.moriaty.vuitton.bean.novel.network.resp.ActuatorSnapshotInfo;
import com.moriaty.vuitton.bean.novel.network.resp.ActuatorSnapshotInfos;
import com.moriaty.vuitton.bean.novel.network.resp.FixDownloadResp;
import com.moriaty.vuitton.constant.Constant;
import com.moriaty.vuitton.dao.mongo.impl.MongoNovelChapterContentImpl;
import com.moriaty.vuitton.dao.mongo.model.MongoNovelChapterContent;
import com.moriaty.vuitton.dao.mysql.mapper.ActuatorMapper;
import com.moriaty.vuitton.dao.mysql.mapper.NovelChapterMapper;
import com.moriaty.vuitton.dao.mysql.mapper.NovelMapper;
import com.moriaty.vuitton.dao.mysql.model.Novel;
import com.moriaty.vuitton.dao.mysql.model.NovelChapter;
import com.moriaty.vuitton.dao.mysql.model.Actuator;
import com.moriaty.vuitton.library.actuator.BaseActuator;
import com.moriaty.vuitton.library.actuator.ActuatorManager;
import com.moriaty.vuitton.library.actuator.ActuatorMeta;
import com.moriaty.vuitton.library.wrap.WrapMapper;
import com.moriaty.vuitton.library.wrap.Wrapper;
import com.moriaty.vuitton.module.novel.NovelLocalModule;
import com.moriaty.vuitton.module.novel.NovelNetworkModule;
import com.moriaty.vuitton.module.novel.actuator.NovelDownloadActuator;
import com.moriaty.vuitton.module.novel.actuator.NovelDownloadActuatorParam;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import com.moriaty.vuitton.util.FileServerUtil;
import com.moriaty.vuitton.util.NovelUtil;
import com.moriaty.vuitton.util.UuidUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * 网络小说 Service
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午12:24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NovelNetworkService {

    private final NovelCommonService novelCommonService;

    private final NovelNetworkModule novelNetworkModule;

    private final NovelLocalModule novelLocalModule;

    private final NovelMapper novelMapper;

    private final NovelChapterMapper novelChapterMapper;

    private final ActuatorMapper actuatorMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final MongoNovelChapterContentImpl mongoNovelChapterContentImpl;

    @Value("${file-server.novel.default-novel-img}")
    private String defaultNovelImg;

    public Wrapper<NovelNetworkCatalogue> findCatalogue(FindCatalogueReq req) {
        BaseNovelDownloader novelDownloader = NovelUtil.findNovelDownloader(req.getDownloaderMark());
        if (novelDownloader == null) {
            return WrapMapperExt.novelDownloaderNotExisted();
        }
        Optional<NovelNetworkCatalogue> optional = novelNetworkModule.findCatalogue(novelDownloader,
                req.getCatalogueUrl());
        return optional.map(WrapMapper::ok).orElseGet(() -> WrapMapper.failure("目录获取失败"));
    }

    public Wrapper<NovelNetworkContent> findContent(FindContentReq req) {
        BaseNovelDownloader novelDownloader = NovelUtil.findNovelDownloader(req.getDownloaderMark());
        if (novelDownloader == null) {
            return WrapMapperExt.novelDownloaderNotExisted();
        }
        Optional<NovelNetworkContent> optional = novelNetworkModule.
                findContent(novelDownloader, req.getTitle(), req.getContentUrl());
        return optional.map(WrapMapper::ok).orElseGet(() -> WrapMapper.failure("内容获取失败"));
    }


    public Wrapper<String> download(DownloadReq req) {
        BaseNovelDownloader novelDownloader = NovelUtil.findNovelDownloader(req.getDownloaderMark());
        if (novelDownloader == null) {
            return WrapMapperExt.novelDownloaderNotExisted();
        }
        Optional<NovelNetworkDownloadResult> optional = novelNetworkModule.
                download(novelDownloader, req.getName(), req.getCatalogueUrl(), req.isParallel());
        if (optional.isEmpty()) {
            return WrapMapper.failure("下载失败");
        }
        NovelNetworkDownloadResult downloadResult = optional.get();
        if (!req.isStorage()) {
            return WrapMapper.okStringData(downloadResult.getFile().getAbsolutePath());
        }
        String imgFileUrl = NovelUtil.uploadImg(ServerInfo.INFO.getFileServerUploadUrl(),
                downloadResult.getInfo().getImgUrl(), downloadResult.getInfo().getName());
        if (imgFileUrl == null) {
            return WrapMapper.failure("上传小说图片失败");
        }
        String fileUrl = NovelUtil.upload(ServerInfo.INFO.getFileServerUploadUrl(),
                downloadResult.getFile(), downloadResult.getInfo().getName() + ".txt");
        if (fileUrl == null) {
            return WrapMapper.failure("上传小说失败");
        }
        Novel novel = new Novel()
                .setName(downloadResult.getInfo().getName())
                .setAuthor(downloadResult.getInfo().getAuthor())
                .setIntro(downloadResult.getInfo().getIntro())
                .setImgUrl(imgFileUrl)
                .setFileUrl(fileUrl)
                .setDownloaderMark(downloadResult.getInfo().getDownloaderMark())
                .setDownloaderCatalogueUrl(downloadResult.getInfo().getDownloaderCatalogueUrl())
                .setCreateTime(LocalDateTime.now());
        int effectRow = novelMapper.insert(novel);
        if (effectRow != 1) {
            return WrapMapper.failure("存储失败");
        }
        List<NovelChapter> chapterList = downloadResult.getContentList().stream()
                .map(content -> {
                    MongoNovelChapterContent novelChapterContent = new MongoNovelChapterContent()
                            .setNovelName(novel.getName())
                            .setChapterTitle(content.getTitle())
                            .setContent(content.getContent())
                            .setContentHtml(content.getContentHtml());
                    mongoNovelChapterContentImpl.save(novelChapterContent);
                    return new NovelChapter()
                            .setNovel(novel.getId())
                            .setIndex(content.getIndex())
                            .setTitle(content.getTitle())
                            .setContentId(novelChapterContent.getId());
                })
                .toList();
        novelChapterMapper.batchInsert(chapterList);
        return WrapMapper.okStringData(ServerInfo.INFO.getFileServerUrl() + novel.getFileUrl());
    }

    public Wrapper<FixDownloadResp> fixDownload(FixDownloadReq req) {
        Optional<NovelLocalFullInfo> novelFullInfoOptional = novelCommonService.findFullInfo(req.getId());
        if (novelFullInfoOptional.isEmpty()) {
            return WrapMapper.failure("小说不存在");
        }
        NovelLocalFullInfo novelFullInfo = novelFullInfoOptional.get();
        Optional<NovelNetworkFixDownloadResult> optional =
                novelNetworkModule.fixDownload(novelFullInfo.getNovelDownloader(),
                        novelFullInfo.getNovel(), novelFullInfo.getChapterContentList(),
                        req.getFixNum() != null && req.getFixNum() >= 0 ? req.getFixNum() : -1);
        if (optional.isEmpty()) {
            return WrapMapper.failure("修补下载失败");
        }
        NovelNetworkFixDownloadResult fixDownloadResult = optional.get();
        if (!fixDownloadResult.getFixContentList().isEmpty()) {
            List<NovelChapter> chapterList = fixDownloadResult.getFixContentList().stream()
                    .map(content -> {
                        MongoNovelChapterContent novelChapterContent = new MongoNovelChapterContent()
                                .setNovelName(novelFullInfo.getNovel().getName())
                                .setChapterTitle(content.getTitle())
                                .setContent(content.getContent())
                                .setContentHtml(content.getContentHtml());
                        return new NovelChapter()
                                .setNovel(novelFullInfo.getNovel().getId())
                                .setIndex(content.getIndex())
                                .setTitle(content.getTitle())
                                .setContentId(novelChapterContent.getId());
                    })
                    .toList();
            novelChapterMapper.batchInsert(chapterList);
        }
        FixDownloadResp resp = new FixDownloadResp()
                .setFixContentList(fixDownloadResult.getFixContentList().stream()
                        .map(NovelNetworkContent::getTitle)
                        .toList())
                .setFailureContentList(fixDownloadResult.getFailureContentList().stream()
                        .map(NovelNetworkContent::getTitle)
                        .toList());

        Optional<List<Integer>> duplicatesOptional = novelLocalModule.removeDuplicates(novelChapterMapper
                .selectList(new LambdaQueryWrapper<NovelChapter>()
                        .eq(NovelChapter::getNovel, novelFullInfo.getNovel().getId())
                        .orderByAsc(NovelChapter::getIndex)));
        duplicatesOptional.ifPresent(duplicates -> {
            log.info("{} 去除重复章节 {}: {}", novelFullInfo.getNovel().getName(), duplicates.size(), duplicates);
            novelChapterMapper.deleteBatchIds(duplicates);
        });
        return WrapMapper.ok(resp);
    }

    public Wrapper<List<NovelNetworkChapter>> checkMissing(CheckMissingReq req) {
        Optional<NovelLocalFullInfo> novelFullInfoOptional = novelCommonService.findFullInfo(req.getId());
        if (novelFullInfoOptional.isEmpty()) {
            return WrapMapper.failure("小说不存在");
        }
        NovelLocalFullInfo novelFullInfo = novelFullInfoOptional.get();
        Optional<List<NovelNetworkChapter>> optional = novelNetworkModule.checkMissing(
                novelFullInfo.getNovelDownloader(), novelFullInfo.getNovel(), novelFullInfo.getChapterContentList());
        return optional.map(WrapMapper::ok).orElseGet(() -> WrapMapper.failure("检查缺失失败"));
    }

    public Wrapper<ActuatorMeta> actuatorDownload(ActuatorDownloadReq req) {
        BaseNovelDownloader novelDownloader = NovelUtil.findNovelDownloader(req.getDownloaderMark());
        if (novelDownloader == null) {
            return WrapMapperExt.novelDownloaderNotExisted();
        }
        String actuatorId = "Actuator-Novel-Download-" + UuidUtil.genId();
        NovelDownloadActuator actuator = new NovelDownloadActuator(new NovelDownloadActuatorParam(actuatorId, req.getName(), req.getCatalogueUrl(),
                req.isParallel(), stringRedisTemplate,
                Constant.Actuator.REDIS_PREFIX_ACTUATOR_NOVEL_DOWNLOAD + actuatorId + ":",
                Constant.Actuator.REDIS_TTL_ACTUATOR_NOVEL_DOWNLOAD, novelDownloader, defaultNovelImg,
                (novel, chapterContentList) -> {
                    novel.setCreateTime(LocalDateTime.now());
                    int effectRow = novelMapper.insert(novel);
                    if (effectRow != 1) {
                        return false;
                    }
                    List<NovelChapter> chapterList = chapterContentList.stream().map(chapter -> {
                        MongoNovelChapterContent content = chapter.getContent()
                                .setNovelName(novel.getName())
                                .setChapterTitle(chapter.getChapter().getTitle());
                        mongoNovelChapterContentImpl.save(content);
                        return chapter.getChapter().setNovel(novel.getId()).setContentId(content.getId());
                    }).toList();
                    novelChapterMapper.batchInsert(chapterList);
                    return true;
                },
                (actuatorSnapshot, stepData) -> {
                    String fileUrl = null;
                    File file = FileServerUtil.createTempFile(null, null);
                    if (file != null) {
                        try (FileWriter fileWriter = new FileWriter(file)) {
                            fileWriter.write(JSON.toJSONString(stepData));
                            fileWriter.flush();
                            fileUrl = FileServerUtil.uploadFile(ServerInfo.INFO.getFileServerUploadUrl(),
                                    file, actuatorSnapshot.getMeta().getId() + "-stepData", "actuator");
                        } catch (IOException e) {
                            log.error("写入执行器步骤数据异常", e);
                        }
                    }
                    actuatorMapper.insert(new Actuator()
                            .setId(actuatorSnapshot.getMeta().getId())
                            .setMeta(JSON.toJSONString(actuatorSnapshot.getMeta()))
                            .setStepList(JSON.toJSONString(actuatorSnapshot.getStepList()))
                            .setStepDataUrl(fileUrl != null ? fileUrl : "")
                            .setInterrupt(actuatorSnapshot.isInterrupt())
                            .setStartTime(actuatorSnapshot.getMeta().getStartTime())
                            .setEndTime(LocalDateTime.now())
                            .setResult(actuatorSnapshot.getResult()));
                }));
        ActuatorManager.runActuator(actuator);
        return WrapMapper.ok(actuator.getMeta());
    }

    public Wrapper<List<ActuatorSnapshotInfo>> actuatorSnapshot(ActuatorSnapshotReq req) {
        if (StringUtils.hasText(req.getId())) {
            BaseActuator actuator = ActuatorManager.getRunningActuator(req.getId());
            if (actuator != null) {
                return WrapMapper.ok(List.of(ActuatorSnapshotInfos.runningActuatorSnapshot(actuator.snapshot())));
            }
            Actuator storageActuator = actuatorMapper.selectById(req.getId());
            if (storageActuator == null) {
                return WrapMapper.ok(List.of());
            }
            return WrapMapper.ok(List.of(ActuatorSnapshotInfos.storageActuatorSnapshot(storageActuator)));
        }
        Map<String, BaseActuator> runningActuatorMap = ActuatorManager.snapshotRunningActuator();
        List<ActuatorSnapshotInfo> snapshotInfoList = new ArrayList<>();
        for (BaseActuator actuator : runningActuatorMap.values()) {
            snapshotInfoList.add(ActuatorSnapshotInfos.runningActuatorSnapshot(actuator.snapshot()));
        }
        List<Actuator> storageActuatorList =
                actuatorMapper.selectList(new LambdaQueryWrapper<Actuator>().orderByDesc(Actuator::getStartTime));
        for (Actuator actuator : storageActuatorList) {
            snapshotInfoList.add(ActuatorSnapshotInfos.storageActuatorSnapshot(actuator));
        }
        int actuatorNumLimit = 8;
        if (snapshotInfoList.size() > actuatorNumLimit) {
            snapshotInfoList = snapshotInfoList.subList(0, actuatorNumLimit);
        }
        return WrapMapper.ok(snapshotInfoList);
    }

    public Wrapper<Map<String, Map<String, Object>>> actuatorSnapshotStepData(ActuatorSnapshotStepDataReq req) {
        BaseActuator actuator = ActuatorManager.getRunningActuator(req.getId());
        if (actuator != null) {
            return WrapMapper.ok(actuator.snapshotStepData());
        }
        Actuator storageActuator = actuatorMapper.selectById(req.getId());
        if (storageActuator == null) {
            return WrapMapper.failure("执行器不存在");
        }
        String stepDataUrl = storageActuator.getStepDataUrl();
        if (!StringUtils.hasText(stepDataUrl)) {
            return WrapMapper.failure("执行器 snapshot 步骤数据不存在");
        }
        try {
            String stepDataStr = IOUtils.toString(
                    URI.create(ServerInfo.INFO.getFileServerUrl() + stepDataUrl).toURL(), StandardCharsets.UTF_8);
            Map<String, Map<String, Object>> stepData = JSON.parseObject(stepDataStr, new TypeReference<>() {
            });
            return WrapMapper.ok(stepData);
        } catch (IOException e) {
            log.error("获取执行器 snapshot 步骤数据异常", e);
            return WrapMapper.failure("获取执行器 snapshot 步骤数据失败");
        }
    }

    public Wrapper<Void> actuatorInterrupt(ActuatorSnapshotReq req) {
        BaseActuator actuator = ActuatorManager.getRunningActuator(req.getId());
        if (actuator == null) {
            return WrapMapper.failure("执行器不存在");
        }
        actuator.interrupt();
        return WrapMapper.ok();
    }

    public Wrapper<Void> actuatorDelete(ActuatorSnapshotReq req) {
        actuatorMapper.deleteById(req.getId());
        return WrapMapper.ok();
    }
}
