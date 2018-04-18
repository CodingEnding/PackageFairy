package com.codingending.packagefairy.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 提供给客户端使用的套餐JSON实体类
 * @author CodingEnding
 */
public class PackageBean implements Parcelable{
	private int id;
	private String name;
	private String partner;//合作方
	private String operator;//运营商
	private int monthRent;//月租
	private int packageCountryFlow;//套餐内国内流量（M）
	private int packageProvinceFlow;//套餐内省内流量（M）
	private int packageCall;//套餐内通话时长
	private double extraPackageCall;//套餐外通话费用（元/min）
	private double extraCountryFlow;//套餐外全国流量（元/M）
	private double extraProvinceFlow;//套餐外省内流量（元/M）
	private double extraProvinceOutFlow;//套餐外省外流量（元/M）
	private int extraCountryDayRent;//套餐外全国日租（元）
	private int extraCountryDayFlow;//套餐外全国日租包含流量（M）
	private int extraProvinceInDayRent;//套餐外省内日租（元）
	private int extraProvinceInDayFlow;//套餐外省内日租包含流量（M）
	private int extraProvinceOutDayRent;//套餐外省外日租（元）
	private int extraProvinceOutDayFlow;//套餐外省外日租包含流量（M）
	private int extraFlowType;//套餐外流量的计费方式
	private String privilegeDescription;//特权描述
	private double star;//评分
	private String url;//官方的说明链接
	private String remark;//备注
	private int abandon;//是否停办
	private int freeFlowType;//是否有免流范围
	private double totalConsume;//每月的预计消费

    public PackageBean(){}

    protected PackageBean(Parcel in) {
        id = in.readInt();
        name = in.readString();
        partner = in.readString();
        operator = in.readString();
        monthRent = in.readInt();
        packageCountryFlow = in.readInt();
        packageProvinceFlow = in.readInt();
        packageCall = in.readInt();
        extraPackageCall = in.readDouble();
        extraCountryFlow = in.readDouble();
        extraProvinceFlow = in.readDouble();
        extraProvinceOutFlow = in.readDouble();
        extraCountryDayRent = in.readInt();
        extraCountryDayFlow = in.readInt();
        extraProvinceInDayRent = in.readInt();
        extraProvinceInDayFlow = in.readInt();
        extraProvinceOutDayRent = in.readInt();
        extraProvinceOutDayFlow = in.readInt();
        extraFlowType = in.readInt();
        privilegeDescription = in.readString();
        star = in.readDouble();
        url = in.readString();
        remark = in.readString();
        abandon = in.readInt();
        freeFlowType = in.readInt();
        totalConsume = in.readDouble();
    }

    public static final Creator<PackageBean> CREATOR = new Creator<PackageBean>() {
        @Override
        public PackageBean createFromParcel(Parcel in) {
            return new PackageBean(in);
        }

        @Override
        public PackageBean[] newArray(int size) {
            return new PackageBean[size];
        }
    };

