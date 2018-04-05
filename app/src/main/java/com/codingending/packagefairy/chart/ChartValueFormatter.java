package com.codingending.packagefairy.chart;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * 流量走势图中流量值的格式化类
 * Created by CodingEnding on 2018/4/5.
 */

public class ChartValueFormatter implements IValueFormatter{

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return Float.valueOf(value).intValue()+"M";//转化为类似5M这样的格式
    }
}
