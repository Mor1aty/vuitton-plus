package com.moriaty.vuitton.library.actuator.step;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 执行器步骤
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午2:35
 */
@Slf4j
public abstract class BaseStep {

    @Getter
    private StepMeta meta;

    @Setter
    @Getter
    private boolean interrupt;

    private final JSONObject stepData = new JSONObject();

    protected BaseStep() {
        init();
    }

    /**
     * 初始化 meta
     *
     * @return StepMeta
     */
    protected abstract StepMeta initMeta();

    /**
     * 获取进度
     *
     * @return String
     */
    public abstract String getProgress();

    /**
     * 运行内容
     *
     * @return boolean
     */
    public abstract boolean runContent();

    public void importStepData(JSONObject shareData) {
        stepData.putAll(shareData);
    }

    public JSONObject exportStepData() {
        return stepData;
    }

    protected <T> T getStepData(String key, TypeReference<T> typeReference) {
        return stepData.getObject(key, typeReference);
    }

    protected <T> void putStepData(String key, T data) {
        stepData.put(key, data);
    }

    public Map<String, Object> snapshotStepData() {
        return new HashMap<>(stepData);
    }

    protected void init() {
        meta = initMeta();
    }

    public boolean run() {
        log.info("步骤 {} 开始执行", meta.getName());
        boolean success = runContent();
        log.info("步骤 {} 执行{}", meta.getName(), success ? "成功" : "失败");
        return success;
    }

}
