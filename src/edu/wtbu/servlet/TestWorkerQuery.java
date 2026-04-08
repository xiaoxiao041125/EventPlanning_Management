package edu.wtbu.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.helper.MySqlHelper;
import edu.wtbu.pojo.Result;

/**
 * Servlet implementation class TestWorkerQuery
 */
@WebServlet("/TestWorkerQuery")
public class TestWorkerQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestWorkerQuery() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String userId = request.getParameter("userId");
		if(userId != null) {
			try {
				// 直接在Servlet中查询，使用正确的字段名userId
				String sql = "SELECT * FROM employee_work WHERE userId = ?";
				java.util.List<java.util.HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId});
				if(list != null && list.size() > 0) {
					// 找到了工作记录，连接活动需求表
					String sqlWithJoin = "SELECT ew.*, ad.activity_place, ad.activity_type, ad.start_time, ad.end_time, ad.activity_people, ad.activity_budget FROM employee_work ew LEFT JOIN activity_demand ad ON ew.demand_id = ad.demand_id WHERE ew.userId = ?";
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