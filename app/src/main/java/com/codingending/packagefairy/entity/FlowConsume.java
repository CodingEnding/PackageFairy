package com.codingending.packagefairy.entity;

import com.codingending.packagefairy.po.FlowConsumePO;
import com.codingending.packagefairy.utils.LogUtils;
import com.google.gson.annotations.SerializedName;

/**
 * 用户应用流量消耗的JSON实体类
 * @author CodingEnding
 */
public class FlowConsume {
	/**
	 * {"app_name":"QQ","app_flow":"160"}
	 */
	@SerializedName("app_name")
	private String appName;
	@SerializedName("app_flow")
	private int appFlow;//流量消耗（MB）

    /**
     * 快速构建方法
     */
	public static FlowConsume build(FlowConsumePO flowConsumePO){
	    FlowConsume flowConsume=new FlowConsume();
	    flowConsume.setAppName(flowConsumePO.getAppName());
	    flowConsume.setAppFlow(flowConsumePO.getFlowAmount()/1024);
		//LogUtils.i("FlowConsume",flowConsume.getAppName()+":"+flowConsume.getAppFlow());
	    return flowConsume;
    }
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public int getAppFlow() {
		return appFlow;
	}
	public void setAppFlow(int appFlow) {
		this.appFlow = appFlow;
	}
	
}
