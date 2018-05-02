package com.codingending.packagefairy.entity;

import android.graphics.drawable.Drawable;

import com.codingending.packagefairy.po.FlowConsumePO;

/**
 * App实体类（用于应用流量排行列表）
 * Created by CodingEnding on 2018/4/10.
 */
public class AppBean {
    private String appName;
    private String packageName;//包名
    private int flowAmount;//流量消耗（KB）
    private Drawable appIcon;//图标

    /**
     * 快速构造AppBean
     */
    public static AppBean build(FlowConsumePO flowConsumePO,Drawable appIcon){
       return new AppBean(flowConsumePO.getAppName(),flowConsumePO.getPackageName(),
                flowConsumePO.getFlowAmount(),appIcon);
    }

    public AppBean() {
    }

    public AppBean(String appName, String packageName, int flowAmount) {
        this.appName = appName;
        this.packageName = packageName;
        this.flowAmount = flowAmount;
    }

    public AppBean(String appName, String packageName, int flowAmount, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.flowAmount = flowAmount;
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getFlowAmount() {
        return flowAmount;
    }

    public void setFlowAmount(int flowAmount) {
        this.flowAmount = flowAmount;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
