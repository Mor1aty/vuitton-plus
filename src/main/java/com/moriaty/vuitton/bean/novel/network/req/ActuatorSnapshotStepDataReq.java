package com.moriaty.vuitton.bean.novel.network.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * 执行器 Snapshot 步骤数据 Req
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/2 下午1:26
 */
@Data
public class ActuatorSnapshotStepDataReq {

    @NotBlank(message = "id 不能为空")
    private String id;

}
