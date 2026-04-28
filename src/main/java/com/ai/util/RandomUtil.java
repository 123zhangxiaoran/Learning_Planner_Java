/**
 * 生成随机数字
 */
package com.ai.util;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    // 生成指定位数随机数字
    public static String generateCode(int length) {
        if (length < 1) length = 6;
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1);
        for (int i = 1; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String generateRandomString() {
        // 定义可选字符集：大小写字母+数字
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(11); // 固定长度11
        for (int i = 0; i < 11; i++) {
            // 随机选择字符集中的一个字符
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    // 1小时的毫秒数
    private static final long ONE_HOUR_MILLIS = 60 * 60 * 1000L; // 3,600,000
    public static long randomHourOffsetMillis() {
        long bound = 2 * ONE_HOUR_MILLIS + 1;
        return ThreadLocalRandom.current().nextLong(bound) - ONE_HOUR_MILLIS;
    }

}
