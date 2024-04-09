package com.moriaty.vuitton.library.actuator.plugin;

import com.alibaba.fastjson2.JSONObject;
import com.moriaty.vuitton.library.actuator.ActuatorMeta;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;

/**
 * <p>
 * Redis 步骤数据插件
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/9 上午4:20
 */
public class RedisStepDataPlugin implements StepDataPlugin {

    private final StringRedisTemplate stringRedisTemplate;

    private final String redisKeyPrefix;

    private final Duration redisTtl;

    private final List<String> redisKeyList = new ArrayList<>();


    public RedisStepDataPlugin(StringRedisTemplate stringRedisTemplate, String redisKeyPrefix, Duration redisTtl) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisKeyPrefix = redisKeyPrefix;
        this.redisTtl = redisTtl;
    }

    @Override
    public String getName() {
        return "Redis 步骤数据插件";
    }

    @Override
    public void storeMetaData(ActuatorMeta meta) {
        String redisKey = redisKeyPrefix + "元数据";
        redisKeyList.add(redisKey);
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(new RedisStepDataPluginInfo()
                .setStepName("元数据")
                .setStepData(JSONObject.from(meta))), redisTtl);
    }

    @Override
    public void storeStepData(String stepName, JSONObject stepData) {
        String redisKey = redisKeyPrefix + stepName;
        redisKeyList.add(redisKey);
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(new RedisStepDataPluginInfo()
                .setStepName(stepName)
                .setStepData(stepData)), redisTtl);
    }

    @Override
    public JSONObject getStepData(String stepName) {
        String stepDataJson = stringRedisTemplate.opsForValue().get(redisKeyPrefix + stepName);
        return StringUtils.hasText(stepDataJson) ?
                JSONObject.parseObject(stepDataJson, RedisStepDataPluginInfo.class).getStepData() : new JSONObject();
    }

    @Override
    public Map<String, JSONObject> snapshotStepData() {
        Map<String, JSONObject> snapshot = new HashMap<>();
        List<String> jsonList = stringRedisTemplate.opsForValue().multiGet(redisKeyList);
        if (jsonList != null) {
            jsonList.forEach(json -> {
                if (StringUtils.hasText(json)) {
                    RedisStepDataPluginInfo info = JSONObject.parseObject(json, RedisStepDataPluginInfo.class);
                    if (info != null) {
                        snapshot.put(info.getStepName(), info.getStepData());
                    }
                }
            });
        }
        return snapshot;
    }

    @Override
    public void clearStepData() {
        stringRedisTemplate.executePipelined((RedisCallback<String>) connection -> {
            StringRedisConnection conn = (StringRedisConnection) connection;
            for (String key : redisKeyList) {
                conn.del(key);
            }
            return null;
        });
    }
}
