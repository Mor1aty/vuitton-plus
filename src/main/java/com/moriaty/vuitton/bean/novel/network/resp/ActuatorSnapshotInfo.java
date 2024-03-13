package com.moriaty.vuitton.bean.novel.network.resp;

import com.moriaty.vuitton.library.actuator.ActuatorStepProgress;
import com.moriaty.vuitton.library.actuator.step.StepMeta;
import com.moriaty.vuitton.module.novel.actuator.NovelDownloadActuatorMeta;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 小说下载执行器信息
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/31 上午11:19
 */
@Data
@Accessors(chain = true)
public class ActuatorSnapshotInfo {

    private Boolean running;

    private NovelDownloadActuatorMeta meta;

    private List<StepMeta> stepList;

    private ActuatorStepProgress runningProgress;

    private boolean interrupt;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}
