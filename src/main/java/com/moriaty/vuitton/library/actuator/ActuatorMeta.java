package com.moriaty.vuitton.library.actuator;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 执行器元数据
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午3:02
 */
@Getter
@Setter
public class ActuatorMeta {

    private String id;

    private String name;

    private int timeoutSecond;

    private int stepSleepSecond;

    private LocalDateTime startTime;

    private boolean stepFailContinue;

    public ActuatorMeta(String name, int timeoutSecond, int stepSleepSecond, boolean stepFailContinue) {
        this.name = name;
        this.timeoutSecond = timeoutSecond;
        this.stepSleepSecond = stepSleepSecond;
        this.stepFailContinue = stepFailContinue;
    }

}
