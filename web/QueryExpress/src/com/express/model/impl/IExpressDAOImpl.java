package com.express.model.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.express.model.beans.Express;
import com.express.model.beans.ExpressGson;
import com.express.model.dao.IExpressDAO;
import com.express.utils.C3P0Utils;
import com.express.utils.Paging;

public class IExpressDAOImpl implements IExpressDAO {
	private Connection connection = null;
	private PreparedStatement statement = null;
	private ResultSet resultSet = null;

	String sql = null;
	Paging paging = new Paging();

	@Override
	public boolean addExpress(Express express) {
		connection = C3P0Utils.getConnection();
		boolean result=false;
		// 向表中加入快递
		sql = "INSERT INTO express(user_id,express_no,express_companycode,express_time,express_remark) VALUES (?,?,?,?,?);";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, express.getUser_id());
			statement.setString(2, express.getExpress_no());
			statement.setString(3, express.getExpress_companycode());
			statement.setString(4, express.getExpress_time());
			statement.setString(5, express.getExpress_remark());
			statement.execute();
			result = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return result;
	}

	@Override
	public boolean delExpress(int express_id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ExpressGson> getAll(int cur, int user_id) {
		List<ExpressGson> expressList = new ArrayList<ExpressGson>();
		connection = C3P0Utils.getConnection();
		String sql = "select express_id,user_id,express_no,express_companycode,express_time,express_remark,company.company_img from express,company where user_id =? and express.express_companycode=company.company_name";
		try {
			statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);/*
			statement.setInt(2, (cur - 1) * paging.getPageSize());
			statement.setInt(3,  paging.getPageSize());*/
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int express_id=resultSet.getInt(1);// int PRIMARY KEY AUTO_INCREMENT,
				int user_id1=resultSet.getInt(2);// int,
				String express_no=resultSet.getString(3);// varchar(50),##快递单号
				String express_companycode=resultSet.getString(4);// varchar(10),##快递
				String express_time=resultSet.getString(5);
				String express_remark=resultSet.getString(6);// VARCHAR(100),##备注
				String company_img=resultSet.getString(7);// VARCHAR(100),##备注
				ExpressGson expressgson=new ExpressGson(express_id, express_no, express_companycode, express_time, company_img, express_remark);
				//System.out.println(expressgson.toString());
				expressList.add(expressgson);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			C3P0Utils.close(resultSet, statement, connection);
		}
		return expressList;
	}

	@Override
	public Express getOne(int express_id) {
		// TODO Auto-generated method stub
		return null;
	}

}
