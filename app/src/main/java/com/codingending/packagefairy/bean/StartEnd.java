package com.codingending.packagefairy.bean;

/**
 * 封装开始时间点和结束时间点
 * 用于计算每一天时间段内的流量消耗
 * Created by CodingEnding on 2018/4/5.
 */

public class StartEnd {
    public long start;//开始时间
    public long end;//结束时间

    public StartEnd(long start, long end) {
        this.start = start;
        this.end = end;
    }
}
