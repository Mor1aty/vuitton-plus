package com.moriaty.vuitton.bean.video.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * 导入视频 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/4 下午11:36
 */
@Data
public class ImportVideoReq {

    @NotBlank(message = "name 不能为空")
    private String name;

    @NotBlank(message = "description 不能为空")
    private String description;

}
