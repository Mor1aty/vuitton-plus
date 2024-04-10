package com.moriaty.vuitton.library.actuator;

import com.alibaba.fastjson2.JSONObject;
import com.moriaty.vuitton.library.actuator.plugin.MemoryStepDataPlugin;
import com.moriaty.vuitton.library.actuator.plugin.StepDataPlugin;
import com.moriaty.vuitton.library.actuator.step.BaseStep;
import com.moriaty.vuitton.util.UuidUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 执行器
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午2:02
 */
@Slf4j
public abstract class BaseActuator {

    @Getter
    private boolean init;

    @Getter
    private String mark;

    @Getter
    private ActuatorMeta meta;

    protected BaseStep currentStep;

    private List<BaseStep> stepList;

    private ActuatorStepProgress progress;

    private boolean interrupt = false;

    private String lastStepDataKey = null;

    protected StepDataPlugin stepDataPlugin;

    /**
     * 初始化 meta
     *
     * @return ActuatorMeta
     */
    protected abstract ActuatorMeta initMeta();

    /**
     * 初始化步骤
     *
     * @return List with BaseStep
     */
    protected abstract List<BaseStep> initStep();

    protected void init() {
        if (init) {
            return;
        }
        meta = initMeta();
        if (!StringUtils.hasText(meta.getId())) {
            meta.setId("Actuator-" + UuidUtil.genId());
        }
        if (!StringUtils.hasText(meta.getName())) {
            meta.setName("默认执行器");
        }
        if (meta.getTimeoutSecond() <= 0) {
            meta.setTimeoutSecond(5 * 60);
        }
        if (meta.getStepSleepSecond() < 0) {
            meta.setStepSleepSecond(10);
        }
        stepDataPlugin = loadStepDataPlugin();
        meta.setStepDataPlugin(stepDataPlugin.getName());
        mark = "执行器 " + meta.getName() + "[" + meta.getId() + "]";
        stepList = initStep();
        progress = new ActuatorStepProgress().setTotalStep(stepList.size());
        stepDataPlugin.storeMetaData(meta);
        init = true;
    }

    protected void initStepData(Map<String, Object> stepData) {
        lastStepDataKey = "0-init";
        stepDataPlugin.storeStepData("0-init", new JSONObject(stepData));
    }

    protected StepDataPlugin loadStepDataPlugin() {
        return new MemoryStepDataPlugin();
    }

    public Map<String, Map<String, Object>> snapshotStepData() {
        Map<String, Map<String, Object>> snapshot = new HashMap<>(stepDataPlugin.snapshotStepData());
        snapshot.put(progress.getCurrentStepIndex() + "-" + currentStep.getMeta().getName(),
                currentStep.snapshotStepData());
        return snapshot;
    }

    public ActuatorStepProgress getProgress() {
        if (interrupt) {
            progress.setCurrentStepProgress("打断中");
        } else if (currentStep != null) {
            progress.setCurrentStepProgress(currentStep.getProgress());
        } else {
            progress.setCurrentStepProgress("未知");
        }
        return progress;
    }

    public ActuatorSnapshot snapshot() {
        return new ActuatorSnapshot()
                .setMeta(meta)
                .setStepList(stepList.stream().map(BaseStep::getMeta).toList())
                .setProgress(getProgress())
                .setInterrupt(interrupt);
    }

    public void interrupt() {
        currentStep.setInterrupt(true);
        interrupt = true;
    }

    public void run() {
        if (!init) {
            log.error("未初始化");
            return;
        }
        meta.setStartTime(LocalDateTime.now());
        try {
            int stepIndex;
            for (int i = 0; i < stepList.size(); i++) {
                stepIndex = i + 1;
                BaseStep step = stepList.get(i);
                if (lastStepDataKey != null) {
                    step.importStepData(stepDataPlugin.getStepData(lastStepDataKey));
                }
                currentStep = step;
                progress.setCurrentStepIndex(stepIndex)
                        .setCurrentStep(step.getMeta());
                log.info("{} 开始执行 {}, {}/{}", mark, progress.getCurrentStep().getName(),
                        progress.getCurrentStepIndex(), progress.getTotalStep());
                boolean success = step.run();
                lastStepDataKey = stepIndex + "-" + step.getMeta().getName();
                stepDataPlugin.storeStepData(lastStepDataKey, step.exportStepData());
                if (handleRunResult(success)) {
                    break;
                }
            }
        } finally {
            stepDataPlugin.clearStepData();
            beforeEnd();
        }
    }

    private boolean handleRunResult(boolean success) {
        if (success) {
            log.info("{} 执行 {} 成功", mark, progress.getCurrentStep().getName());
        } else {
            log.info("{} 执行 {} 失败", mark, progress.getCurrentStep().getName());
            if (!meta.isStepFailContinue()) {
                log.info("{} 退出执行", mark);
                return true;
            }
        }
        if (interrupt) {
            log.info("{} 执行被打断, 退出执行", mark);
            return true;
        }
        try {
            Thread.sleep(Duration.ofSeconds(meta.getStepSleepSecond()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    protected void beforeEnd() {
        log.info("{} 执行完成", mark);
    }

}
