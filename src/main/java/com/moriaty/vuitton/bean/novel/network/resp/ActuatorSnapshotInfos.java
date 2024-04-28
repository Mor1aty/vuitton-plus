package com.moriaty.vuitton.bean.novel.network.resp;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.moriaty.vuitton.dao.mysql.model.Actuator;
import com.moriaty.vuitton.library.actuator.ActuatorSnapshot;
import com.moriaty.vuitton.module.novel.actuator.NovelDownloadActuatorMeta;

/**
 * <p>
 * 小说下载执行器信息构造
 * </p>
 *
 * @author Moriaty
 * @since 2024/2/2 下午1:09
 */
public class ActuatorSnapshotInfos {

    private ActuatorSnapshotInfos() {

    }

    public static ActuatorSnapshotInfo runningActuatorSnapshot(ActuatorSnapshot snapshot) {
        NovelDownloadActuatorMeta meta = (NovelDownloadActuatorMeta) snapshot.getMeta();
        return new ActuatorSnapshotInfo()
                .setRunning(true)
                .setMeta(meta)
                .setStepList(snapshot.getStepList())
                .setRunningProgress(snapshot.getProgress())
                .setInterrupt(snapshot.isInterrupt())
                .setStartTime(snapshot.getMeta().getStartTime())
                .setResult(snapshot.getResult());
    }

    public static ActuatorSnapshotInfo storageActuatorSnapshot(Actuator storageActuator) {
        return new ActuatorSnapshotInfo()
                .setRunning(false)
                .setMeta(JSONObject.parseObject(storageActuator.getMeta(), NovelDownloadActuatorMeta.class))
                .setStepList(JSON.parseObject(storageActuator.getStepList(), new TypeReference<>() {
                }))
                .setInterrupt(storageActuator.getInterrupt())
                .setStartTime(storageActuator.getStartTime())
                .setEndTime(storageActuator.getEndTime())
                .setResult(storageActuator.getResult());
    }
}
