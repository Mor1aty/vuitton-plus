package com.moriaty.vuitton.bean.novel.local.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 本地小说获取环绕章节 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/2 下午11:25
 */
@Data
public class FindAroundChapterReq {

    @NotNull(message = "novelId 不能为空")
    private Integer novelId;

    @NotNull(message = "chapterIndex 不能为空")
    private Integer chapterIndex;
}
