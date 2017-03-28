package com.express.model.beans;

import java.io.Serializable;

public class Express implements Serializable {

	/**
	 * 快递实体
	 */
	private static final long serialVersionUID = 1L;
	private int express_id;// int PRIMARY KEY AUTO_INCREMENT,
	private int user_id;// int,
	private String express_no;// varchar(50),##快递单号
	private String express_companycode;// varchar(10),##快递简称
	private String express_time;
	private String express_remark;// VARCHAR(100),##备注
	public Express() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Express(int express_id, int user_id, String express_no, String express_companycode, String express_time,String express_remark) {
		super();
		this.express_id = express_id;
		this.user_id = user_id;
		this.express_no = express_no;
		this.express_companycode = express_companycode;
		this.express_time = express_time;
		this.express_remark = express_remark;
	}
	public Express( int user_id, String express_no, String express_companycode, String express_time,String express_remark) {
		super();
		this.user_id = user_id;
		this.express_no = express_no;
		this.express_companycode = express_companycode;
		this.express_time = express_time;
		this.express_remark = express_remark;
	}
	public int getExpress_id() {
		return express_id;
	}
	public void setExpress_id(int express_id) {
		this.express_id = express_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getExpress_no() {
		return express_no;
	}
	public void setExpress_no(String express_no) {
		this.express_no = express_no;
	}
	public String getExpress_companycode() {
		return express_companycode;
	}
	public void setExpress_companycode(String express_companycode) {
		this.express_companycode = express_companycode;
	}
	public String getExpress_remark() {
		return express_remark;
	}
	public void setExpress_remark(String express_remark) {
		this.express_remark = express_remark;
	}
	public String getExpress_time() {
		return express_time;
	}
	public void setExpress_time(String express_time) {
		this.express_time = express_time;
	}
	@Override
	public String toString() {
		return "Express [express_id=" + express_id + ", user_id=" + user_id + ", express_no=" + express_no
				+ ", express_companycode=" + express_companycode + ", express_time=" + express_time
				+ ", express_remark=" + express_remark + "]";
	}
	
}
