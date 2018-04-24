package com.codingending.packagefairy.entity;

/**
 * 用户设备信息的JSON实体类
 * Created by CodingEnding on 2018/4/23.
 */
public class DeviceBean {
    private int id;
    private String deviceType;//机型
    private String systemVersion;//系统版本
    private String deviceFinger;//设备唯一识别码
    private int userId;//（外键）用户Id（可能为空）

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public String getSystemVersion() {
        return systemVersion;
    }
    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }
    public String getDeviceFinger() {
        return deviceFinger;
    }
    public void setDeviceFinger(String deviceFinger) {
        this.deviceFinger = deviceFinger;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

}