package edu.wtbu.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.helper.MySqlHelper;
import edu.wtbu.pojo.Result;

/**
 * Servlet implementation class AddReimbursementApplication
 */
@WebServlet("/AddReimbursementApplication")
public class AddReimbursementApplication extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public AddReimbursementApplication() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");

		// 从请求中获取参数
		String userId = request.getParameter("userId");
		String reimbursementType = request.getParameter("reimbursementType");
		String amount = request.getParameter("amount");
		String expenseDate = request.getParameter("expenseDate");
		String description = request.getParameter("description");

		try {
			// 验证必填参数
			if(userId == null || userId.isEmpty() || 
			   reimbursementType == null || reimbursementType.isEmpty() || 
			   amount == null || amount.isEmpty() || 
			   expenseDate == null || expenseDate.isEmpty()) {
				rs.setData("错误: 缺少必填参数");
			} else {
				// 验证参数格式
				int userIdInt = Integer.parseInt(userId);
				int reimbursementTypeInt = Integer.parseInt(reimbursementType);
				double amountDouble = Double.parseDouble(amount);

				// 验证报销类型范围
				if(reimbursementTypeInt < 1 || reimbursementTypeInt > 5) {
					rs.setData("错误: 报销类型必须在1-5之间");
				} else {
					// 构建SQL语句
					String sql = "INSERT INTO reimbursement_application (user_id, reimbursement_type, amount, expense_date, description, status, created_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
					Object[] params = new Object[]{
						userIdInt,
						reimbursementTypeInt,
						amountDouble,
						expenseDate,
						description != null ? description : "",
						0, // 状态：0=待审批
						new Timestamp(new Date().getTime())
					};

					// 执行插入操作
					int rowsAffected = MySqlHelper.executeUpdate(sql, params);

					if(rowsAffected > 0) {
						rs.setData("报销申请提交成功");
						rs.setFlag("success");
					} else {
						rs.setData("报销申请提交失败");
					}
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}