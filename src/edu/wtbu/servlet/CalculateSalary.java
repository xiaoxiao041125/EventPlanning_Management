package edu.wtbu.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
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
 * Servlet implementation class CalculateSalary
 * 薪资计算Servlet - 计算并保存员工薪资记录
 */
@WebServlet("/CalculateSalary")
public class CalculateSalary extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CalculateSalary() {
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
				int yearInt = year != null ? Integer.parseInt(year) : Calendar.getInstance().get(Calendar.YEAR);
				int monthInt = month != null ? Integer.parseInt(month) : Calendar.getInstance().get(Calendar.MONTH) + 1;
				
				// 获取员工信息
				String employeeSql = "SELECT e.*, u.name, u.sex FROM employee e LEFT JOIN `user` u ON e.userId = u.userId WHERE e.userId = ?";
				List<HashMap<String, Object>> employeeList = MySqlHelper.executeQueryReturnMap(employeeSql, new Object[]{userIdInt});
				
				if(employeeList == null || employeeList.size() == 0) {
					rs.setData("错误: 未找到该员工信息");
				} else {
					HashMap<String, Object> employee = employeeList.get(0);
					String post = employee.get("post") != null ? employee.get("post").toString() : "";
					String employeeNo = employee.get("username") != null ? employee.get("username").toString() : "";
					
					// 基本工资
					BigDecimal baseSalary = getBaseSalary(post);
					
					// 根据职位计算薪资
					BigDecimal totalSalary = BigDecimal.ZERO;
					BigDecimal performanceSalary = BigDecimal.ZERO;
					
					if("会计".equals(post) || "用户管理员".equals(post)) {
						// 会计/用户管理员: 基本工资 + 补贴(报销)
						BigDecimal subsidy = calculateSubsidy(userIdInt, yearInt, monthInt);
						performanceSalary = subsidy; // 补贴计入performance_salary字段
						totalSalary = baseSalary.add(subsidy);
						
					} else if("仓库管理员".equals(post) || "业务管理员".equals(post)) {
						// 仓库管理员/业务管理员: 基本工资 + 补贴 + 绩效工资(活动金额的1%)
						BigDecimal subsidy = calculateSubsidy(userIdInt, yearInt, monthInt);
						BigDecimal activityBonus = calculateActivityBonus(userIdInt, yearInt, monthInt);
						performanceSalary = subsidy.add(activityBonus); // 补贴+绩效计入performance_salary字段
						totalSalary = baseSalary.add(subsidy).add(activityBonus);
						
					} else if("工人".equals(post)) {
						// 工人: 根据工作天数计算薪资
						int workDays = calculateWorkDays(userIdInt, yearInt, monthInt);
						
						// 如果一天都没工作，不生成薪资记录
						if(workDays == 0) {
							rs.setData("本月无工作记录，不生成薪资");
							rs.setFlag("no_work");
							response.getWriter().append(JSON.toJSONString(rs));
							return;
						}
						
						// 如果工作天数少于5天，没有底薪，只有日薪
						if(workDays < 5) {
							baseSalary = BigDecimal.ZERO; // 无底薪
							BigDecimal dailyWage = new BigDecimal(workDays * 200);
							totalSalary = dailyWage;
							performanceSalary = dailyWage; // 日薪计入performance_salary字段
						} else {
							// 工作5天及以上，正常计算
							BigDecimal dailyWage = new BigDecimal(workDays * 200);
							totalSalary = baseSalary.add(dailyWage);
							performanceSalary = dailyWage; // 日薪计入performance_salary字段
						}
						
					} else {
						// 其他职位: 只发基本工资
						totalSalary = baseSalary;
					}
					
					// 保存或更新薪资记录
					saveOrUpdateSalaryRecord(userIdInt, yearInt, monthInt, baseSalary, performanceSalary, totalSalary);
					
					// 构建返回结果
					HashMap<String, Object> salaryInfo = new HashMap<>();
					salaryInfo.put("userId", userIdInt);
					salaryInfo.put("employeeNo", employeeNo);
					salaryInfo.put("name", employee.get("name"));
					salaryInfo.put("post", post);
					salaryInfo.put("year", yearInt);
					salaryInfo.put("month", monthInt);
					salaryInfo.put("baseSalary", baseSalary.setScale(2, RoundingMode.HALF_UP));
					salaryInfo.put("performanceSalary", performanceSalary.setScale(2, RoundingMode.HALF_UP));
					salaryInfo.put("totalSalary", totalSalary.setScale(2, RoundingMode.HALF_UP));
					salaryInfo.put("status", 0); // 0待发放
					salaryInfo.put("statusText", "待发放");
					
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
	 * 获取基本工资
	 */
	private BigDecimal getBaseSalary(String post) {
		switch(post) {
			case "会计":
				return new BigDecimal(9000);
			case "用户管理员":
				return new BigDecimal(7000);
			case "仓库管理员":
				return new BigDecimal(6000);
			case "业务管理员":
				return new BigDecimal(8000);
			case "工人":
				return new BigDecimal(5000);
			default:
				return new BigDecimal(5000);
		}
	}
	
	/**
	 * 计算补贴(通过已批准的报销申请)
	 */
	private BigDecimal calculateSubsidy(int userId, int year, int month) {
		String sql = "SELECT SUM(amount) as total FROM reimbursement_application WHERE user_id = ? AND status = 1 AND YEAR(created_time) = ? AND MONTH(created_time) = ?";
		List<HashMap<String, Object>> result = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId, year, month});
		
