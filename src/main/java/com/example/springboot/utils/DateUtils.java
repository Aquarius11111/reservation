package com.example.springboot.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 日期时间工具类（适配预约模块全业务场景）
 */
public class DateUtils {
    // 日期时间格式常量
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // 线程安全的 SimpleDateFormat（避免频繁创建对象+线程安全问题）
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_FORMAT));
    private static final ThreadLocal<SimpleDateFormat> TIME_FORMATTER =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(TIME_FORMAT));
    private static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMATTER =
            ThreadLocal.withInitial(() -> new SimpleDateFormat(DATE_TIME_FORMAT));


    // ======================== 基础格式化方法 ========================
    /**
     * 格式化日期（yyyy-MM-dd）
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.get().format(date);
    }

    /**
     * 格式化时间（HH:mm:ss）
     */
    public static String formatTime(Date date) {
        if (date == null) {
            return null;
        }
        return TIME_FORMATTER.get().format(date);
    }

    /**
     * 格式化日期时间（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.get().format(date);
    }


    // ======================== 基础解析方法 ========================
    /**
     * 解析日期（yyyy-MM-dd）
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return DATE_FORMATTER.get().parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("日期格式错误（需符合yyyy-MM-dd）：" + dateStr, e);
        }
    }

    /**
     * 解析时间（HH:mm:ss）
     */
    public static Date parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        try {
            // 拼接当前日期（确保Date对象可正常参与时间比较）
            String currentDateStr = formatDate(new Date());
            String dateTimeStr = currentDateStr + " " + timeStr;
            return DATE_TIME_FORMATTER.get().parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException("时间格式错误（需符合HH:mm:ss）：" + timeStr, e);
        }
    }

    /**
     * 解析日期时间（yyyy-MM-dd HH:mm:ss）
     */
    public static Date parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return DATE_TIME_FORMATTER.get().parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException("日期时间格式错误（需符合yyyy-MM-dd HH:mm:ss）：" + dateTimeStr, e);
        }
    }


    // ======================== 日期时间加减方法 ========================
    /**
     * 日期加/减天数（正数加，负数减）
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 日期时间加/减小时（正数加，负数减）
     */
    public static Date addHours(Date date, int hours) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    /**
     * 日期时间加/减分钟（正数加，负数减）
     */
    public static Date addMinutes(Date date, int minutes) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }


    // ======================== 日期判断方法 ========================
    /**
     * 判断日期是否为今天
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        return formatDate(date).equals(formatDate(new Date()));
    }

    /**
     * 判断日期是否为未来日期（不含今天）
     */
    public static boolean isFutureDate(Date date) {
        if (date == null) {
            return false;
        }
        Date today = parseDate(formatDate(new Date()));
        Date target = parseDate(formatDate(date));
        return target.after(today);
    }

    /**
     * 判断日期是否在“今天+未来n天”范围内（预约时段筛选核心）
     */
    public static boolean isInFutureDays(Date date, int days) {
        if (date == null || days < 0) {
            return false;
        }
        Date today = parseDate(formatDate(new Date()));
        Date endDate = addDays(today, days);
        Date target = parseDate(formatDate(date));
        return !target.before(today) && !target.after(endDate);
    }


    // ======================== 时段处理方法 ========================
    /**
     * 判断两个时段是否重叠（日期相同前提下）
     */
    public static boolean isTimeOverlap(String newStartTime, String newEndTime,
                                        String existStartTime, String existEndTime) {
        Date newStart = parseTime(newStartTime);
        Date newEnd = parseTime(newEndTime);
        Date existStart = parseTime(existStartTime);
        Date existEnd = parseTime(existEndTime);

        // 重叠逻辑：新时段开始 < 已有时段结束 且 新时段结束 > 已有时段开始
        return newStart.before(existEnd) && newEnd.after(existStart);
    }

    /**
     * 获取指定日期的开始时间（00:00:00）
     */
    public static Date getDayStartTime(Date date) {
        if (date == null) {
            return null;
        }
        String dateStr = formatDate(date);
        return parseDateTime(dateStr + " 00:00:00");
    }

    /**
     * 获取指定日期的结束时间（23:59:59）
     */
    public static Date getDayEndTime(Date date) {
        if (date == null) {
            return null;
        }
        String dateStr = formatDate(date);
        return parseDateTime(dateStr + " 23:59:59");
    }


    // ======================== 差值计算方法 ========================
    /**
     * 计算两个日期的天数差（忽略时间，endDate - startDate）
     */
    public static long getDaysDiff(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        Date startDay = getDayStartTime(startDate);
        Date endDay = getDayStartTime(endDate);
        long diffMs = endDay.getTime() - startDay.getTime();
        return diffMs / (1000 * 60 * 60 * 24);
    }

    /**
     * 计算两个日期时间的小时差（endDate - startDate）
     */
    public static long getHoursDiff(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        long diffMs = endDate.getTime() - startDate.getTime();
        return diffMs / (1000 * 60 * 60);
    }


    // ======================== 资源释放（避免内存泄漏） ========================
    /**
     * 移除ThreadLocal中的SimpleDateFormat（Web场景建议在请求结束时调用）
     */
    public static void removeFormatters() {
        DATE_FORMATTER.remove();
        TIME_FORMATTER.remove();
        DATE_TIME_FORMATTER.remove();
    }
}