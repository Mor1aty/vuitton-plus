package com.moriaty.vuitton.bean.novel.local.req;

import com.moriaty.vuitton.bean.PageReq;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 本地小说获取章节 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/31 上午11:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FindChapterReq extends PageReq {

    @NotNull(message = "novelId 不能为空")
    private Integer novelId;

}
