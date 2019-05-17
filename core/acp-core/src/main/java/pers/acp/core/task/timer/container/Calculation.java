package pers.acp.core.task.timer.container;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import pers.acp.core.exceptions.TimerException;
import pers.acp.core.task.timer.ruletype.CircleType;

/**
 * 日历计算类
 *
 * @author zhangbin
 */
public final class Calculation {

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * yyyy-MM-dd
     */
    public static String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * HH:mm:ss
     */
    public static String TIME_FORMAT = "HH:mm:ss";

    /**
     * 根据时间戳获取日期对象
     *
     * @param instant 时间戳
     * @return 日期对象
     */
    public static DateTime getCalendar(long instant) {
        return new DateTime(instant);
    }

    /**
     * 获取日期对象
     *
     * @param dateTimeStr 日期字符串（yyyy-MM-dd）
     * @return 日期对象
     */
    public static DateTime getCalendar(String dateTimeStr) {
        return getCalendar(dateTimeStr, Calculation.DATE_FORMAT);
    }

    /**
     * 获取日期对象
     *
     * @param dateTimeStr    日期字符串
     * @param dateTimeFormat 格式字符串
     * @return 日期对象
     */
    public static DateTime getCalendar(String dateTimeStr, String dateTimeFormat) {
        return DateTimeFormat.forPattern(dateTimeFormat).parseDateTime(dateTimeStr);
    }

    /**
     * 获取指定日期的后一天
     *
     * @param dateTime 日期对象
     * @return 日期对象
     */
    public static DateTime getNextDay(DateTime dateTime) {
        return dateTime.plusDays(1);
    }

    /**
     * 获取指定日期前一天
     *
     * @param dateTime 日期对象
     * @return 日期对象
     */
    public static DateTime getPrevDay(DateTime dateTime) {
        return dateTime.minusDays(1);
    }

    /**
     * 获取指定日期是一周中第几天
     *
     * @param dateTime 日期对象
     * @return 结果（1-7）,其中sunday是7
     */
    public static int getWeekNo(DateTime dateTime) {
        return dateTime.getDayOfWeek();
    }

    /**
     * 获取指定日期日号
     *
     * @param dateTime 日期对象
     * @return 日号（1-31）
     */
    public static int getDayNo(DateTime dateTime) {
        return dateTime.getDayOfMonth();
    }

    /**
     * 获取指定月号
     *
     * @param dateTime 日期对象
     * @return 月号（1-12）
     */
    public static int getMonthNo(DateTime dateTime) {
        return dateTime.getMonthOfYear();
    }

    /**
     * 获取指定月所在季度内的月号
     *
     * @param dateTime 日期对象
     * @return 季度号（1, 2, 3, 4）
     */
    public static int getMonthNoInQuarter(DateTime dateTime) {
        int month = getMonthNo(dateTime);
        if (month >= 1 && month <= 12) {
            return ((month + 2) / 3);
        } else {
            return -1;
        }
    }

    /**
     * 获取指定月最后一天日号
     *
     * @param dateTime 日期对象
     * @return 日号
     */
    public static int getLastDayInMonthNo(DateTime dateTime) {
        return dateTime.dayOfMonth().withMaximumValue().getDayOfMonth();
    }

    /**
     * 判断当前时间是否是工作日
     *
     * @param dateTime 日期对象
     * @return 是否是工作日
     */
    public static boolean isWeekDay(DateTime dateTime) {
        int nowIndex = getWeekNo(dateTime);
        return nowIndex < 6;
    }

    /**
     * 判断当前时间是否是周末
     *
     * @param dateTime 日期对象
     * @return 是否是周末
     */
    public static boolean isWeekend(DateTime dateTime) {
        int nowIndex = getWeekNo(dateTime);
        return nowIndex > 5;
    }

