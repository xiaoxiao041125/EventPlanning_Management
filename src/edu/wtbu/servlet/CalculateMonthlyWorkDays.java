package edu.wtbu.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.dao.EmployeeDao;
import edu.wtbu.pojo.Result;

/**
 * Servlet implementation class CalculateMonthlyWorkDays
 */
@WebServlet("/CalculateMonthlyWorkDays")
public class CalculateMonthlyWorkDays extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CalculateMonthlyWorkDays() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		String userId = request.getParameter("userId");
		String type = request.getParameter("type"); // type=completed 或 type=pending 或 type=statistics
		
		Result rs;
		if ("completed".equals(type)) {
			rs = EmployeeDao.countCompletedWorkItems(userId);
		} else if ("pending".equals(type)) {
			rs = EmployeeDao.countPendingWorkItems(userId);
		} else if ("statistics".equals(type)) {
			rs = EmployeeDao.getWorkStatistics(userId);
		} else {
			rs = EmployeeDao.calculateMonthlyWorkDays(userId);
		}
		response.getWriter().append(JSON.toJSONString(rs));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}