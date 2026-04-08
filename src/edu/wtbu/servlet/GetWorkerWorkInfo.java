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
 * Servlet implementation class GetWorkerWorkInfo
 */
@WebServlet("/GetWorkerWorkInfo")
public class GetWorkerWorkInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetWorkerWorkInfo() {
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
				// 直接在Servlet中查询，使用userld字段
				String sql = "SELECT * FROM employee_work WHERE userld = ?";
				java.util.List<java.util.HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId});
				if(list != null && list.size() > 0) {
					// 找到了工作记录，连接活动需求表
					String sqlWithJoin = "SELECT ew.*, ad.activity_place, ad.activity_type, ad.start_time, ad.end_time, ad.activity_people, ad.activity_budget, e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city FROM employee_work ew LEFT JOIN activity_demand ad ON ew.demand_id = ad.demand_id LEFT JOIN employee e ON ew.userld = e.userid WHERE ew.userld = ?";
					java.util.List<java.util.HashMap<String, Object>> joinedList = MySqlHelper.executeQueryReturnMap(sqlWithJoin, new Object[]{userId});
					rs.setData(joinedList);
					rs.setFlag("success");
				} else {
					// 测试整个表
					String sqlAll = "SELECT * FROM employee_work";
					java.util.List<java.util.HashMap<String, Object>> allList = MySqlHelper.executeQueryReturnMap(sqlAll, null);
					if(allList != null && allList.size() > 0) {
						rs.setData("表中有记录，但没有匹配的userId: " + userId);
					} else {
						rs.setData("表中没有记录");
					}
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