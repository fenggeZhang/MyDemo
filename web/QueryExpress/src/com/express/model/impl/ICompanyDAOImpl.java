package com.express.model.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.express.model.beans.Company;
import com.express.model.beans.ExpressGson;
import com.express.model.dao.ICompanyDAO;
import com.express.utils.C3P0Utils;

public class ICompanyDAOImpl implements ICompanyDAO {
	private Connection connection = null;
	private PreparedStatement statement = null;
	private ResultSet resultSet = null;

	String sql = null;
	@Override
	public String getCode(String company_name) {
		connection = C3P0Utils.getConnection();
		String code="";
		sql = "select company_code from company where company_name =?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, company_name);/*
			statement.setInt(2, (cur - 1) * paging.getPageSize());
			statement.setInt(3,  paging.getPageSize());*/
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				code=resultSet.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return code;
	}

	@Override
	public Company get(String Company) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImg(String company_name) {
		connection = C3P0Utils.getConnection();
		String img_url="";
		sql = "select company_img from company where company_name =?";
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, company_name);/*
			statement.setInt(2, (cur - 1) * paging.getPageSize());
			statement.setInt(3,  paging.getPageSize());*/
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				img_url=resultSet.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return img_url;
	}

	@Override
	public List<Company> getAll() {
		List<Company> companyList = new ArrayList<Company>();
		connection = C3P0Utils.getConnection();
		String sql = "select * from company";
		try {
			statement = connection.prepareStatement(sql);/*
			statement.setInt(2, (cur - 1) * paging.getPageSize());
			statement.setInt(3,  paging.getPageSize());*/
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int company_id=resultSet.getInt(1);// int PRIMARY KEY AUTO_INCREMENT,
				String company_code=resultSet.getString(2);// VARCHAR(10),##快递公司简称
				String company_name=resultSet.getString(3);// varchar(20),##快递公司名称
				String company_img=resultSet.getString(4);//  varchar(200),##快递图片
				String company_phone=resultSet.getString(5);// VARCHAR(20),##电话
				String company_remark=resultSet.getString(6);// varchar(100)##备注
				Company company=new Company(company_id, company_code, company_name, company_img, company_phone, company_remark);
				//System.out.println(expressgson.toString());
				companyList.add(company);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return companyList;
	}

}
