package com.moriaty.vuitton.library.actuator.step;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * <p>
 * 重复步骤
 * </p>
 *
 * @author Moriaty
 * @since 2024/4/9 上午2:11
 */
@Slf4j
public abstract class BaseRepeatStep extends BaseStep {

    private int repeatNum = 0;

    private int repeatSleepSecond;

    /**
     * 是否停止重复
     *
     * @return boolean
     */
    protected abstract boolean isStopRepeat();

    /**
     * 重复执行内容
     */
    protected abstract void repeatRunContent();

    /**
     * 初始化重复休眠时间(秒)
     *
     * @return int
     */
    protected abstract int initRepeatSleepSecond();

    /**
     * 开始运行之前
     */
    protected abstract void beforeRun();

    @Override
    public void init() {
        super.init();
        repeatSleepSecond = initRepeatSleepSecond();
        if (repeatSleepSecond <= 0) {
            repeatSleepSecond = 5;
        }
    }

    @Override
    public boolean runContent() {
        beforeRun();
        while (true) {
            if (isStopRepeat() || super.isInterrupt()) {
                log.info("步骤 {} 重复执行{}", super.getMeta().getName(), super.isInterrupt() ? "被打断" : "结束");
                break;
            }
            repeatNum++;
            log.info("步骤 {} 第 {} 次执行", super.getMeta().getName(), repeatNum);
            repeatRunContent();
            try {
                Thread.sleep(Duration.ofSeconds(repeatSleepSecond));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return true;
    }
}
