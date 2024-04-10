package com.moriaty.vuitton.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moriaty.vuitton.dao.model.NovelChapter;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 小说章节表 Mapper
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 上午11:19
 */
public interface NovelChapterMapper extends BaseMapper<NovelChapter> {

    /**
     * 批量插入
     *
     * @param chapterList List with NovelChapter
     */
    @Insert("""
            <script>
                INSERT INTO novel_chapter(novel, `index`, title, content, content_html)
                VALUES
                <foreach collection='chapterList' item='chapter' separator=','>
                    (#{chapter.novel}, #{chapter.index}, #{chapter.title},
                    #{chapter.content}, #{chapter.contentHtml})
                </foreach>
            </script>""")
    void batchInsert(@Param("chapterList") List<NovelChapter> chapterList);
}
