package edu.wtbu.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.pojo.Result;
import edu.wtbu.dao.ActivityDao;

/**
 * Servlet implementation class CancelActivity
 * 用户取消活动接口 - 将活动状态改为-3
 */
@WebServlet("/CancelActivity")
public class CancelActivity extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 设置响应内容类型和编码
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		// 获取参数
		String demandId = request.getParameter("demandId");
		
		System.out.println("CancelActivity请求 - demandId: " + demandId);
		
		// 参数验证
		if (demandId == null || demandId.trim().isEmpty()) {
			Result rs = new Result();
			rs.setData("活动ID不能为空");
			rs.setFlag("fail");
			out.print(JSON.toJSONString(rs));
			out.close();
			return;
		}
		
		// 调用DAO层取消活动
		Result rs = ActivityDao.cancelActivity(demandId);
		
		System.out.println("CancelActivity结果: " + rs.getFlag() + ", " + rs.getData());
		
		// 返回JSON结果
		out.print(JSON.toJSONString(rs));
		out.close();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 设置请求编码，确保中文参数正确处理
		request.setCharacterEncoding("UTF-8");
		doGet(request, response);
	}
}
