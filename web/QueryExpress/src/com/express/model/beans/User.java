package com.express.model.beans;

import java.io.Serializable;

public class User implements Serializable{

	/**
	 * 用户实体
	 */
	private static final long serialVersionUID = 1L;
	private int user_id;//        int PRIMARY KEY AUTO_INCREMENT,
	private String user_phone;//     VARCHAR(20),##手机号
	private String user_pwd;//       VARCHAR(100),##密码
	private String user_name;//      VARCHAR(20),##用户名
	private String user_photo;//     VARCHAR(200),##头像
	private String user_remark;//    VARCHAR(100)##备注
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(int user_id, String user_phone, String user_pwd, String user_name, String user_photo,
			String user_remark) {
		super();
		this.user_id = user_id;
		this.user_phone = user_phone;
		this.user_pwd = user_pwd;
		this.user_name = user_name;
		this.user_photo = user_photo;
		this.user_remark = user_remark;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUser_phone() {
		return user_phone;
	}
	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}
	public String getUser_pwd() {
		return user_pwd;
	}
	public void setUser_pwd(String user_pwd) {
		this.user_pwd = user_pwd;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_photo() {
		return user_photo;
	}
	public void setUser_photo(String user_photo) {
		this.user_photo = user_photo;
	}
	public String getUser_remark() {
		return user_remark;
	}
	public void setUser_remark(String user_remark) {
		this.user_remark = user_remark;
	}
	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", user_phone=" + user_phone + ", user_pwd=" + user_pwd + ", user_name="
				+ user_name + ", user_photo=" + user_photo + ", user_remark=" + user_remark + "]";
	}
	
}
