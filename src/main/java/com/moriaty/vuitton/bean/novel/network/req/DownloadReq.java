package com.moriaty.vuitton.bean.novel.network.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * 网络小说下载 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午3:48
 */
@Data
public class DownloadReq {

    @NotBlank(message = "downloaderMark 不能为空")
    private String downloaderMark;

    @NotBlank(message = "name 不能为空")
    private String name;

    @NotBlank(message = "catalogueUrl 不能为空")
    private String catalogueUrl;

    private boolean parallel;

    private boolean storage;
    
}
