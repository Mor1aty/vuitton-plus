package com.moriaty.vuitton.library.actuator;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 * 执行器管理者
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/29 下午3:58
 */
@Slf4j
public class ActuatorManager {

    private ActuatorManager() {

    }

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(10, Integer.MAX_VALUE,
            TimeUnit.SECONDS.toNanos(60L), TimeUnit.NANOSECONDS,
            new SynchronousQueue<>(false),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    private static final Map<String, BaseActuator> RUNNING_ACTUATOR_MAP = new HashMap<>();

    public static void runActuator(BaseActuator actuator) {
        EXECUTOR.execute(() -> {
            try {
                if (!actuator.isInit()) {
                    actuator.init();
                }
                RUNNING_ACTUATOR_MAP.put(actuator.getMeta().getId(), actuator);
                Future<?> future = EXECUTOR.submit(actuator::run);
                future.get(actuator.getMeta().getTimeoutSecond(), TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("{} 执行被打断", actuator.getMark());
            } catch (ExecutionException e) {
                log.error("{} 执行发生异常", actuator.getMark(), e);
            } catch (TimeoutException e) {
                log.error("{} 执行超时", actuator.getMark());
            }
            RUNNING_ACTUATOR_MAP.remove(actuator.getMeta().getId());
        });
    }

    public static Map<String, BaseActuator> snapshotRunningActuator() {
        return new HashMap<>(RUNNING_ACTUATOR_MAP);
    }

    public static BaseActuator getRunningActuator(String id) {
        return RUNNING_ACTUATOR_MAP.get(id);
    }

}
