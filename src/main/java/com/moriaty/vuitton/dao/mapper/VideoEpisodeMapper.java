package com.moriaty.vuitton.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moriaty.vuitton.dao.model.VideoEpisode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 视频分集表 Mapper
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/4 下午10:43
 */
public interface VideoEpisodeMapper extends BaseMapper<VideoEpisode> {

    /**
     * 批量插入
     *
     * @param episodeList List with VideoEpisode
     */
    @Insert("""
            <script>
                INSERT INTO video_episode(video, `index`, `name`, `url`)
                VALUES
                <foreach collection='episodeList' item='episode' separator=','>
                    (#{episode.video}, #{episode.index}, #{episode.name}, #{episode.url})
                </foreach>
            </script>""")
    void batchInsert(@Param("episodeList") List<VideoEpisode> episodeList);

}
