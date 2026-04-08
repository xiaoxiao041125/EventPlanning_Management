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
 * Servlet implementation class ApproveReimbursementApplication
 */
@WebServlet("/ApproveReimbursementApplication")
public class ApproveReimbursementApplication extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public ApproveReimbursementApplication() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String reimbursementId = request.getParameter("reimbursementId");
		String status = request.getParameter("status");
		String approverId = request.getParameter("approverId");
		
		try {
			if(reimbursementId == null || reimbursementId.isEmpty() || status == null || status.isEmpty() || approverId == null || approverId.isEmpty()) {
				rs.setData("错误: 缺少必填参数");
			} else {
				// 验证参数格式
				int reimbursementIdInt = Integer.parseInt(reimbursementId);
				int statusInt = Integer.parseInt(status);
				int approverIdInt = Integer.parseInt(approverId);
				
				// 验证状态值
				if(statusInt != 1 && statusInt != 2) {
					rs.setData("错误: 状态值必须为1（批准）或2（拒绝）");
				} else {
					// 构建SQL语句
					String sql = "UPDATE reimbursement_application SET status = ?, approver_id = ?, approval_time = ? WHERE reimbursement_id = ?";
					Object[] params = new Object[]{
						statusInt,
						approverIdInt,
						new Timestamp(new Date().getTime()),
						reimbursementIdInt
					};
					
					// 执行更新操作
					int rowsAffected = MySqlHelper.executeUpdate(sql, params);
					
					if(rowsAffected > 0) {
						if(statusInt == 1) {
							rs.setData("报销申请已批准");
						} else {
							rs.setData("报销申请已拒绝");
						}
						rs.setFlag("success");
					} else {
						rs.setData("操作失败，未找到对应的报销申请");
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