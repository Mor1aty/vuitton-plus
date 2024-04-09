package com.moriaty.vuitton.library.actuator.plugin;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * Redis 步骤数据插件信息
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/9 下午4:38
 */
@Data
@Accessors(chain=true)
public class RedisStepDataPluginInfo {

    private String stepName;

    private JSONObject stepData;

}
