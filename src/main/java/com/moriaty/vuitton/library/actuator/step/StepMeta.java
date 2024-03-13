package com.moriaty.vuitton.library.actuator.step;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 执行器步骤元数据
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午2:36
 */
@Data
@Accessors(chain = true)
public class StepMeta {

    private String name;

}
