package com.moriaty.vuitton.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moriaty.vuitton.bean.video.VideoSearchInfo;
import com.moriaty.vuitton.dao.model.Video;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 视频表 Mapper
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/4 下午10:42
 */
public interface VideoMapper extends BaseMapper<Video> {

    /**
     * 分页查询视频信息
     *
     * @param name        String
     * @param startIndex  int
     * @param limitOffset int
     * @return List with VideoSearchInfo
     */
    @Select("""
            <script>
                SELECT
                    id AS videoId,
                    `name` AS videoName,
                    `description` AS videoDescription,
                    img_url AS videoImgUrl,
                    create_time AS videoCreateTime
                FROM video
                WHERE
                    1 = 1
                    <if test="name != null and name != ''"> AND `name` LIKE #{name} </if>
                ORDER BY create_time DESC
                LIMIT #{startIndex}, #{limitOffset}
            </script>""")
    List<VideoSearchInfo> findSearchVideoPage(@Param("name") String name,
                                              @Param("startIndex") int startIndex,
                                              @Param("limitOffset") int limitOffset);

    /**
     * 分页查询视频信息总数
     *
     * @param name String
     * @return long
     */
    @Select("""
            <script>
                SELECT
                    COUNT(*)
                FROM video
                WHERE
                    1 = 1
                    <if test="name != null and name != ''"> AND `name` LIKE #{name} </if>
            </script>""")
    long findSearchVideoPageTotal(@Param("name") String name);

    /**
     * 分页查询视频历史信息
     *
     * @param name        String
     * @param startIndex  int
     * @param limitOffset int
     * @return List with VideoSearchInfo
     */
    @Select("""
            <script>
                SELECT
                    v.id AS videoId,
                    v.`name` AS videoName,
                    v.description AS videoDescription,
                    v.img_url AS videoImgUrl,
                    v.create_time AS videoCreateTime,
                    vph.episode AS lastPlayEpisodeId,
                    ve.`name` AS lastPlayEpisodeName,
                    vph.episode_play_second AS lastPlayEpisodeSecond,
                    vph.episode_play_second_str AS lastPlayEpisodeSecondStr,
                    vph.play_time AS lastPlayTime
                FROM
                	video_play_history vph
                	LEFT JOIN video v ON vph.video = v.id
                	LEFT JOIN video_episode ve ON vph.episode = ve.id
                	AND vph.video = ve.video
                WHERE
                    1 = 1
                    <if test="name != null and name != ''"> AND v.`name` LIKE #{name} </if>
                ORDER BY vph.play_time DESC
                LIMIT #{startIndex}, #{limitOffset}
            </script>""")
    List<VideoSearchInfo> findHistorySearchVideoPage(@Param("name") String name,
                                                     @Param("startIndex") int startIndex,
                                                     @Param("limitOffset") int limitOffset);

    /**
     * 分页查询视频历史信息总数
     *
     * @param name String
     * @return long
     */
    @Select("""
            <script>
                SELECT
                    COUNT(*)
                FROM
                	video_play_history vph
                	LEFT JOIN video v ON vph.video = v.id
                	LEFT JOIN video_episode ve ON vph.episode = ve.id
                	AND vph.video = ve.video
                WHERE
                    1 = 1
                    <if test="name != null and name != ''"> AND v.`name` LIKE #{name} </if>
            </script>""")
    long findHistorySearchVideoPageTotal(@Param("name") String name);

}
