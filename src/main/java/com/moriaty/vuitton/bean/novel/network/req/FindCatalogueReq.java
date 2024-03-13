package com.moriaty.vuitton.bean.novel.network.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * 网络小说查询目录 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午12:54
 */
@Data
public class FindCatalogueReq {

    @NotBlank(message = "downloaderMark 不能为空")
    private String downloaderMark;

    @NotBlank(message = "catalogueUrl 不能为空")
    private String catalogueUrl;

}
