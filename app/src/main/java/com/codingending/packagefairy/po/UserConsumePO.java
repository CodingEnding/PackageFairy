package com.codingending.packagefairy.po;

/**
 * 对应数据库中的UserConsume表
 * Created by CodingEnding on 2018/4/6.
 */

public class UserConsumePO {
    private int id;
    private int callTime;//通话时长
    private int allFlow;//流量消耗（单位M）
    private int day;//日数
    private int month;//月数
    private int year;//年份

    public UserConsumePO(){}

    public UserConsumePO(int callTime, int allFlow, int day, int month, int year) {
        this.callTime = callTime;
        this.allFlow = allFlow;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCallTime() {
        return callTime;
    }

    public void setCallTime(int callTime) {
        this.callTime = callTime;
    }

    public int getAllFlow() {
        return allFlow;
    }

    public void setAllFlow(int allFlow) {
        this.allFlow = allFlow;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
