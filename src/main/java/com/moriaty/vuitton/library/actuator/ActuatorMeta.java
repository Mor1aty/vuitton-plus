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

    private String stepDataPlugin;

    private int timeoutSecond;

    private int stepSleepSecond;

    private LocalDateTime startTime;

    private boolean stepFailContinue;

    public ActuatorMeta(String id, String name, int timeoutSecond, int stepSleepSecond, boolean stepFailContinue) {
        this.id = id;
        this.name = name;
        this.timeoutSecond = timeoutSecond;
        this.stepSleepSecond = stepSleepSecond;
        this.stepFailContinue = stepFailContinue;
    }

    public ActuatorMeta(String id, String name) {
        this.id = id;
        this.name = name;
        this.timeoutSecond = 30 * 60;
        this.stepSleepSecond = 5;
        this.stepFailContinue = false;
    }

}
