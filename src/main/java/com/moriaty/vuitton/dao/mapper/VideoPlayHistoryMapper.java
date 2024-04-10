package com.moriaty.vuitton.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moriaty.vuitton.bean.video.VideoPlayHistoryInfo;
import com.moriaty.vuitton.dao.model.VideoPlayHistory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 视频播放历史表 Mapper
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/5 下午10:09
 */
public interface VideoPlayHistoryMapper extends BaseMapper<VideoPlayHistory> {

    /**
     * 查询播放历史
     *
     * @param videoId  Integer
     * @param limitNum Integer
     * @return List of VideoPlayHistoryInfo
     */
    @Select("""
            <script>
            SELECT
            	v.id AS videoId,
            	v.`name` AS videoName,
            	v.description AS videoDescription,
            	v.img_url AS videoImgUrl,
            	ve.id AS episodeId,
            	ve.`index` AS episodeIndex,
            	ve.`name` AS episodeName,
            	vph.episode_play_second AS episodePlaySecond,
            	vph.episode_play_second_str AS episodePlaySecondStr,
            	vph.play_time AS playTime
            FROM
            	video_play_history vph
            	LEFT JOIN video v ON vph.video = v.id
            	LEFT JOIN video_episode ve ON vph.episode = ve.id
            	AND vph.video = ve.video
            WHERE
            	1 = 1
            <if test="videoId != null"> AND vph.video = #{videoId} </if>
            ORDER BY vph.play_time DESC
            <if test="limitNum != null"> LIMIT #{limitNum} </if>
            </script>""")
    List<VideoPlayHistoryInfo> findPlayHistory(@Param("videoId") Integer videoId, @Param("limitNum") Integer limitNum);
}
