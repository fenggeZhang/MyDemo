package com.express.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.express.model.beans.Company;
import com.express.model.beans.Express;
import com.express.model.beans.ExpressGson;
import com.express.model.beans.QueryResultBean;
import com.express.model.dao.ICompanyDAO;
import com.express.model.dao.IExpressDAO;
import com.express.model.impl.ICompanyDAOImpl;
import com.express.model.impl.IExpressDAOImpl;
import com.express.test.KdniaoTrackQueryAPI;
import com.google.gson.Gson;

/**
 * 快递servlet
 */
@WebServlet("/expressservlet")
public class ExpressServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// 用于输出数据
	private PrintWriter mPrintWriter;

	private Express express;
	private int express_id;// int PRIMARY KEY AUTO_INCREMENT,
	private int user_id;// int,
	private String express_no;// varchar(50),##快递单号
	private String express_companycode;// varchar(10),##快递简称
	private String express_time;// 添加时间
	private String express_remark;// VARCHAR(100),##备注

	private List<ExpressGson> expressList;

	private Gson gson;
	private String result;

	private IExpressDAO expressDAO;
	private ICompanyDAO companyDAO;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExpressServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 初始化
		mPrintWriter = response.getWriter();
		String method = request.getParameter("methods");
		switch (method) {
		case "add":
			expressDAO = new IExpressDAOImpl();
			// user_id=Integer.parseInt(request.getParameter("user_id"));
			express_no = request.getParameter("express_no");
			express_companycode = request.getParameter("express_companycode");
			/*
			 * express_companycode=new String(request.getParameter(
			 * "express_companycode").getBytes("iso8859-1"), "UTF-8");
			 */
			express_remark =request.getParameter("express_remark");
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("MM-dd");
			express_time = format.format(date);
			express = new Express(1, express_no, express_companycode, express_time, express_remark);
			expressDAO.addExpress(express);
			System.out.println("添加快递");
			break;
		case "getOne":
			// 根据快递公司的编码 订单号 返回该订单的 所有轨迹信息
			express_no = request.getParameter("express_no");// 订单号
			express_companycode = request.getParameter("express_companycode");// 快递公司
			//System.out.println("zfg");
			ICompanyDAO companyDAO=new ICompanyDAOImpl();
			String company_code=companyDAO.getCode(express_companycode);//根据快递公司名字 返回code
			KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
			try {
				String result = api.getOrderTracesByJson1(company_code,express_no);
				// QueryResultBean result = api.getOrderTracesByJson("BS",
				// "70906597189385");
				// System.out.print(result.toString());
				/*
				 * gson = new Gson(); result = gson.toJson(result);
				 */
				System.out.println(result);
				mPrintWriter.write(result.toString());
				mPrintWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "getall":
			expressDAO = new IExpressDAOImpl();
			user_id = Integer.parseInt(request.getParameter("user_id"));
			int cur = Integer.parseInt(request.getParameter("cur"));
			expressList = expressDAO.getAll(cur, user_id);
			gson = new Gson();
			result = gson.toJson(expressList);
			mPrintWriter.write(result);
			mPrintWriter.close();
			break;
		case "getCompanyImg":
			companyDAO=new ICompanyDAOImpl();
			express_companycode = request.getParameter("express_companycode");// 快递公司
			String company_name=companyDAO.getImg(express_companycode);//根据快递公司名字 返回code
			mPrintWriter.write(company_name);
			mPrintWriter.close();
			break;
		case "getCompany":
			companyDAO=new ICompanyDAOImpl();
			List<Company> companyList=companyDAO.getAll();
			gson = new Gson();
			result = gson.toJson(companyList);
			mPrintWriter.write(result);
			mPrintWriter.close();
			break;
		default:
			break;
		}
	}

}
