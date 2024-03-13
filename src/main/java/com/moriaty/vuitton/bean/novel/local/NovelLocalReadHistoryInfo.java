package com.moriaty.vuitton.bean.novel.local;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 本地小说阅读历史信息
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/6 上午11:16
 */
@Data
@Accessors(chain = true)
public class NovelLocalReadHistoryInfo {

    private Integer novelId;

    private String novelName;

    private String novelAuthor;

    private String novelIntro;

    private String novelImgUrl;

    private Integer chapterId;

    private Integer chapterIndex;

    private String chapterTitle;

    private LocalDateTime readTime;

}
