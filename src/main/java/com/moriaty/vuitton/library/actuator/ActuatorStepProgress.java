package com.moriaty.vuitton.library.actuator;

import com.moriaty.vuitton.library.actuator.step.StepMeta;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 执行器步骤进度
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午2:51
 */
@Data
@Accessors(chain = true)
public class ActuatorStepProgress {

    private int currentStepIndex;

    private StepMeta currentStep;

    private String currentStepProgress;

    private int totalStep;

}
