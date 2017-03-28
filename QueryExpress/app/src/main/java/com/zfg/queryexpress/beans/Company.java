package com.zfg.queryexpress.beans;

import java.io.Serializable;

public class Company implements Serializable{

	/**
	 * 快递公司实体
	 */
	private static final long serialVersionUID = 1L;
	private int company_id;// int PRIMARY KEY AUTO_INCREMENT,
	private String company_code;// VARCHAR(10),##快递公司简称
	private String company_name;// varchar(20),##快递公司名称
	private String company_img;//  varchar(200),##快递图片
	private String company_phone;// VARCHAR(20),##电话
	private String company_remark;// varchar(100)##备注
	public Company() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Company(int company_id, String company_code, String company_name, String company_img, String company_phone,
			String company_remark) {
		super();
		this.company_id = company_id;
		this.company_code = company_code;
		this.company_name = company_name;
		this.company_img = company_img;
		this.company_phone = company_phone;
		this.company_remark = company_remark;
	}
	public Company(String company_code, String company_name, String company_img, String company_phone,
			String company_remark) {
		super();
		this.company_code = company_code;
		this.company_name = company_name;
		this.company_img = company_img;
		this.company_phone = company_phone;
		this.company_remark = company_remark;
	}
	public int getCompany_id() {
		return company_id;
	}
	public void setCompany_id(int company_id) {
		this.company_id = company_id;
	}
	public String getCompany_code() {
		return company_code;
	}
	public void setCompany_code(String company_code) {
		this.company_code = company_code;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getCompany_img() {
		return company_img;
	}
	public void setCompany_img(String company_img) {
		this.company_img = company_img;
	}
	public String getCompany_phone() {
		return company_phone;
	}
	public void setCompany_phone(String company_phone) {
		this.company_phone = company_phone;
	}
	public String getCompany_remark() {
		return company_remark;
	}
	public void setCompany_remark(String company_remark) {
		this.company_remark = company_remark;
	}
	@Override
	public String toString() {
		return "Company [company_id=" + company_id + ", company_code=" + company_code + ", company_name=" + company_name
				+ ", company_img=" + company_img + ", company_phone=" + company_phone + ", company_remark="
				+ company_remark + "]";
	}
}
