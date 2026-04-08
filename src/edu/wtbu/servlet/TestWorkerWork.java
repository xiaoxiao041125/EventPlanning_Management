package edu.wtbu.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.helper.MySqlHelper;
import edu.wtbu.pojo.Result;

/**
 * Servlet implementation class TestWorkerWork
 */
@WebServlet("/TestWorkerWork")
public class TestWorkerWork extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestWorkerWork() {
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
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rsSet = null;
			
			try {
				conn = MySqlHelper.getConnection();
				if(conn != null) {
					// 直接执行SQL查询
					String sql = "SELECT ew.userld, ew.work_start_time, ew.work_end_time, ew.demand_id, ad.activity_place, ad.activity_type, ad.start_time, ad.end_time, ad.activity_people, ad.activity_budget FROM employee_work ew LEFT JOIN activity_demand ad ON ew.demand_id = ad.demand_id WHERE ew.userld = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, userId);
					rsSet = pstmt.executeQuery();
					
					List<HashMap<String, Object>> list = new ArrayList<>();
					while(rsSet.next()) {
						HashMap<String, Object> map = new HashMap<>();
						map.put("userld", rsSet.getInt("userld"));
						map.put("work_start_time", rsSet.getTimestamp("work_start_time"));
						map.put("work_end_time", rsSet.getTimestamp("work_end_time"));
						map.put("demand_id", rsSet.getInt("demand_id"));
						map.put("activity_place", rsSet.getString("activity_place"));
						map.put("activity_type", rsSet.getString("activity_type"));
						map.put("start_time", rsSet.getDate("start_time"));
						map.put("end_time", rsSet.getDate("end_time"));
						map.put("activity_people", rsSet.getString("activity_people"));
						map.put("activity_budget", rsSet.getString("activity_budget"));
						list.add(map);
					}
					
					if(list.size() > 0) {
						rs.setData(list);
						rs.setFlag("success");
					} else {
						// 测试整个表
						sql = "SELECT * FROM employee_work";
						pstmt = conn.prepareStatement(sql);
						rsSet = pstmt.executeQuery();
						list = new ArrayList<>();
						while(rsSet.next()) {
							HashMap<String, Object> map = new HashMap<>();
							map.put("userld", rsSet.getInt("userld"));
							map.put("work_start_time", rsSet.getTimestamp("work_start_time"));
							map.put("work_end_time", rsSet.getTimestamp("work_end_time"));
							map.put("demand_id", rsSet.getInt("demand_id"));
							list.add(map);
						}
						if(list.size() > 0) {
							rs.setData("表中有记录，但没有匹配的userId: " + userId);
						} else {
							rs.setData("表中没有记录");
						}
					}
				} else {
					rs.setData("数据库连接失败");
				}
			} catch (Exception e) {
				rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
			} finally {
				// 关闭资源
				try {
					if(rsSet != null) rsSet.close();
					if(pstmt != null) pstmt.close();
					if(conn != null) conn.close();
				} catch (SQLException e) {
					// 忽略关闭异常
				}
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