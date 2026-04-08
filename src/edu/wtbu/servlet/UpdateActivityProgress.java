package edu.wtbu.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.dao.ActivityDao;
import edu.wtbu.pojo.Result;

/**
 * Servlet implementation class UpdateActivityProgress
 */
@WebServlet("/UpdateActivityProgress")
public class UpdateActivityProgress extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateActivityProgress() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		String demandId = request.getParameter("demandId");
		String activityProgress = request.getParameter("activityProgress");
		String refuseReason = request.getParameter("refuseReason");
		String principalUserId = request.getParameter("principalUserId");
		
		Result rs = ActivityDao.updateActivityProgress(demandId, activityProgress, refuseReason, principalUserId);
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