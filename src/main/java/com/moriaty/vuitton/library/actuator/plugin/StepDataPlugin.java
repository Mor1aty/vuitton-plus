package com.moriaty.vuitton.library.actuator.plugin;

import com.alibaba.fastjson2.JSONObject;
import com.moriaty.vuitton.library.actuator.ActuatorMeta;

import java.util.Map;

/**
 * <p>
 * 步骤数据插件
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/9 上午4:14
 */
public interface StepDataPlugin {

    String getName();

    void storeMetaData(ActuatorMeta meta);

    void storeStepData(String stepName, JSONObject stepData);

    JSONObject getStepData(String stepName);

    Map<String, JSONObject> snapshotStepData();

    void clearStepData();

}
