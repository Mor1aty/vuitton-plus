package com.moriaty.vuitton.library.actuator;

import com.moriaty.vuitton.library.actuator.step.StepMeta;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 执行器 Snapshot
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午9:47
 */
@Data
@Accessors(chain = true)
public class ActuatorSnapshot {

    private ActuatorMeta meta;

    private List<StepMeta> stepList;

    private ActuatorStepProgress progress;

    private boolean interrupt;

}
