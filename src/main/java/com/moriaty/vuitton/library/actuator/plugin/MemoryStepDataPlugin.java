package com.moriaty.vuitton.library.actuator.plugin;

import com.alibaba.fastjson2.JSONObject;
import com.moriaty.vuitton.library.actuator.ActuatorMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 内存步骤数据插件
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/9 上午4:19
 */
public class MemoryStepDataPlugin implements StepDataPlugin {

    private final Map<String, JSONObject> stepDataMap = new HashMap<>();

    @Override
    public String getName() {
        return "内存步骤数据插件";
    }

    @Override
    public void storeMetaData(ActuatorMeta meta) {
        stepDataMap.put("元数据", JSONObject.from(meta));
    }

    @Override
    public void storeStepData(String stepName, JSONObject stepData) {
        stepDataMap.put(stepName, stepData);
    }

    @Override
    public JSONObject getStepData(String stepName) {
        return stepDataMap.get(stepName);
    }

    @Override
    public Map<String, JSONObject> snapshotStepData() {
        return new HashMap<>(stepDataMap);
    }

    @Override
    public void clearStepData() {
        stepDataMap.clear();
    }
}