		if(result != null && result.size() > 0 && result.get(0).get("total") != null) {
			return new BigDecimal(result.get(0).get("total").toString());
		}
		return BigDecimal.ZERO;
	}
	
	/**
	 * 计算活动绩效(活动金额的1%)
	 */
	private BigDecimal calculateActivityBonus(int userId, int year, int month) {
		// 查询员工参与的活动预算总和的1%
		String sql = "SELECT SUM(d.budget) as total FROM demand d " +
				"INNER JOIN employee_work ew ON d.demandId = ew.demand_id " +
				"WHERE ew.user_id = ? AND d.status = 1 AND YEAR(ew.work_start_time) = ? AND MONTH(ew.work_start_time) = ?";
		List<HashMap<String, Object>> result = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId, year, month});
		
		if(result != null && result.size() > 0 && result.get(0).get("total") != null) {
			BigDecimal totalBudget = new BigDecimal(result.get(0).get("total").toString());
			return totalBudget.multiply(new BigDecimal("0.01")); // 活动金额的1%
		}
		return BigDecimal.ZERO;
	}
	
	/**
	 * 计算工作天数(根据employee_work表)
	 */
	private int calculateWorkDays(int userId, int year, int month) {
		// 计算该员工本月的工作天数（不同的工作记录算不同的天数）
		String sql = "SELECT COUNT(DISTINCT DATE(work_start_time)) as workDays FROM employee_work " +
				"WHERE user_id = ? AND YEAR(work_start_time) = ? AND MONTH(work_start_time) = ?";
		List<HashMap<String, Object>> result = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId, year, month});
		
		if(result != null && result.size() > 0 && result.get(0).get("workDays") != null) {
			return Integer.parseInt(result.get(0).get("workDays").toString());
		}
		return 0;
	}
	
	/**
	 * 保存或更新薪资记录
	 */
	private void saveOrUpdateSalaryRecord(int userId, int year, int month, 
			BigDecimal baseSalary, BigDecimal performanceSalary, BigDecimal totalSalary) {
		// 先检查是否已有记录
		String checkSql = "SELECT record_id FROM salary_record WHERE user_id = ? AND year = ? AND month = ?";
		List<HashMap<String, Object>> existing = MySqlHelper.executeQueryReturnMap(checkSql, new Object[]{userId, year, month});
		
		if(existing != null && existing.size() > 0) {
			// 更新记录
			String updateSql = "UPDATE salary_record SET base_salary = ?, performance_salary = ?, total_salary = ?, created_time = ? " +
					"WHERE user_id = ? AND year = ? AND month = ?";
			MySqlHelper.executeUpdate(updateSql, new Object[]{
				baseSalary, performanceSalary, totalSalary, new Timestamp(System.currentTimeMillis()),
				userId, year, month
			});
		} else {
			// 插入新记录
			String insertSql = "INSERT INTO salary_record (user_id, year, month, base_salary, performance_salary, total_salary, salary_status, created_time) " +
					"VALUES (?, ?, ?, ?, ?, ?, 0, ?)";
			MySqlHelper.executeUpdate(insertSql, new Object[]{
				userId, year, month, baseSalary, performanceSalary, totalSalary, new Timestamp(System.currentTimeMillis())
			});
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}