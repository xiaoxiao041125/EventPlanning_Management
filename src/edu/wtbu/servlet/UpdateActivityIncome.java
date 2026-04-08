package edu.wtbu.servlet;

import java.io.IOException;
import java.math.BigDecimal;
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
 * Servlet implementation class UpdateActivityIncome
 * 更新活动收入 - 收入核定
 */
@WebServlet("/UpdateActivityIncome")
public class UpdateActivityIncome extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UpdateActivityIncome() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String demandId = request.getParameter("demandId");
		String income = request.getParameter("income");
		
		try {
			if(demandId == null || demandId.isEmpty() || income == null || income.isEmpty()) {
				rs.setData("错误: 请提供活动ID和收入金额");
				response.getWriter().append(JSON.toJSONString(rs));
				return;
			}
			
			// 验证收入金额
			BigDecimal incomeAmount;
			try {
				incomeAmount = new BigDecimal(income);
				if(incomeAmount.compareTo(BigDecimal.ZERO) < 0) {
					rs.setData("错误: 收入金额不能为负数");
					response.getWriter().append(JSON.toJSONString(rs));
					return;
				}
			} catch (NumberFormatException e) {
				rs.setData("错误: 收入金额格式不正确");
				response.getWriter().append(JSON.toJSONString(rs));
				return;
			}
			
			// 检查活动是否存在
			String checkSql = "SELECT demand_id FROM activity_demand WHERE demand_id = ?";
			List<HashMap<String, Object>> checkResult = MySqlHelper.executeQueryReturnMap(checkSql, new Object[]{Integer.parseInt(demandId)});
			
			if(checkResult == null || checkResult.size() == 0) {
				rs.setData("错误: 活动不存在");
				response.getWriter().append(JSON.toJSONString(rs));
				return;
			}
			
			// 更新活动收入
			String updateSql = "UPDATE activity_demand SET total_activity_price = ? WHERE demand_id = ?";
			int result = MySqlHelper.executeUpdate(updateSql, new Object[]{incomeAmount.toString(), Integer.parseInt(demandId)});
			
			if(result > 0) {
				rs.setData("收入核定成功");
				rs.setFlag("success");
			} else {
				rs.setData("收入核定失败");
			}
			
		} catch (NumberFormatException e) {
			rs.setData("错误: 参数格式不正确");
		} catch (Exception e) {
			e.printStackTrace();
			rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
		}
		
		response.getWriter().append(JSON.toJSONString(rs));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}