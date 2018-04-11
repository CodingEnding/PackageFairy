package com.codingending.packagefairy.entity;

/**
 * 套餐外流量计费方式 （ExtraFlowType）
 * @author CodingEnding
 */
public class ExtraFlowType {
	private int id;
	private int type;//计费方式标识
	private String remark;//备注

    public static final int EXTRA_FLOW_TYPE_ONE=1;//形式：全国流量10元/GB
    public static final int EXTRA_FLOW_TYPE_TWO=2;//形式：全国日租1元800M
    public static final int EXTRA_FLOW_TYPE_THREE=3;//形式：省内日租1元800M 省外日租2元800M
    public static final int EXTRA_FLOW_TYPE_FOUR=4;//形式：省内日租1元800M 省外流量0.1元/GB
    public static final int EXTRA_FLOW_TYPE_FIVE=5;//形式：全国无限流量
    public static final int EXTRA_FLOW_TYPE_SIX=6;//形式：全国日租3元无限流量
    public static final int EXTRA_FLOW_TYPE_SEVEN=7;//形式：省内日租3元无限流量 省外日租2元800M
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
