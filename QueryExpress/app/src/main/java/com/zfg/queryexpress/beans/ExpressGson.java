package com.zfg.queryexpress.beans;

import java.io.Serializable;

public class ExpressGson implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int express_id;
	private String express_no;
	private String company_name;
	private String express_time;
	private String company_img;
	private String express_remark;
	public ExpressGson() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ExpressGson(int express_id, String express_no, String company_name, String express_time,
			String company_img,String express_remark) {
		super();
		this.express_id = express_id;
		this.express_no = express_no;
		this.company_name = company_name;
		this.express_time = express_time;
		this.company_img = company_img;
		this.express_remark = express_remark;
	}
	public int getExpress_id() {
		return express_id;
	}
	public void setExpress_id(int express_id) {
		this.express_id = express_id;
	}
	public String getExpress_no() {
		return express_no;
	}
	public void setExpress_no(String express_no) {
		this.express_no = express_no;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getExpress_time() {
		return express_time;
	}
	public void setExpress_time(String express_time) {
		this.express_time = express_time;
	}
	public String getCompany_img() {
		return company_img;
	}
	public void setCompany_img(String company_img) {
		this.company_img = company_img;
	}
	public String getExpress_remark() {
		return express_remark;
	}
	public void setExpress_remark(String express_remark) {
		this.express_remark = express_remark;
	}
	
	@Override
	public String toString() {
		return "ExpressGson [express_id=" + express_id + ", express_no=" + express_no + ", company_name=" + company_name
				+ ", express_time=" + express_time + ", express_remark=" + express_remark + "]";
	}
	

}
