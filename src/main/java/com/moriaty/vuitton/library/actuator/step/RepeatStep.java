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
public abstract class RepeatStep extends Step {

    private int repeatNum = 0;

    private int repeatSleepSecond;

    protected abstract boolean isStopRepeat();

    protected abstract void repeatRunContent();

    protected abstract int initRepeatSleepSecond();

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
