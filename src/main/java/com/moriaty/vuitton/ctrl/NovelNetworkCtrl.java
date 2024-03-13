package com.moriaty.vuitton.ctrl;

import com.moriaty.vuitton.bean.novel.network.*;
import com.moriaty.vuitton.bean.novel.network.req.*;
import com.moriaty.vuitton.bean.novel.network.resp.ActuatorSnapshotInfo;
import com.moriaty.vuitton.bean.novel.network.resp.FixDownloadResp;
import com.moriaty.vuitton.library.actuator.ActuatorMeta;
import com.moriaty.vuitton.library.wrap.Wrapper;
import com.moriaty.vuitton.service.NovelNetworkService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 网络小说 Ctrl
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午12:23
 */
@RestController
@RequestMapping("novel/network")
@AllArgsConstructor
@Slf4j
public class NovelNetworkCtrl {

    private final NovelNetworkService novelNetworkService;

    @PostMapping("findCatalogue")
    Wrapper<NovelNetworkCatalogue> findCatalogue(@RequestBody @Validated FindCatalogueReq req) {
        return novelNetworkService.findCatalogue(req);
    }

    @PostMapping("findContent")
    Wrapper<NovelNetworkContent> findContent(@RequestBody @Validated FindContentReq req) {
        return novelNetworkService.findContent(req);
    }

    @PostMapping("download")
    Wrapper<String> download(@RequestBody @Validated DownloadReq req) {
        return novelNetworkService.download(req);
    }

    @PostMapping("fixDownload")
    Wrapper<FixDownloadResp> fixDownload(@RequestBody @Validated FixDownloadReq req) {
        return novelNetworkService.fixDownload(req);
    }

    @PostMapping("checkMissing")
    Wrapper<List<NovelNetworkChapter>> checkMissing(@RequestBody @Validated CheckMissingReq req) {
        return novelNetworkService.checkMissing(req);
    }

    @PostMapping("actuatorDownload")
    Wrapper<ActuatorMeta> actuatorDownload(@RequestBody @Validated ActuatorDownloadReq req) {
        return novelNetworkService.actuatorDownload(req);
    }

    @PostMapping("actuatorSnapshot")
    Wrapper<List<ActuatorSnapshotInfo>> actuatorSnapshot(
            @RequestBody @Validated ActuatorSnapshotReq req) {
        return novelNetworkService.actuatorSnapshot(req);
    }

    @PostMapping("actuatorSnapshotStepData")
    Wrapper<Map<String, Map<String, Object>>> actuatorSnapshotStepData(
            @RequestBody @Validated ActuatorSnapshotStepDataReq req) {
        return novelNetworkService.actuatorSnapshotStepData(req);
    }

    @PostMapping("actuatorInterrupt")
    Wrapper<Void> actuatorInterrupt(@RequestBody @Validated ActuatorSnapshotReq req) {
        return novelNetworkService.actuatorInterrupt(req);
    }

    @PostMapping("actuatorDelete")
    Wrapper<Void> actuatorDelete(@RequestBody @Validated ActuatorSnapshotReq req) {
        return novelNetworkService.actuatorDelete(req);
    }

}
