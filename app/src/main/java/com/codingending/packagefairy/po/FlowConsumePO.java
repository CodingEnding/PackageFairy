package com.codingending.packagefairy.po;

/**
 * 对应数据库中的FlowConsume表
 * Created by CodingEnding on 2018/4/6.
 */

public class FlowConsumePO {
    private int id;
    private String appName;//应用名称
    private String packageName;//包名
    private int flowAmount;//流量消耗（单位KB）
    private int day;//日数
    private int month;//月数
    private int year;//年份

    @Override
    public String toString() {
        return appName+":"+(flowAmount/1024.0)+"MB";
    }

    public FlowConsumePO() {
    }

    public FlowConsumePO(String appName, String packageName, int flowAmount,
                         int day, int month, int year) {
        this.appName = appName;
        this.packageName = packageName;
        this.flowAmount = flowAmount;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getFlowAmount() {
        return flowAmount;
    }

    public void setFlowAmount(int flowAmount) {
        this.flowAmount = flowAmount;
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