    /**
     * 获取定时器参数
     *
     * @param circleType 周期
     * @param rules      规则
     * @return long[]：[0]long-initialDelay开始执行延迟时间,[1]long-period执行间隔时间
     */
    public static long[] getTimerParam(CircleType circleType, String rules) throws TimerException {
        DateTime now = new DateTime();
        long[] param = new long[2];
        String[] rule = StringUtils.splitPreserveAllTokens(rules, "|");
        try {
            if (circleType.equals(CircleType.Time)) {
                if (rule.length == 2 || rule.length == 1) {
                    if (rule.length == 1) {
                        param[0] = now.toDate().getTime();
                        param[1] = Long.valueOf(rule[0]);
                    } else {
                        String time = now.toString(DATE_FORMAT) + " " + rule[0];
                        DateTime dateTime = getCalendar(time, DATETIME_FORMAT);
                        param[0] = dateTime.toDate().getTime();
                        param[1] = Long.valueOf(rule[1]);
                    }
                } else {
                    throw new TimerException("circleType is not support（circleType=" + CircleType.Time.getName() + ",rules=" + rules + "）");
                }
            } else if (circleType.equals(CircleType.Day)) {
                if (rule.length == 1) {
                    DateTime nextDay = getNextDay(now);
                    String time = nextDay.toString(DATE_FORMAT) + " " + rule[0];
                    DateTime dateTime = getCalendar(time, DATETIME_FORMAT);
                    param[0] = dateTime.toDate().getTime();
                } else {
                    throw new TimerException("circleType is not support（circleType=" + CircleType.Day.getName() + ",rules=" + rules + "）");
                }
                param[1] = (long) (1000 * 60 * 60 * 24);
            } else if (circleType.equals(CircleType.Week) || circleType.equals(CircleType.Month)) {
                if (rule.length == 2) {
                    DateTime nextDay = getNextDay(now);
                    String time = nextDay.toString(DATE_FORMAT) + " " + rule[0];
                    DateTime dateTime = getCalendar(time, DATETIME_FORMAT);
                    param[0] = dateTime.toDate().getTime();
                } else {
                    throw new TimerException("circleType is not support（circleType=" + CircleType.Week.getName() + ",rules=" + rules + "）");
                }
                param[1] = (long) (1000 * 60 * 60 * 24);
            } else if (circleType.equals(CircleType.Quarter) || circleType.equals(CircleType.Year)) {
                if (rule.length == 3) {
                    DateTime nextDay = getNextDay(now);
                    String time = nextDay.toString(DATE_FORMAT) + " " + rule[0];
                    DateTime dateTime = getCalendar(time, DATETIME_FORMAT);
                    param[0] = dateTime.toDate().getTime();
                } else {
                    throw new TimerException("circleType is not support. circleType=" + CircleType.Week.getName() + ",rules=" + rules + "）");
                }
                param[1] = (long) (1000 * 60 * 60 * 24);
            } else {
                throw new TimerException("circleType is not support（circleType error）");
            }
            if (param[0] - now.toDate().getTime() > 0) {
                param[0] = param[0] - now.toDate().getTime();
            } else {
                param[0] = 0;
            }
            return param;
        } catch (Exception e) {
            throw new TimerException("getTimerParam failed:" + e.getMessage());
        }
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
        String[] rules = StringUtils.splitPreserveAllTokens(rule, "|");
        boolean isExecute = false;
        if (rules.length == 1) {
            // 参照日期后一天
            String afterDay = getNextDay(contrast).toString(DATE_FORMAT) + " " + rules[0];
            // 当前日期和时间
            String nowDay = now.toString(DATETIME_FORMAT);
            if (nowDay.compareTo(afterDay) >= 0) {
                isExecute = true;
            }
        }
        return isExecute;
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
        String[] rules = StringUtils.splitPreserveAllTokens(rule, "|");
        boolean isExecute = false;
        if (rules.length == 2) {
            // 参照日期后一天0点
            String afterDay = getNextDay(contrast).toString(DATE_FORMAT) + " 00:00:00";
            // 当前日期和时间
            String nowDay = now.toString(DATETIME_FORMAT);
            // 当前时间
            String nowTime = now.toString(TIME_FORMAT);

            int dayIndex = Integer.valueOf(rules[0]);// 一周中第几天
            int nowIndex = getWeekNo(now);// 当前日期是一周中第几天
            // 比上次发送时间至少晚一天，符合一周中第几天，等于或超过配置的发送时间
            if (nowDay.compareTo(afterDay) >= 0 && dayIndex == nowIndex && nowTime.compareTo(rules[1]) >= 0) {
                isExecute = true;
            }
        }
        return isExecute;
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
        String[] rules = StringUtils.splitPreserveAllTokens(rule, "|");
        boolean isExecute = false;
        if (rules.length == 2) {
            // 参照日期后一天0点
            String afterDay = getNextDay(contrast).toString(DATE_FORMAT) + " 00:00:00";
            // 当前日期和时间
            String nowDay = now.toString(DATETIME_FORMAT);
            // 当前时间
            String nowTime = now.toString(TIME_FORMAT);

            int dayIndex = Integer.valueOf(rules[0]);// 日号
            int nowIndex = getDayNo(now);// 当前日期是几号
            int maxDay = getLastDayInMonthNo(now);// 当前日期所在月最后一天
            if (maxDay < dayIndex) {
                dayIndex = maxDay;
            }
            // 比上次发送时间至少晚一天，符合月中日期，等于或超过配置的发送时间
            if (nowDay.compareTo(afterDay) >= 0 && dayIndex == nowIndex && nowTime.compareTo(rules[1]) >= 0) {
                isExecute = true;
            }
        }
        return isExecute;
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
        String[] rules = StringUtils.splitPreserveAllTokens(rule, "|");
        boolean isExecute = false;
        if (rules.length == 3) {
            // 参照日期后一天0点
            String afterDay = getNextDay(contrast).toString(DATE_FORMAT) + " 00:00:00";

            int dayIndex = Integer.valueOf(rules[1]);// 日号
            int nowIndex = getDayNo(now);// 当前日期是几号
            int maxDay = getLastDayInMonthNo(now);// 当前日期所在月最后一天
            if (maxDay < dayIndex) {
                dayIndex = maxDay;
            }
            // 比上次发送时间至少晚一天，符合季度内月号，符合月内日号，等于或超过配置的发送时间
            if (now.toString(DATETIME_FORMAT).compareTo(afterDay) >= 0 && getMonthNoInQuarter(now) == Integer.valueOf(rules[0]) && dayIndex == nowIndex && now.toString(TIME_FORMAT).compareTo(rules[2]) >= 0) {
                isExecute = true;
            }
        }
        return isExecute;
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
        String[] rules = StringUtils.splitPreserveAllTokens(rule, "|");
        boolean isExecute = false;
        if (rules.length == 3) {
            // 参照日期后一天0点
            String afterDay = getNextDay(contrast).toString(DATE_FORMAT) + " 00:00:00";

            int dayIndex = Integer.valueOf(rules[1]);// 日号
            int nowIndex = getDayNo(now);// 当前日期是几号
            int maxDay = getLastDayInMonthNo(now);// 当前日期所在月最后一天
            if (maxDay < dayIndex) {
                dayIndex = maxDay;
            }
            // 比上次发送时间至少晚一天，符合月号，符合月内日号，等于或超过配置的发送时间
            if (now.toString(DATETIME_FORMAT).compareTo(afterDay) >= 0 && getMonthNo(now) == Integer.valueOf(rules[0]) && dayIndex == nowIndex && now.toString(TIME_FORMAT).compareTo(rules[2]) >= 0) {
                isExecute = true;
            }
        }
        return isExecute;
    }

}
