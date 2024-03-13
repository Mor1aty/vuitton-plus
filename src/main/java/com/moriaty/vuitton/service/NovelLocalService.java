package com.moriaty.vuitton.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moriaty.vuitton.ServerInfo;
import com.moriaty.vuitton.bean.PageResp;
import com.moriaty.vuitton.bean.novel.local.NovelLocalAroundChapter;
import com.moriaty.vuitton.bean.novel.local.NovelLocalFullInfo;
import com.moriaty.vuitton.bean.novel.local.NovelLocalReadHistoryInfo;
import com.moriaty.vuitton.bean.novel.local.req.*;
import com.moriaty.vuitton.bean.novel.network.req.ReparseFileReq;
import com.moriaty.vuitton.dao.mapper.NovelChapterMapper;
import com.moriaty.vuitton.dao.mapper.NovelMapper;
import com.moriaty.vuitton.dao.mapper.NovelReadHistoryMapper;
import com.moriaty.vuitton.dao.model.Novel;
import com.moriaty.vuitton.dao.model.NovelChapter;
import com.moriaty.vuitton.dao.model.NovelReadHistory;
import com.moriaty.vuitton.library.wrap.WrapMapper;
import com.moriaty.vuitton.library.wrap.Wrapper;
import com.moriaty.vuitton.module.novel.NovelLocalModule;
import com.moriaty.vuitton.util.NovelUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 本地小说 Service
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 上午11:21
 */
@Service
@AllArgsConstructor
@Slf4j
public class NovelLocalService {

    private final NovelCommonService novelCommonService;

    private final NovelLocalModule novelLocalModule;

    private final NovelMapper novelMapper;

    private final NovelChapterMapper novelChapterMapper;

    private final NovelReadHistoryMapper novelReadHistoryMapper;

    public Wrapper<PageResp<Novel>> findNovel(FindNovelReq req) {
        LambdaQueryWrapper<Novel> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(req.getName())) {
            queryWrapper.like(Novel::getName, req.getName());
        }
        Page<Novel> novelPage = novelMapper.selectPage(new Page<>(req.getPageNum(), req.getPageSize()), queryWrapper);
        return WrapMapper.ok(new PageResp<>(req, novelPage));
    }

    public Wrapper<PageResp<NovelChapter>> findChapter(FindChapterReq req) {
        Page<NovelChapter> chapterPage = novelChapterMapper.selectPage(
                new Page<>(req.getPageNum(), req.getPageSize()), new LambdaQueryWrapper<NovelChapter>()
                        .eq(NovelChapter::getNovel, req.getNovelId()));
        return WrapMapper.ok(new PageResp<>(req, chapterPage));
    }

    public Wrapper<NovelLocalAroundChapter> findAroundChapter(FindAroundChapterReq req) {
        List<NovelChapter> chapterList = novelChapterMapper.selectList(new LambdaQueryWrapper<NovelChapter>()
                .eq(NovelChapter::getNovel, req.getNovelId()));
        if (chapterList.isEmpty()) {
            return WrapMapper.failure("小说章节不存在");
        }
        Optional<NovelLocalAroundChapter> optional =
                novelLocalModule.findAroundChapter(chapterList, req.getChapterIndex());
        return optional.map(WrapMapper::ok).orElseGet(() -> WrapMapper.failure("小说当前章节不存在"));
    }

    public Wrapper<String> reparseFile(ReparseFileReq req) {
        Optional<NovelLocalFullInfo> novelFullInfoOptional = novelCommonService.findFullInfo(req.getId());
        if (novelFullInfoOptional.isEmpty()) {
            return WrapMapper.failure("小说不存在");
        }
        NovelLocalFullInfo novelFullInfo = novelFullInfoOptional.get();
        Optional<File> optional = novelLocalModule.reparseFile(novelFullInfo.getNovel(),
                novelFullInfo.getChapterList());
        if (optional.isEmpty()) {
            return WrapMapper.failure("重新解析小说文件失败");
        }
        String filUrl = NovelUtil.upload(ServerInfo.INFO.getFileServerUploadUrl(),
                optional.get(), novelFullInfo.getNovel().getName() + ".txt");
        if (filUrl == null) {
            return WrapMapper.failure("上传失败");
        }
        novelMapper.updateById(new Novel()
                .setId(novelFullInfo.getNovel().getId())
                .setFileUrl(filUrl));
        return WrapMapper.okStringData(ServerInfo.INFO.getFileServerUrl() + filUrl);
    }

    public Wrapper<Void> deleteNovel(DeleteNovelReq req) {
        novelChapterMapper.delete(new LambdaQueryWrapper<NovelChapter>()
                .eq(NovelChapter::getNovel, req.getId()));
        novelMapper.deleteById(req.getId());
        return WrapMapper.ok();
    }

    public Wrapper<List<NovelLocalReadHistoryInfo>> findReadHistory(FindReadHistoryReq req) {
        List<NovelLocalReadHistoryInfo> readHistoryList = novelReadHistoryMapper.findReadHistory(req.getNovelId());
        return WrapMapper.ok(readHistoryList);
    }

    public Wrapper<Void> insertReadHistory(InsertReadHistoryReq req) {
        Novel novel = novelMapper.selectById(req.getNovelId());
        if (novel == null) {
            return WrapMapper.failure("小说不存在");
        }
        NovelChapter chapter = novelChapterMapper.selectById(req.getChapterId());
        if (chapter == null || !novel.getId().equals(chapter.getNovel())) {
            return WrapMapper.failure("小说章节不存在");
        }
        List<NovelReadHistory> readHistoryList = novelReadHistoryMapper.selectList(
                new LambdaQueryWrapper<NovelReadHistory>().eq(NovelReadHistory::getNovel, req.getNovelId()));
        if (!readHistoryList.isEmpty()) {
            novelReadHistoryMapper.deleteBatchIds(readHistoryList.stream().map(NovelReadHistory::getId).toList());
        }
        novelReadHistoryMapper.insert(new NovelReadHistory()
                .setNovel(req.getNovelId())
                .setChapter(req.getChapterId())
                .setReadTime(LocalDateTime.now()));
        return WrapMapper.ok();
    }
}
