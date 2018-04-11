package com.codingending.packagefairy.entity;

/**
 * 封装较少信息的套餐实体类
 * @author CodingEnding
 */
public class SimplePackageBean {
	private int id;
	private String name;
	private String partner;//合作方
	private String operator;//运营商
	private double star;//评分
	private int freeFlowType;//是否有免流范围
	private int monthRent;//月租
	
	public SimplePackageBean() {
	}
	
	public SimplePackageBean(int id, String name, String partner,String operator,
                             double star, int freeFlowType,int monthRent) {
		this.id = id;
		this.name = name;
		this.partner = partner;
		this.operator = operator;
		this.star = star;
		this.freeFlowType = freeFlowType;
		this.monthRent=monthRent;
	}

	public int getMonthRent() {
		return monthRent;
	}
	public void setMonthRent(int monthRent) {
		this.monthRent = monthRent;
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
	public double getStar() {
		return star;
	}
	public void setStar(double star) {
		this.star = star;
	}
	public int getFreeFlowType() {
		return freeFlowType;
	}
	public void setFreeFlowType(int freeFlowType) {
		this.freeFlowType = freeFlowType;
	}
	
}
