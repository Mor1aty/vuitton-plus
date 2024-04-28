package com.moriaty.vuitton.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moriaty.vuitton.bean.novel.local.NovelChapterWithContent;
import com.moriaty.vuitton.bean.novel.local.NovelLocalFullInfo;
import com.moriaty.vuitton.dao.mongo.impl.MongoNovelChapterContentImpl;
import com.moriaty.vuitton.dao.mongo.model.MongoNovelChapterContent;
import com.moriaty.vuitton.dao.mysql.mapper.NovelChapterMapper;
import com.moriaty.vuitton.dao.mysql.mapper.NovelMapper;
import com.moriaty.vuitton.dao.mysql.model.Novel;
import com.moriaty.vuitton.dao.mysql.model.NovelChapter;
import com.moriaty.vuitton.module.novel.downloader.BaseNovelDownloader;
import com.moriaty.vuitton.util.NovelUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 小说通用 Service
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 上午11:23
 */
@Service
@AllArgsConstructor
@Slf4j
public class NovelCommonService {

    private final NovelMapper novelMapper;

    private final NovelChapterMapper novelChapterMapper;

    private final MongoNovelChapterContentImpl mongoNovelChapterContentImpl;

    public Optional<NovelLocalFullInfo> findFullInfo(int id) {
        Novel novel = novelMapper.selectById(id);
        if (novel == null) {
            return Optional.empty();
        }
        BaseNovelDownloader novelDownloader = NovelUtil.findNovelDownloader(novel.getDownloaderMark());
        if (novelDownloader == null) {
            return Optional.empty();
        }
        List<NovelChapterWithContent> chapterContentList = novelChapterMapper.selectList(new LambdaQueryWrapper<NovelChapter>()
                .eq(NovelChapter::getNovel, novel.getId())
                .orderByAsc(NovelChapter::getIndex)).stream().map(chapter -> {
            MongoNovelChapterContent content = mongoNovelChapterContentImpl.getById(chapter.getContentId());
            return new NovelChapterWithContent()
                    .setChapter(chapter)
                    .setContent(content);
        }).toList();
        return Optional.of(new NovelLocalFullInfo()
                .setNovel(novel)
                .setChapterContentList(chapterContentList)
                .setNovelDownloader(novelDownloader));
    }

}
