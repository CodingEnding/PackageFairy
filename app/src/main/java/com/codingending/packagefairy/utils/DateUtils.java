package com.codingending.packagefairy.utils;

import com.codingending.packagefairy.bean.StartEnd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 与日期相关的工具类
 * Created by CodingEnding on 2018/4/5.
 */

public class DateUtils {
    private static final String TAG="DateUtils";
    private DateUtils(){}

    //获得本月第一天0点时间（long的形式）
    public static long getFirstMonthDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    //获得本月第一天0点时间（Calendar的形式）
    public static Calendar getFirstMonthDayInCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal;
    }

    //获取今天是本月第几天（从1开始）
    public static int getTodayOfMonth(){
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    //获取今日的时间段
    public static StartEnd getTodayTimePart(){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        long start=calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        long end=calendar.getTimeInMillis();
        return new StartEnd(start,end);
    }

    //获取从本月第一天到现在的时间段列表
    public static List<StartEnd> getFirstMonthDayToNowTimeList(){
        List<StartEnd> timeList=new ArrayList<>();
        Calendar rootCalendar=getFirstMonthDayInCalendar();
        Calendar currentCalendar=Calendar.getInstance();//现在的时间
        int dayOfMonth=currentCalendar.get(Calendar.DAY_OF_MONTH);//获得今天是本月的第几天（从1开始）

        long start=0;
        long end=0;
        for(int i=0;i<dayOfMonth;i++){//循环获取每一天的时间段
            start=rootCalendar.getTimeInMillis();
            rootCalendar.add(Calendar.DAY_OF_MONTH,1);//每次向后跳转一天
            end=rootCalendar.getTimeInMillis();
            timeList.add(new StartEnd(start,end));
        }
        return timeList;
    }

    /**
     * 获取从指定时间到今天的时间段列表
     * @param startTime 开始时间
     * 注意开始时间可能是上月，但是在这种情况下系统会直接计算本月的流量数据，因此不必考虑开始时间到今天的时间段
     * 换句话说，如果本方法被触发，说明开始时间肯定是本月内的某一天（不过这样会使得DateUtils和逻辑模块耦合度过高）
     */
    public static List<StartEnd> getPeriodTimeList(long startTime){
        List<StartEnd> timeList=new ArrayList<>();
        Calendar currentCalendar=Calendar.getInstance();//现在的时间
        Calendar rootCalendar=Calendar.getInstance();
        rootCalendar.setTimeInMillis(startTime);
        rootCalendar.set(rootCalendar.get(Calendar.YEAR), rootCalendar.get(Calendar.MONTH), rootCalendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);//定位到开始时间当天的0时0分0秒
        LogUtils.i(TAG,"StartDate:"+rootCalendar.getTime());

        int startDayOfMonth=rootCalendar.get(Calendar.DAY_OF_MONTH);//获得开始时间是本月第几天
        int endDayOfMonth=currentCalendar.get(Calendar.DAY_OF_MONTH);//获得今天是本月的第几天（从1开始）
        LogUtils.i(TAG,"StartDay:"+rootCalendar.getTime());
        long start=0;
        long end=0;
        for(int i=startDayOfMonth-1;i<endDayOfMonth;i++){//循环获取每一天的时间段
            start=rootCalendar.getTimeInMillis();
            rootCalendar.add(Calendar.DAY_OF_MONTH,1);//每次向后跳转一天
            end=rootCalendar.getTimeInMillis();
            timeList.add(new StartEnd(start,end));
        }
        return timeList;
    }

    //判断两个时间戳对应的是否为同一天
    public static boolean isSameDay(long time1,long time2){
        Calendar calendar1=Calendar.getInstance();
        Calendar calendar2=Calendar.getInstance();
        calendar1.setTimeInMillis(time1);
        calendar2.setTimeInMillis(time2);

        return calendar1.get(Calendar.DAY_OF_MONTH)==calendar2.get(Calendar.DAY_OF_MONTH);
    }

}
