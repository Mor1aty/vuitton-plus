package com.moriaty.vuitton.library.actuator;

import com.alibaba.fastjson2.JSONObject;
import com.moriaty.vuitton.library.actuator.step.Step;
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
public abstract class Actuator {

    @Getter
    private boolean init;

    @Getter
    private String mark;

    @Getter
    private ActuatorMeta meta;

    protected Step currentStep;

    private List<Step> stepList;

    private ActuatorStepProgress progress;

    private boolean interrupt = false;

    private String lastStepDataKey = null;

    protected final Map<String, JSONObject> stepDataMap = new HashMap<>();

    protected abstract ActuatorMeta initMeta();

    protected abstract List<Step> initStep();

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
        mark = "执行器 " + meta.getName() + "[" + meta.getId() + "]";
        stepList = initStep();
        progress = new ActuatorStepProgress().setTotalStep(stepList.size());
        init = true;
    }

    protected void initStepData(Map<String, Object> stepData) {
        lastStepDataKey = "0-init";
        stepDataMap.put("0-init", new JSONObject(stepData));
    }

    public Map<String, Map<String, Object>> snapshotStepData() {
        HashMap<String, Map<String, Object>> snapshot = new HashMap<>(stepDataMap);
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
                .setStepList(stepList.stream().map(Step::getMeta).toList())
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
                Step step = stepList.get(i);
                if (lastStepDataKey != null) {
                    step.importStepData(stepDataMap.get(lastStepDataKey));
                }
                currentStep = step;
                progress.setCurrentStepIndex(stepIndex)
                        .setCurrentStep(step.getMeta());
                log.info("{} 开始执行 {}, {}/{}", mark, progress.getCurrentStep().getName(),
                        progress.getCurrentStepIndex(), progress.getTotalStep());
                boolean success = step.run();
                lastStepDataKey = stepIndex + "-" + step.getMeta().getName();
                stepDataMap.put(lastStepDataKey, step.exportStepData());
                if (handleRunResult(success)) {
                    break;
                }
            }
        } finally {
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
