package edu.wtbu.servlet;

import java.io.IOException;
import java.math.BigDecimal;
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
 * Servlet implementation class GetActivityFinance
 * 获取活动收支数据 - 查询activity_progress >= 4的活动（已付定金或已完成）
 */
@WebServlet("/GetActivityFinance")
public class GetActivityFinance extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetActivityFinance() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String activityName = request.getParameter("activityName");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String status = request.getParameter("status");
		
		System.out.println("GetActivityFinance被调用");
		System.out.println("参数: activityName=" + activityName + ", startDate=" + startDate + ", endDate=" + endDate + ", status=" + status);
		
		try {
			// 查询activity_progress >= 4的活动（4-已付定金, 5-已付尾款/已完成）
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT demand_id, activity_place, activity_type, start_time, end_time, ");
			sql.append("total_activity_price, activity_material_amount, activity_progress ");
			sql.append("FROM activity_demand ");
			sql.append("WHERE activity_progress >= 4 ");
			
			List<Object> params = new ArrayList<>();
			
			// 活动名称筛选（activity_place + activity_type）
			if(activityName != null && !activityName.isEmpty()) {
				sql.append("AND (activity_place LIKE ? OR activity_type LIKE ?) ");
				params.add("%" + activityName + "%");
				params.add("%" + activityName + "%");
			}
			
			// 日期范围筛选
			if(startDate != null && !startDate.isEmpty()) {
				sql.append("AND start_time >= ? ");
				params.add(startDate);
			}
			if(endDate != null && !endDate.isEmpty()) {
				sql.append("AND end_time <= ? ");
				params.add(endDate);
			}
			
			// 状态筛选
			if(status != null && !status.isEmpty()) {
				if("deposit_paid".equals(status)) {
					// 已付定金 (activity_progress = 4)
					sql.append("AND activity_progress = 4 ");
				} else if("completed".equals(status)) {
					// 已完成 (activity_progress = 5)
					sql.append("AND activity_progress = 5 ");
				}
			}
			
			sql.append("ORDER BY demand_id DESC");
			
			System.out.println("执行SQL: " + sql.toString());
			System.out.println("参数: " + params);
			
			List<HashMap<String, Object>> activityList = MySqlHelper.executeQueryReturnMap(sql.toString(), params.toArray());
			
			System.out.println("查询结果数量: " + (activityList != null ? activityList.size() : "null"));
			
			// 处理返回数据
			List<HashMap<String, Object>> resultList = new ArrayList<>();
			BigDecimal totalIncome = BigDecimal.ZERO;
			BigDecimal totalExpense = BigDecimal.ZERO;
			BigDecimal totalProfit = BigDecimal.ZERO;
			
			if(activityList != null) {
				for(HashMap<String, Object> activity : activityList) {
					HashMap<String, Object> item = new HashMap<>();
					
					int demandId = Integer.parseInt(activity.get("demand_id").toString());
					String activityPlace = activity.get("activity_place") != null ? activity.get("activity_place").toString() : "";
					String activityType = activity.get("activity_type") != null ? activity.get("activity_type").toString() : "";
					
					// 活动名称 = activity_place + activity_type
					item.put("activityName", activityPlace + activityType);
					item.put("demandId", demandId);
					item.put("activityPlace", activityPlace);
					item.put("activityType", activityType);
					
					// 活动日期
					String startTime = activity.get("start_time") != null ? activity.get("start_time").toString() : "";
					item.put("activityDate", startTime);
					
					// 收入 = total_activity_price
					BigDecimal income = BigDecimal.ZERO;
					if(activity.get("total_activity_price") != null) {
						income = new BigDecimal(activity.get("total_activity_price").toString());
					}
					item.put("income", income);
					
					// 物料金额
					BigDecimal materialAmount = BigDecimal.ZERO;
					if(activity.get("activity_material_amount") != null) {
						materialAmount = new BigDecimal(activity.get("activity_material_amount").toString());
					}
					
					// 计算该活动的工资支出（查询salary_record中该活动相关的工资）
					BigDecimal salaryExpense = calculateActivitySalaryExpense(demandId);
					
					// 总支出 = 工资 + 物料金额
					BigDecimal expense = salaryExpense.add(materialAmount);
					item.put("expense", expense);
					
					// 利润 = 收入 - 支出
					BigDecimal profit = income.subtract(expense);
					item.put("profit", profit);
					
					// 状态
					int activityProgress = activity.get("activity_progress") != null ? 
							Integer.parseInt(activity.get("activity_progress").toString()) : 4;
					if(activityProgress == 4) {
						item.put("status", "deposit_paid");
						item.put("statusText", "已付定金");
					} else {
						item.put("status", "completed");
						item.put("statusText", "已完成");
					}
					
					// 累计总计
					totalIncome = totalIncome.add(income);
					totalExpense = totalExpense.add(expense);
					totalProfit = totalProfit.add(profit);
					
					resultList.add(item);
				}
			}
			
			// 返回数据
			HashMap<String, Object> result = new HashMap<>();
			result.put("activities", resultList);
			result.put("totalIncome", totalIncome);
			result.put("totalExpense", totalExpense);
			result.put("totalProfit", totalProfit);
			
			rs.setData(result);
			rs.setFlag("success");
			
		} catch (Exception e) {
			e.printStackTrace();
			rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
		}
		
		response.getWriter().append(JSON.toJSONString(rs));
	}
	
	/**
	 * 计算活动的工资支出
	 * 通过employee_work表关联查询该活动相关的工资
	 */
	private BigDecimal calculateActivitySalaryExpense(int demandId) {
		BigDecimal totalSalary = BigDecimal.ZERO;
		
		try {
			// 查询该活动关联的员工工作记录的工资
			// 这里假设employee_work表中有demand_id字段关联活动
			String sql = "SELECT SUM(s.total_salary) as total_salary " +
					 "FROM salary_record s " +
					 "INNER JOIN employee_work ew ON s.user_id = ew.userId " +
					 "WHERE ew.demand_id = ? AND s.salary_status = 1";
			
			List<HashMap<String, Object>> result = MySqlHelper.executeQueryReturnMap(sql, new Object[]{demandId});
			
			if(result != null && result.size() > 0 && result.get(0).get("total_salary") != null) {
				totalSalary = new BigDecimal(result.get(0).get("total_salary").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return totalSalary;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}