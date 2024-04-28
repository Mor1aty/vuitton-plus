package com.moriaty.vuitton.dao.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moriaty.vuitton.bean.novel.local.NovelLocalReadHistoryInfo;
import com.moriaty.vuitton.dao.mysql.model.NovelReadHistory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 小说阅读历史表 Mapper
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 上午11:20
 */
public interface NovelReadHistoryMapper extends BaseMapper<NovelReadHistory> {

    /**
     * 查询阅读历史
     *
     * @param novelId Integer
     * @return List with NovelLocalReadHistoryInfo
     */
    @Select("""
            <script>
            SELECT
            	n.id AS novelId,
            	n.`name` AS novelName,
            	n.author AS novelAuthor,
            	n.intro AS novelIntro,
            	n.img_url AS novelImgUrl,
            	nc.id AS chapterId,
            	nc.`index` AS chapterIndex,
            	nc.title AS chapterTitle,
            	nrh.read_time AS readTime
            FROM
            	novel_read_history nrh
            	LEFT JOIN novel n ON nrh.novel = n.id
            	LEFT JOIN novel_chapter nc ON nrh.chapter = nc.id
            	AND nrh.novel = nc.novel
            WHERE
            	1 = 1
            <if test="novelId != null"> AND nrh.novel = #{novelId} </if>
            ORDER BY nrh.read_time DESC
            LIMIT 100
            </script>""")
    List<NovelLocalReadHistoryInfo> findReadHistory(@Param("novelId") Integer novelId);

}
