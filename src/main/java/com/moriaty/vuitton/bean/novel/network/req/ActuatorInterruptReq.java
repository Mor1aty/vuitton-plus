package com.moriaty.vuitton.bean.novel.network.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * 执行器打断 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午9:44
 */
@Data
public class ActuatorInterruptReq {

    @NotBlank(message = "id 不能为空")
    private String id;

}
