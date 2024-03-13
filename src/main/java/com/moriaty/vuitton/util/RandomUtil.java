package com.moriaty.vuitton.util;

import java.util.Random;

/**
 * <p>
 * 随机工具
 * </p>
 *
 * @author Moriaty
 * @since 2024/1/28 下午9:19
 */
public class RandomUtil {

    private RandomUtil() {

    }

    private static final Random random = new Random();

    public static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

}
