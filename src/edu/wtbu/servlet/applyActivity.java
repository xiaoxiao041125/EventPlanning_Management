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
 * Servlet implementation class applyActivity
 */
@WebServlet("/applyActivity")
public class applyActivity extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public applyActivity() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}
	
	/**
	 * 统一处理请求
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		String userId = request.getParameter("userId");
		String activity_place = request.getParameter("activity_place");
		String activity_type = request.getParameter("activity_type");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String activity_people = request.getParameter("activity_people");
		String activity_budget = request.getParameter("activity_budget");
		String activity_progress = request.getParameter("activity_progress");
		String requirement_desc = request.getParameter("requirement_desc");
		String phone = request.getParameter("phone");
		String name = request.getParameter("name");
		
		// 打印接收到的参数，便于调试
		System.out.println("applyActivity接收参数:");
		System.out.println("  userId=" + userId);
		System.out.println("  activity_place=" + activity_place);
		System.out.println("  activity_type=" + activity_type);
		System.out.println("  start_time=" + start_time);
		System.out.println("  end_time=" + end_time);
		System.out.println("  phone=" + phone);
		System.out.println("  name=" + name);
		
		Result rs = ActivityDao.applyActivity(userId, activity_place, activity_type, start_time, end_time, 
				activity_people, activity_budget, activity_progress, requirement_desc, phone, name);
		response.getWriter().append(JSON.toJSONString(rs));
	}

}
