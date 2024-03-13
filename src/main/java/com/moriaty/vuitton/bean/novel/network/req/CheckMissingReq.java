package com.moriaty.vuitton.bean.novel.network.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 网络小说检查缺失 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 上午11:19
 */
@Data
public class CheckMissingReq {

    @NotNull(message = "id 不能为空")
    private Integer id;

}
