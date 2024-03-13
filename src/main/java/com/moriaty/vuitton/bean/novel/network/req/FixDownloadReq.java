package com.moriaty.vuitton.bean.novel.network.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 网络小说修补下载 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午8:50
 */
@Data
public class FixDownloadReq {

    @NotNull(message = "id 不能为空")
    private Integer id;

    private Integer fixNum;

}
