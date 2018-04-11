package com.codingending.packagefairy.entity;

/**
 * 服务器携带数据的返回对象（列表或单个对象）
 * 服务器的返回值都是json对象
 * @author CodignEnding
 */
public class DataResponse<T> extends BaseResponse{
	protected T data;//真正的数据部分

	public DataResponse(boolean succeed, int code, T data) {
		super(succeed,code);
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
}
