package edu.wtbu.servlet;

import java.io.IOException;
import java.sql.Timestamp;
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
 * Servlet implementation class PaySalary
 * 发放薪资 - 修改薪资记录状态为已发放
 */
@WebServlet("/PaySalary")
public class PaySalary extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public PaySalary() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String recordId = request.getParameter("recordId");
		String userId = request.getParameter("userId");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		
		System.out.println("PaySalary接收参数: recordId=" + recordId + ", userId=" + userId + ", year=" + year + ", month=" + month);
		
		try {
			if((recordId == null || recordId.isEmpty()) && 
			   (userId == null || userId.isEmpty() || year == null || year.isEmpty() || month == null || month.isEmpty())) {
				rs.setData("错误: 请提供recordId或(userId+year+month)参数");
				response.getWriter().append(JSON.toJSONString(rs));
				return;
			}
			
			String updateSql;
			Object[] params;
			
			if(recordId != null && !recordId.isEmpty()) {
				// 通过recordId更新
				updateSql = "UPDATE salary_record SET salary_status = 1, created_time = ? WHERE record_id = ? AND salary_status = 0";
				params = new Object[]{new Timestamp(System.currentTimeMillis()), Integer.parseInt(recordId)};
			} else {
				// 通过userId+year+month更新
				updateSql = "UPDATE salary_record SET salary_status = 1, created_time = ? WHERE user_id = ? AND year = ? AND month = ? AND salary_status = 0";
				params = new Object[]{new Timestamp(System.currentTimeMillis()), Integer.parseInt(userId), Integer.parseInt(year), Integer.parseInt(month)};
			}
			
			int result = MySqlHelper.executeUpdate(updateSql, params);
			
			if(result > 0) {
				rs.setData("薪资发放成功");
				rs.setFlag("success");
			} else {
				rs.setData("薪资发放失败：记录不存在或已发放");
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