package com.moriaty.vuitton.bean.novel.local.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 本地小说删除 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/4 下午6:39
 */
@Data
public class DeleteNovelReq {

    @NotNull(message = "id 不能为空")
    private Integer id;

}
