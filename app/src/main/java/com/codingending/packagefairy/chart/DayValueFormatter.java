package com.codingending.packagefairy.chart;

import com.codingending.packagefairy.utils.DateUtils;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * 流量走势图的横轴格式化类
 * Created by CodingEnding on 2018/4/5.
 */

public class DayValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        float todayOfMonth=DateUtils.getTodayOfMonth()+0f;//获取今天是本月第几天（转化为Float）
        if(Float.compare(value+1,todayOfMonth)==0){//判断当前横轴坐标是否为今天
            return "今天";
        }
        return String.valueOf(Float.valueOf(value+1).intValue());//否则将横坐标+1显示（因为横坐标从0开始，而天数从1开始）
    }
}
