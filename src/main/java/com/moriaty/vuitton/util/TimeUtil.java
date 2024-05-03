package com.moriaty.vuitton.util;

import java.time.Duration;
import java.util.Random;

/**
 * <p>
 * 时间工具
 * </p>
 *
 * @author Moriaty
 * @since 2024/5/3 下午5:47
 */
public class TimeUtil {

    private TimeUtil() {

    }

    private static final Random RANDOM = new Random();

    public static void sleepSecond(int second) {
        try {
            Thread.sleep(Duration.ofSeconds(second));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void sleepRandomSecond(int min, int max) {
        sleepSecond(RANDOM.nextInt(max - min + 1) + min);
    }

}
