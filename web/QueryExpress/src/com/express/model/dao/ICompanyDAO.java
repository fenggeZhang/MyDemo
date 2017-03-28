package com.express.model.dao;

import java.util.List;

import com.express.model.beans.Company;

public interface ICompanyDAO {
	//查找某个快递
	public abstract String getCode(String company_name);
	//根据快递公司名字 返回 整个公司信息
	public abstract Company get(String Company);
	
	public abstract String getImg(String company_name);
	//查找所有快递公司
	public abstract List<Company> getAll();
}
