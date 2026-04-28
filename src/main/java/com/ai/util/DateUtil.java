/**
 * 时间格式化，计算
 */
package com.ai.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    // 定义格式：yyyy-MM-dd（月份、日期不足两位时自动补0）
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取当前日期，格式为：年-月-日（如2025-07-05）
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMAT);
    }

    /**
     * 自定义日期，格式为：年-月-日（如2025-07-05）
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }
}
