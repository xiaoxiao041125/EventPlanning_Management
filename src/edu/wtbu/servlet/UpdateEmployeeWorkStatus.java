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
 * Servlet implementation class UpdateEmployeeWorkStatus
 */
@WebServlet("/UpdateEmployeeWorkStatus")
public class UpdateEmployeeWorkStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateEmployeeWorkStatus() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		String userId = request.getParameter("userId");
		String workStartTime = request.getParameter("workStartTime");
		String workEndTime = request.getParameter("workEndTime");
		String demandId = request.getParameter("demandId");
		
		Result rs = EmployeeDao.updateEmployeeWorkStatus(userId, workStartTime, workEndTime, demandId);
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