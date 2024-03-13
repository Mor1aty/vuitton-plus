package com.moriaty.vuitton.bean.novel.network.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * 执行器下载 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午9:41
 */
@Data
public class ActuatorDownloadReq {

    @NotBlank(message = "downloaderMark 不能为空")
    private String downloaderMark;

    @NotBlank(message = "name 不能为空")
    private String name;

    @NotBlank(message = "catalogueUrl 不能为空")
    private String catalogueUrl;

}
