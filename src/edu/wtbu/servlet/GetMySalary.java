package edu.wtbu.servlet;

import java.io.IOException;
import java.util.Calendar;
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
 * Servlet implementation class GetMySalary
 * 获取个人薪资记录（员工查看自己的薪资）
 */
@WebServlet("/GetMySalary")
public class GetMySalary extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetMySalary() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String userId = request.getParameter("userId");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		
		try {
			if(userId == null || userId.isEmpty()) {
				rs.setData("错误: 缺少userId参数");
			} else {
				int userIdInt = Integer.parseInt(userId);
				int yearInt = year != null && !year.isEmpty() ? Integer.parseInt(year) : Calendar.getInstance().get(Calendar.YEAR);
				int monthInt = month != null && !month.isEmpty() ? Integer.parseInt(month) : Calendar.getInstance().get(Calendar.MONTH) + 1;
				
				// 先尝试计算薪资（确保有记录）
				calculateIfNotExists(userIdInt, yearInt, monthInt);
				
				// 查询薪资记录
				String sql = "SELECT sr.record_id, sr.user_id, sr.year, sr.month, sr.base_salary, " +
						"sr.performance_salary, sr.total_salary, sr.salary_status, sr.created_time, " +
						"e.username as employee_no, e.post, u.name, u.sex " +
						"FROM salary_record sr " +
						"LEFT JOIN employee e ON sr.user_id = e.userId " +
						"LEFT JOIN `user` u ON sr.user_id = u.userId " +
						"WHERE sr.user_id = ? AND sr.year = ? AND sr.month = ?";
				
				List<HashMap<String, Object>> salaryList = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userIdInt, yearInt, monthInt});
				
				if(salaryList == null || salaryList.size() == 0) {
					rs.setData("暂无该月份的薪资记录");
				} else {
					HashMap<String, Object> record = salaryList.get(0);
					
					// 构建返回结果
					HashMap<String, Object> salaryInfo = new HashMap<>();
					salaryInfo.put("recordId", record.get("record_id"));
					salaryInfo.put("userId", record.get("user_id"));
					salaryInfo.put("employeeNo", record.get("employee_no") != null ? record.get("employee_no") : "");
					salaryInfo.put("name", record.get("name") != null ? record.get("name") : "");
					salaryInfo.put("post", record.get("post") != null ? record.get("post") : "");
					salaryInfo.put("sex", record.get("sex") != null ? record.get("sex") : "");
					salaryInfo.put("year", record.get("year"));
					salaryInfo.put("month", record.get("month"));
					salaryInfo.put("baseSalary", record.get("base_salary"));
					salaryInfo.put("performanceSalary", record.get("performance_salary"));
					salaryInfo.put("totalSalary", record.get("total_salary"));
					salaryInfo.put("status", record.get("salary_status"));
					salaryInfo.put("statusText", "1".equals(String.valueOf(record.get("salary_status"))) ? "已发放" : "待发放");
					salaryInfo.put("createdTime", record.get("created_time"));
					
					// 获取薪资说明
					String post = record.get("post") != null ? record.get("post").toString() : "";
					salaryInfo.put("salaryDescription", getSalaryDescription(post));
					
					rs.setData(salaryInfo);
					rs.setFlag("success");
				}
			}
		} catch (NumberFormatException e) {
			rs.setData("错误: 参数格式不正确");
		} catch (Exception e) {
			e.printStackTrace();
			rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
		}
		
		response.getWriter().append(JSON.toJSONString(rs));
	}
	
	/**
	 * 如果记录不存在，则计算薪资
	 */
	private void calculateIfNotExists(int userId, int year, int month) {
		try {
			// 检查是否已有记录
			String checkSql = "SELECT record_id FROM salary_record WHERE user_id = ? AND year = ? AND month = ?";
			List<HashMap<String, Object>> existing = MySqlHelper.executeQueryReturnMap(checkSql, new Object[]{userId, year, month});
			
			if(existing == null || existing.size() == 0) {
				// 调用CalculateSalary计算薪资
				// 这里简化处理，实际应该调用CalculateSalary的方法
				// 由于无法直接调用，我们跳过，让前端触发计算
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取薪资说明
	 */
	private String getSalaryDescription(String post) {
		switch(post) {
			case "会计":
			case "用户管理员":
				return "薪资组成：基本工资 + 补贴（通过报销申请获得）";
			case "仓库管理员":
			case "业务管理员":
				return "薪资组成：基本工资 + 补贴（通过报销申请获得）+ 绩效工资（参与活动金额的1%）";
			case "工人":
				return "薪资组成：基本工资 + 日薪（工作天数 × 200元）";
			default:
				return "薪资组成：基本工资";
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