    @Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(partner);
        dest.writeString(operator);
        dest.writeInt(monthRent);
        dest.writeInt(packageCountryFlow);
        dest.writeInt(packageProvinceFlow);
        dest.writeInt(packageCall);
        dest.writeDouble(extraPackageCall);
        dest.writeDouble(extraCountryFlow);
        dest.writeDouble(extraProvinceFlow);
        dest.writeDouble(extraProvinceOutFlow);
        dest.writeInt(extraCountryDayRent);
        dest.writeInt(extraCountryDayFlow);
        dest.writeInt(extraProvinceInDayRent);
        dest.writeInt(extraProvinceInDayFlow);
        dest.writeInt(extraProvinceOutDayRent);
        dest.writeInt(extraProvinceOutDayFlow);
        dest.writeInt(extraFlowType);
        dest.writeString(privilegeDescription);
        dest.writeDouble(star);
        dest.writeString(url);
        dest.writeString(remark);
        dest.writeInt(abandon);
        dest.writeInt(freeFlowType);
        dest.writeDouble(totalConsume);
    }
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public int getMonthRent() {
		return monthRent;
	}
	public void setMonthRent(int monthRent) {
		this.monthRent = monthRent;
	}
	public int getPackageCountryFlow() {
		return packageCountryFlow;
	}
	public void setPackageCountryFlow(int packageCountryFlow) {
		this.packageCountryFlow = packageCountryFlow;
	}
	public int getPackageProvinceFlow() {
		return packageProvinceFlow;
	}
	public void setPackageProvinceFlow(int packageProvinceFlow) {
		this.packageProvinceFlow = packageProvinceFlow;
	}
	public int getPackageCall() {
		return packageCall;
	}
	public void setPackageCall(int packageCall) {
		this.packageCall = packageCall;
	}
	public double getExtraPackageCall() {
		return extraPackageCall;
	}
	public void setExtraPackageCall(double extraPackageCall) {
		this.extraPackageCall = extraPackageCall;
	}
	public double getExtraCountryFlow() {
		return extraCountryFlow;
	}
	public void setExtraCountryFlow(double extraCountryFlow) {
		this.extraCountryFlow = extraCountryFlow;
	}
	public double getExtraProvinceFlow() {
		return extraProvinceFlow;
	}
	public void setExtraProvinceFlow(double extraProvinceFlow) {
		this.extraProvinceFlow = extraProvinceFlow;
	}
	public double getExtraProvinceOutFlow() {
		return extraProvinceOutFlow;
	}
	public void setExtraProvinceOutFlow(double extraProvinceOutFlow) {
		this.extraProvinceOutFlow = extraProvinceOutFlow;
	}
	public int getExtraCountryDayRent() {
		return extraCountryDayRent;
	}
	public void setExtraCountryDayRent(int extraCountryDayRent) {
		this.extraCountryDayRent = extraCountryDayRent;
	}
	public int getExtraCountryDayFlow() {
		return extraCountryDayFlow;
	}
	public void setExtraCountryDayFlow(int extraCountryDayFlow) {
		this.extraCountryDayFlow = extraCountryDayFlow;
	}
	public int getExtraProvinceInDayRent() {
		return extraProvinceInDayRent;
	}
	public void setExtraProvinceInDayRent(int extraProvinceInDayRent) {
		this.extraProvinceInDayRent = extraProvinceInDayRent;
	}
	public int getExtraProvinceInDayFlow() {
		return extraProvinceInDayFlow;
	}
	public void setExtraProvinceInDayFlow(int extraProvinceInDayFlow) {
		this.extraProvinceInDayFlow = extraProvinceInDayFlow;
	}
	public int getExtraProvinceOutDayRent() {
		return extraProvinceOutDayRent;
	}
	public void setExtraProvinceOutDayRent(int extraProvinceOutDayRent) {
		this.extraProvinceOutDayRent = extraProvinceOutDayRent;
	}
	public int getExtraProvinceOutDayFlow() {
		return extraProvinceOutDayFlow;
	}
	public void setExtraProvinceOutDayFlow(int extraProvinceOutDayFlow) {
		this.extraProvinceOutDayFlow = extraProvinceOutDayFlow;
	}
	public int getExtraFlowType() {
		return extraFlowType;
	}
	public void setExtraFlowType(int extraFlowType) {
		this.extraFlowType = extraFlowType;
	}
	public String getPrivilegeDescription() {
		return privilegeDescription;
	}
	public void setPrivilegeDescription(String privilegeDescription) {
		this.privilegeDescription = privilegeDescription;
	}
	public double getStar() {
		return star;
	}
	public void setStar(double star) {
		this.star = star;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getAbandon() {
		return abandon;
	}
	public void setAbandon(int abandon) {
		this.abandon = abandon;
	}
	public int getFreeFlowType() {
		return freeFlowType;
	}
	public void setFreeFlowType(int freeFlowType) {
		this.freeFlowType = freeFlowType;
	}
	public double getTotalConsume() {
		return totalConsume;
	}
	public void setTotalConsume(double totalConsume) {
		this.totalConsume = totalConsume;
	}
}
