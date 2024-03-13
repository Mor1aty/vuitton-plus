package com.moriaty.vuitton.bean.novel.local.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 本地小说插入阅读历史 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/5 下午10:21
 */
@Data
public class InsertReadHistoryReq {

    @NotNull(message = "novelId 不能为空")
    private Integer novelId;

    @NotNull(message = "chapterId 不能为空")
    private Integer chapterId;

}
