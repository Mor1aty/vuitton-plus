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

    /**
     * 获取名称
     *
     * @return String
     */
    String getName();

    /**
     * 存储 meta 数据
     *
     * @param meta ActuatorMeta
     */
    void storeMetaData(ActuatorMeta meta);

    /**
     * 存储步骤数据
     *
     * @param stepName String
     * @param stepData JSONObject
     */
    void storeStepData(String stepName, JSONObject stepData);

    /**
     * 获取步骤数据
     *
     * @param stepName String
     * @return JSONObject
     */
    JSONObject getStepData(String stepName);

    /**
     * 快照步骤数据
     *
     * @return Map with key String val JSONObject
     */
    Map<String, JSONObject> snapshotStepData();

    /**
     * 清理步骤数据
     */
    void clearStepData();

}
