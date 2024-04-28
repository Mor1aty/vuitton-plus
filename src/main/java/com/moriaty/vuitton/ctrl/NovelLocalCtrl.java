package com.moriaty.vuitton.ctrl;

import com.moriaty.vuitton.bean.PageResp;
import com.moriaty.vuitton.bean.novel.local.NovelLocalAroundChapter;
import com.moriaty.vuitton.bean.novel.local.NovelLocalReadHistoryInfo;
import com.moriaty.vuitton.bean.novel.local.req.*;
import com.moriaty.vuitton.bean.novel.network.req.ReparseFileReq;
import com.moriaty.vuitton.dao.mysql.model.Novel;
import com.moriaty.vuitton.dao.mysql.model.NovelChapter;
import com.moriaty.vuitton.library.wrap.Wrapper;
import com.moriaty.vuitton.service.NovelLocalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 本地小说 Ctrl
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 上午11:20
 */
@RestController
@RequestMapping("novel/local")
@AllArgsConstructor
@Slf4j
public class NovelLocalCtrl {

    private final NovelLocalService novelLocalService;

    @PostMapping("findNovel")
    Wrapper<PageResp<Novel>> findNovel(@RequestBody @Validated FindNovelReq req) {
        return novelLocalService.findNovel(req);
    }

    @PostMapping("findChapter")
    Wrapper<PageResp<NovelChapter>> findChapter(@RequestBody @Validated FindChapterReq req) {
        return novelLocalService.findChapter(req);
    }

    @PostMapping("findAroundChapter")
    Wrapper<NovelLocalAroundChapter> findAroundChapter(@RequestBody @Validated FindAroundChapterReq req) {
        return novelLocalService.findAroundChapter(req);
    }

    @PostMapping("reparseFile")
    Wrapper<String> reparseFile(@RequestBody @Validated ReparseFileReq req) {
        return novelLocalService.reparseFile(req);
    }

    @PostMapping("deleteNovel")
    Wrapper<Void> deleteNovel(@RequestBody @Validated DeleteNovelReq req) {
        return novelLocalService.deleteNovel(req);
    }

    @PostMapping("findReadHistory")
    Wrapper<List<NovelLocalReadHistoryInfo>> findReadHistory(@RequestBody @Validated FindReadHistoryReq req) {
        return novelLocalService.findReadHistory(req);
    }

    @PostMapping("insertReadHistory")
    Wrapper<Void> insertReadHistory(@RequestBody @Validated InsertReadHistoryReq req) {
        return novelLocalService.insertReadHistory(req);
    }

    @GetMapping("downloadNovel")
    public ResponseEntity<Resource> downloadNovel(@RequestParam("novelId") Integer novelId) {
        return novelLocalService.downloadNovel(novelId);
    }

}
