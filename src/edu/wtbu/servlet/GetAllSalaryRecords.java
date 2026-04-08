package edu.wtbu.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
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
 * Servlet implementation class GetAllSalaryRecords
 * 获取所有员工薪资记录（会计使用）
 * 逻辑：先查询salary_record表，如果没有数据，则从员工表、工作表、报销表查询并计算，写入salary_record表，再返回数据
 */
@WebServlet("/GetAllSalaryRecords")
public class GetAllSalaryRecords extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetAllSalaryRecords() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		String employeeNo = request.getParameter("employeeNo");
		String employeeName = request.getParameter("employeeName");
		
		try {
			int yearInt = year != null && !year.isEmpty() ? Integer.parseInt(year) : Calendar.getInstance().get(Calendar.YEAR);
			int monthInt = month != null && !month.isEmpty() ? Integer.parseInt(month) : Calendar.getInstance().get(Calendar.MONTH) + 1;
			
			// 第一步：查询salary_record表
			List<HashMap<String, Object>> salaryRecords = querySalaryRecords(yearInt, monthInt, employeeNo, employeeName);
			
			// 如果没有数据，则从员工表查询并计算
			if(salaryRecords == null || salaryRecords.size() == 0) {
				System.out.println("salary_record表无数据，开始从员工表查询...");
				
				// 从员工表查询所有员工
				List<HashMap<String, Object>> employees = queryEmployees(employeeNo, employeeName);
				System.out.println("查询到员工数量: " + (employees != null ? employees.size() : 0));
				
				// 为每个员工计算薪资并插入salary_record表
				if(employees != null && employees.size() > 0) {
					for(HashMap<String, Object> emp : employees) {
						int userId = Integer.parseInt(emp.get("userId").toString());
						String post = emp.get("post") != null ? emp.get("post").toString() : "";
						String name = emp.get("name") != null ? emp.get("name").toString() : "";
						
						System.out.println("处理员工: userId=" + userId + ", name=" + name + ", post=" + post);
						
						// 计算该员工的薪资
						HashMap<String, Object> salaryData = calculateAndSaveSalary(userId, post, yearInt, monthInt);
						
						// 如果返回null，说明不满足条件（如一天都没工作的工人），跳过
						if(salaryData == null) {
							System.out.println("员工 " + name + " 不满足薪资计算条件，跳过");
							continue;
						}
						System.out.println("员工 " + name + " 薪资计算完成");
					}
				}
				
				// 再次查询salary_record表（现在应该有数据了）
				salaryRecords = querySalaryRecords(yearInt, monthInt, employeeNo, employeeName);
				System.out.println("再次查询salary_record表，记录数: " + (salaryRecords != null ? salaryRecords.size() : 0));
			}
			
			// 处理返回数据
			List<HashMap<String, Object>> resultList = new ArrayList<>();
			if(salaryRecords != null) {
				for(HashMap<String, Object> record : salaryRecords) {
					HashMap<String, Object> item = new HashMap<>();
					item.put("recordId", record.get("record_id"));
					item.put("userId", record.get("user_id"));
					item.put("employeeNo", record.get("employee_id") != null ? record.get("employee_id") : "");
					item.put("name", record.get("name") != null ? record.get("name") : "");
					item.put("post", record.get("post") != null ? record.get("post") : "");
					item.put("sex", record.get("sex") != null ? record.get("sex") : "");
					item.put("year", record.get("year"));
					item.put("month", record.get("month"));
					item.put("baseSalary", record.get("base_salary"));
					item.put("performanceSalary", record.get("performance_salary"));
					item.put("totalSalary", record.get("total_salary"));
					item.put("status", record.get("salary_status"));
					item.put("statusText", "1".equals(String.valueOf(record.get("salary_status"))) ? "已发放" : "待发放");
					item.put("createdTime", record.get("created_time"));
					resultList.add(item);
				}
			}
			
			rs.setData(resultList);
			rs.setFlag("success");
			
		} catch (NumberFormatException e) {
			rs.setData("错误: 参数格式不正确");
		} catch (Exception e) {
			e.printStackTrace();
			rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
		}
		
		response.getWriter().append(JSON.toJSONString(rs));
	}
	
	/**
	 * 查询salary_record表
	 */
	private List<HashMap<String, Object>> querySalaryRecords(int year, int month, String employeeNo, String employeeName) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT sr.record_id, sr.user_id, sr.year, sr.month, sr.base_salary, ");
		sql.append("sr.performance_salary, sr.total_salary, sr.salary_status, sr.created_time, ");
		sql.append("e.employee_id, e.post, e.name, e.sex ");
		sql.append("FROM salary_record sr ");
		sql.append("LEFT JOIN employee e ON sr.user_id = e.userId ");
		sql.append("WHERE sr.year = ? AND sr.month = ? ");
		
		List<Object> params = new ArrayList<>();
		params.add(year);
		params.add(month);
		
		if(employeeNo != null && !employeeNo.isEmpty()) {
			sql.append("AND e.employee_id LIKE ? ");
			params.add("%" + employeeNo + "%");
		}
		
		if(employeeName != null && !employeeName.isEmpty()) {
			sql.append("AND e.name LIKE ? ");
			params.add("%" + employeeName + "%");
		}
		
		sql.append("ORDER BY sr.created_time DESC");
		
		return MySqlHelper.executeQueryReturnMap(sql.toString(), params.toArray());
	}
	
	/**
	 * 查询员工表
	 */
	private List<HashMap<String, Object>> queryEmployees(String employeeNo, String employeeName) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT e.userId, e.employee_id, e.post, e.name, e.sex ");
		sql.append("FROM employee e ");
		sql.append("WHERE 1=1 ");
		
		List<Object> params = new ArrayList<>();
		
		if(employeeNo != null && !employeeNo.isEmpty()) {
			sql.append("AND e.employee_id LIKE ? ");
			params.add("%" + employeeNo + "%");
		}
		
		if(employeeName != null && !employeeName.isEmpty()) {
			sql.append("AND e.name LIKE ? ");
			params.add("%" + employeeName + "%");
		}
		
		sql.append("ORDER BY e.employee_id");
		
		return MySqlHelper.executeQueryReturnMap(sql.toString(), params.toArray());
	}
	
	/**
	 * 计算员工薪资并保存到salary_record表
	 * @return 薪资数据，如果员工不满足条件（如一天都没工作的工人）则返回null
	 */
	private HashMap<String, Object> calculateAndSaveSalary(int userId, String post, int year, int month) {
		System.out.println("calculateAndSaveSalary: userId=" + userId + ", post=" + post + ", year=" + year + ", month=" + month);
		
		// 基本工资
		BigDecimal baseSalary = getBaseSalary(post);
		BigDecimal totalSalary = BigDecimal.ZERO;
		BigDecimal performanceSalary = BigDecimal.ZERO;
		
		System.out.println("职位=" + post + ", 基本工资=" + baseSalary);
		
		if("会计".equals(post) || "用户管理员".equals(post)) {
			// 会计/用户管理员: 基本工资 + 补贴(报销)
			BigDecimal subsidy = calculateSubsidy(userId, year, month);
			performanceSalary = subsidy;
			totalSalary = baseSalary.add(subsidy);
			System.out.println("会计/用户管理员: 补贴=" + subsidy + ", 总工资=" + totalSalary);
			
		} else if("仓库管理员".equals(post) || "业务管理员".equals(post)) {
			// 仓库管理员/业务管理员: 基本工资 + 补贴 + 绩效工资(活动金额的1%)
			BigDecimal subsidy = calculateSubsidy(userId, year, month);
			BigDecimal activityBonus = calculateActivityBonus(userId, year, month);
			performanceSalary = subsidy.add(activityBonus);
			totalSalary = baseSalary.add(subsidy).add(activityBonus);
			System.out.println("仓库/业务管理员: 补贴=" + subsidy + ", 绩效=" + activityBonus + ", 总工资=" + totalSalary);
			
		} else if("工人".equals(post)) {
			// 工人: 根据工作天数计算薪资
			int workDays = calculateWorkDays(userId, year, month);
			System.out.println("工人: 工作天数=" + workDays);
			
			// 如果一天都没工作，不生成薪资记录，返回null
			if(workDays == 0) {
				System.out.println("工人无工作记录，返回null");
				return null;
			}
			
			// 如果工作天数少于5天，没有底薪，只有日薪
			if(workDays < 5) {
				baseSalary = BigDecimal.ZERO;
				BigDecimal dailyWage = new BigDecimal(workDays * 200);
				totalSalary = dailyWage;
				performanceSalary = dailyWage;
			} else {
				// 工作5天及以上，正常计算
				BigDecimal dailyWage = new BigDecimal(workDays * 200);
				totalSalary = baseSalary.add(dailyWage);
				performanceSalary = dailyWage;
			}
			System.out.println("工人: 底薪=" + baseSalary + ", 日薪=" + performanceSalary + ", 总工资=" + totalSalary);
			
		} else {
			// 其他职位: 只发基本工资
			totalSalary = baseSalary;
			System.out.println("其他职位: 总工资=" + totalSalary);
		}
		
		// 保存到salary_record表
		System.out.println("准备插入薪资记录...");
		insertSalaryRecord(userId, year, month, baseSalary, performanceSalary, totalSalary);
		
		HashMap<String, Object> salaryInfo = new HashMap<>();
		salaryInfo.put("userId", userId);
		salaryInfo.put("year", year);
		salaryInfo.put("month", month);
		salaryInfo.put("baseSalary", baseSalary);
		salaryInfo.put("performanceSalary", performanceSalary);
		salaryInfo.put("totalSalary", totalSalary);
		
		return salaryInfo;
	}
	
	/**
	 * 插入薪资记录到salary_record表
	 */
	private void insertSalaryRecord(int userId, int year, int month, 
			BigDecimal baseSalary, BigDecimal performanceSalary, BigDecimal totalSalary) {
		try {
			// 先检查是否已存在记录
			String checkSql = "SELECT record_id FROM salary_record WHERE user_id = ? AND year = ? AND month = ?";
			List<HashMap<String, Object>> existing = MySqlHelper.executeQueryReturnMap(checkSql, new Object[]{userId, year, month});
			
			if(existing != null && existing.size() > 0) {
				// 更新已有记录
				String updateSql = "UPDATE salary_record SET base_salary = ?, performance_salary = ?, total_salary = ?, created_time = ? " +
						"WHERE user_id = ? AND year = ? AND month = ?";
				int result = MySqlHelper.executeUpdate(updateSql, new Object[]{
					baseSalary.setScale(2, RoundingMode.HALF_UP), 
					performanceSalary.setScale(2, RoundingMode.HALF_UP), 
					totalSalary.setScale(2, RoundingMode.HALF_UP), 
					new Timestamp(System.currentTimeMillis()),
					userId, year, month
				});
				System.out.println("更新薪资记录: userId=" + userId + ", year=" + year + ", month=" + month + ", result=" + result);
			} else {
				// 插入新记录
				String insertSql = "INSERT INTO salary_record (user_id, year, month, base_salary, performance_salary, total_salary, salary_status, created_time) " +
						"VALUES (?, ?, ?, ?, ?, ?, 0, ?)";
				
				// 转为 String 类型（MySqlHelper 支持 String）
				String bs = baseSalary != null ? baseSalary.setScale(2, RoundingMode.HALF_UP).toString() : "0.00";
				String ps = performanceSalary != null ? performanceSalary.setScale(2, RoundingMode.HALF_UP).toString() : "0.00";
				String ts = totalSalary != null ? totalSalary.setScale(2, RoundingMode.HALF_UP).toString() : "0.00";
				
				System.out.println("插入参数: userId=" + userId + ", year=" + year + ", month=" + month + 
					", baseSalary=" + bs + ", performanceSalary=" + ps + ", totalSalary=" + ts);
				
				int result = MySqlHelper.executeUpdate(insertSql, new Object[]{
					userId, year, month, 
					bs, 
					ps, 
					ts, 
					new Timestamp(System.currentTimeMillis())
				});
				System.out.println("插入薪资记录: userId=" + userId + ", year=" + year + ", month=" + month + ", result=" + result);
			}
		} catch (Exception e) {
			System.out.println("插入薪资记录失败: userId=" + userId + ", error=" + e.getMessage());
			e.printStackTrace();
		}
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
		String sql = "SELECT SUM(d.budget) as total FROM demand d " +
				"INNER JOIN employee_work ew ON d.demandId = ew.demand_id " +
				"WHERE ew.userId = ? AND d.status = 1 AND YEAR(ew.work_start_time) = ? AND MONTH(ew.work_start_time) = ?";
		List<HashMap<String, Object>> result = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId, year, month});
		
		if(result != null && result.size() > 0 && result.get(0).get("total") != null) {
			BigDecimal totalBudget = new BigDecimal(result.get(0).get("total").toString());
			return totalBudget.multiply(new BigDecimal("0.01"));
		}
		return BigDecimal.ZERO;
	}
	
	/**
	 * 计算工作天数(根据employee_work表)
	 */
	private int calculateWorkDays(int userId, int year, int month) {
		String sql = "SELECT COUNT(DISTINCT DATE(work_start_time)) as workDays FROM employee_work " +
				"WHERE userId = ? AND YEAR(work_start_time) = ? AND MONTH(work_start_time) = ?";
		List<HashMap<String, Object>> result = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId, year, month});
		
		if(result != null && result.size() > 0 && result.get(0).get("workDays") != null) {
			return Integer.parseInt(result.get(0).get("workDays").toString());
		}
		return 0;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}