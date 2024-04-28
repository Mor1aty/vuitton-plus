package com.moriaty.vuitton.module.novel;

import com.moriaty.vuitton.bean.novel.local.NovelChapterWithContent;
import com.moriaty.vuitton.bean.novel.local.NovelLocalAroundChapter;
import com.moriaty.vuitton.dao.mysql.model.Novel;
import com.moriaty.vuitton.dao.mysql.model.NovelChapter;
import com.moriaty.vuitton.module.Module;
import com.moriaty.vuitton.module.ModuleFactory;
import com.moriaty.vuitton.util.NovelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 本地小说模块
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 上午11:31
 */
@Component
@Slf4j
public class NovelLocalModule implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        ModuleFactory.addModule(new Module()
                .setId(1)
                .setName("本地小说"));
    }

    public Optional<List<Integer>> removeDuplicates(List<NovelChapter> existedChapterList) {
        int lastIndex = existedChapterList.getFirst().getIndex();
        existedChapterList = existedChapterList.subList(1, existedChapterList.size());
        List<Integer> duplicatesList = new ArrayList<>();
        for (NovelChapter existedChapter : existedChapterList) {
            if (existedChapter.getIndex() == lastIndex) {
                duplicatesList.add(existedChapter.getId());
            }
            lastIndex = existedChapter.getIndex();
        }
        return duplicatesList.isEmpty() ? Optional.empty() : Optional.of(duplicatesList);
    }

    public Optional<File> reparseFile(Novel novel, List<NovelChapterWithContent> chapterContentList) {
        File file = NovelUtil.writeToFile(novel.getName(), novel.getAuthor(), novel.getIntro(), chapterContentList);
        if (file == null) {
            log.error("{} 重新解析小说文件失败", novel.getName());
            return Optional.empty();
        }
        log.info("{} 重新解析文件完成", novel.getName());
        return Optional.of(file);
    }

    public Optional<NovelLocalAroundChapter> findAroundChapter(List<NovelChapter> chapterList, int chapterIndex) {
        for (int i = 0; i < chapterList.size(); i++) {
            NovelChapter chapter = chapterList.get(i);
            if (chapterIndex == chapter.getIndex()) {
                NovelLocalAroundChapter aroundChapter = new NovelLocalAroundChapter().setChapter(chapter);
                if (i - 1 >= 0) {
                    aroundChapter.setPreChapter(chapterList.get(i - 1));
                }
                if (i + 1 < chapterList.size()) {
                    aroundChapter.setNextChapter(chapterList.get(i + 1));
                }
                return Optional.of(aroundChapter);
            }
        }
        return Optional.empty();
    }
}
