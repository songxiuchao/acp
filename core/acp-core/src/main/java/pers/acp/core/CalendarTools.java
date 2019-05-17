package pers.acp.core;

import org.joda.time.DateTime;
import pers.acp.core.task.timer.container.Calculation;

public final class CalendarTools {

    /**
     * 根据时间戳获取日期对象
     *
     * @param instant 时间戳
     * @return 日期对象
     */
    public static DateTime getCalendar(long instant) {
        return Calculation.getCalendar(instant);
    }

    /**
     * 获取日期对象
     *
     * @param dateTimeStr 日期字符串（yyyy-MM-dd）
     * @return 日期对象
     */
    public static DateTime getCalendar(String dateTimeStr) {
        return Calculation.getCalendar(dateTimeStr);
    }

    /**
     * 获取日期对象
     *
     * @param dateTimeStr    日期字符串
     * @param dateTimeFormat 格式字符串
     * @return 日期对象
     */
    public static DateTime getCalendar(String dateTimeStr, String dateTimeFormat) {
        return Calculation.getCalendar(dateTimeStr, dateTimeFormat);
    }

    /**
     * 获取指定日期的后一天
     *
     * @param dateTime 日期对象
     * @return 日期对象
     */
    public static DateTime getNextDay(DateTime dateTime) {
        return Calculation.getNextDay(dateTime);
    }

    /**
     * 获取指定日期前一天
     *
     * @param dateTime 日期对象
     * @return 日期对象
     */
    public static DateTime getPrevDay(DateTime dateTime) {
        return Calculation.getPrevDay(dateTime);
    }

    /**
     * 获取指定日期是一周中第几天
     *
     * @param dateTime 日期对象
     * @return 结果（1-7）,其中sunday是7
     */
    public static int getWeekNo(DateTime dateTime) {
        return Calculation.getWeekNo(dateTime);
    }

    /**
     * 获取指定日期日号
     *
     * @param dateTime 日期对象
     * @return 日号（1-31）
     */
    public static int getDayNo(DateTime dateTime) {
        return Calculation.getDayNo(dateTime);
    }

    /**
     * 获取指定月号
     *
     * @param dateTime 日期对象
     * @return 月号（1-12）
     */
    public static int getMonthNo(DateTime dateTime) {
        return Calculation.getMonthNo(dateTime);
    }

    /**
     * 获取指定月所在季度内的月号
     *
     * @param dateTime 日期对象
     * @return 季度号（1, 2, 3, 4）
     */
    public static int getMonthNoInQuarter(DateTime dateTime) {
        return Calculation.getMonthNoInQuarter(dateTime);
    }

    /**
     * 获取指定月最后一天日号
     *
     * @param dateTime 日期对象
     * @return 日号
     */
    public static int getLastDayInMonthNo(DateTime dateTime) {
        return Calculation.getLastDayInMonthNo(dateTime);
    }

    /**
     * 判断当前时间是否是工作日
     *
     * @param dateTime 日期对象
     * @return 是否是工作日
     */
    public static boolean isWeekDay(DateTime dateTime) {
        return Calculation.isWeekDay(dateTime);
    }

    /**
     * 判断当前时间是否是周末
     *
     * @param dateTime 日期对象
     * @return 是否是周末
     */
    public static boolean isWeekend(DateTime dateTime) {
        return Calculation.isWeekend(dateTime);
    }

    /**
     * 以日为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    public static boolean validateDay(DateTime now, DateTime contrast, String rule) {
        return Calculation.validateDay(now, contrast, rule);
    }

    /**
     * 以周为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    public static boolean validateWeek(DateTime now, DateTime contrast, String rule) {
        return Calculation.validateWeek(now, contrast, rule);
    }

    /**
     * 以月为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    public static boolean validateMonth(DateTime now, DateTime contrast, String rule) {
        return Calculation.validateMonth(now, contrast, rule);
    }

    /**
     * 以季度为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    public static boolean validateQuarter(DateTime now, DateTime contrast, String rule) {
        return Calculation.validateQuarter(now, contrast, rule);
    }

    /**
     * 以年为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    public static boolean validateYear(DateTime now, DateTime contrast, String rule) {
        return Calculation.validateYear(now, contrast, rule);
    }

}
