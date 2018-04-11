package com.codingending.packagefairy.entity;

/**
 * 服务器的基础响应对象
 * 服务器的返回值都是json对象
 * @author CodignEnding
 */
public class BaseResponse {
	protected boolean succeed;//本次操作是否成功
	protected int code;//状态码
	
	public BaseResponse(boolean succeed, int code) {
		this.succeed = succeed;
		this.code = code;
	}
	
	public boolean isSucceed() {
		return succeed;
	}

	public void setSucceed(boolean succeed) {
		this.succeed = succeed;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
