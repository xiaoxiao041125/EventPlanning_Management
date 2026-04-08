package edu.wtbu.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.dao.EmployeeDao;
import edu.wtbu.helper.MySqlHelper;
import edu.wtbu.pojo.Result;

/**
 * Servlet implementation class CheckWorkerWorkByUserId
 */
@WebServlet({"/CheckWorkerWorkByUserId", "/GetWorkStatistics"})
public class CheckWorkerWorkByUserId extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @return 
     * @see HttpServlet#HttpServlet()
     */
    public void CheckUserWorkByUserId() {
        
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String userId = request.getParameter("userId");
		String requestURI = request.getRequestURI();
		
		// 检查是否是工作统计请求
		if (requestURI.endsWith("/GetWorkStatistics")) {
			if(userId != null) {
				try {
					// 获取工作统计信息
					rs = EmployeeDao.getWorkStatistics(userId);
				} catch (Exception e) {
					rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
				}
			} else {
				rs.setData("请提供userId参数");
			}
		} else {
			// 原有逻辑：查询工作记录
			if(userId != null) {
				try {
					// 直接在Servlet中查询，使用正确的字段名userId
					String sql = "SELECT * FROM employee_work WHERE userId = ?";
					java.util.List<java.util.HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId});
					if(list != null && list.size() > 0) {
						// 找到了工作记录，连接活动需求表和员工表获取管理员名字
						String sqlWithJoin = "SELECT ew.*, ad.activity_place, ad.activity_type, ad.start_time, ad.end_time, ad.activity_people, ad.activity_budget, e.name as principal_name FROM employee_work ew LEFT JOIN activity_demand ad ON ew.demand_id = ad.demand_id LEFT JOIN employee e ON ad.principal_userid = e.userid WHERE ew.userId = ?";
						java.util.List<java.util.HashMap<String, Object>> joinedList = MySqlHelper.executeQueryReturnMap(sqlWithJoin, new Object[]{userId});
						rs.setData(joinedList);
						rs.setFlag("success");
					} else {
						rs.setData("没有找到该用户的工作记录");
					}
				} catch (Exception e) {
					rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
				}
			} else {
				rs.setData("请提供userId参数");
			}
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