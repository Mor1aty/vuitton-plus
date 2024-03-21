package com.moriaty.vuitton.bean.novel.local.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * <p>
 * 下载小说 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/3/21 13:25
 */
@Data
public class DownloadNovelReq {

    @NotNull(message = "novelId 不能为空")
    private Integer novelId;

}
