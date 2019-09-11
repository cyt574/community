package com.yichen.community.utils;

public class TimeUtils {
    //one year time 1000*60*60*24*365L
    public final static Long ONE_YEAR_TIME = 1000*60*60*24*365L;

    //one month time 1000*60*60*24*30L
    public final static Long ONE_MONTH_TIME = 1000*60*60*24*30L;

    //15 d time
    public final static Long FIFTEEN_DAY_TIME = 1000*60*60*24*15L;

    //one week time 1000*60*60*24*7L
    public final static Long ONE_WEEK_TIME = 1000*60*60*24*7L;

    //one day time 1000*60*60*24L
    public final static Long ONE_DAY_TIME = 1000*60*60*24L;

    //one hour time 1000*60*60L
    public final static Long ONE_HOUR_TIME = 1000*60*60L;

    //one minute time 1000*60L
    public final static Long ONE_MINUTE_TIME = 1000*60L;



    public static String getFormatedDiffTime (Long earlyTime, Long nowTime) {
        Long diffTime = nowTime - earlyTime;
       if ( diffTime < ONE_MINUTE_TIME) {
           return "" + formateTimeBySecond(diffTime) + "秒前";
       } else if( diffTime < ONE_HOUR_TIME) {
           return "" + formateTimeByMinute(diffTime) + "分钟前";
       } else if ( diffTime < ONE_DAY_TIME) {
           return "" + formateTimeByHour(diffTime) + "小时前";
       } else if ( diffTime < ONE_WEEK_TIME) {
           return "" + formateTimeByDay(diffTime) + "天前";
       } else if ( diffTime < ONE_MONTH_TIME) {
           return "" + formateTimeByWeek(diffTime) + "星期前";
       } else if ( diffTime < ONE_YEAR_TIME) {
           return "" + formateTimeByMonth(diffTime) + "个月前";
       } else {
           return "" + formateTimeByYear(diffTime) + "年前";
       }
    }

    public static Long formateTimeByYear(Long time) {
        return time/ONE_YEAR_TIME;
    }

    public static Long formateTimeByMonth(Long time) {
        return time/ONE_MONTH_TIME;
    }


    public static Long formateTimeByWeek(Long time) {
        return time/ONE_WEEK_TIME;
    }

    public static Long formateTimeByDay(Long time) {
        return time/ONE_DAY_TIME;
    }

    public static Long formateTimeByHour(Long time) {
        return time/ONE_HOUR_TIME;
    }

    public static Long formateTimeByMinute(Long time) {
        return time/ONE_MINUTE_TIME;
    }

    public static Long formateTimeBySecond(Long time) {
        return time/1000L;
    }
}
