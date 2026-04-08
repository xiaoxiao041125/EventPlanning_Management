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
 * Servlet implementation class GetAllReimbursementApplications
 */
@WebServlet("/GetAllReimbursementApplications")
public class GetAllReimbursementApplications extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public GetAllReimbursementApplications() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		try {
			// 获取查询参数
			String reimbursementType = request.getParameter("reimbursementType");
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			String status = request.getParameter("status");
			
			// 构建SQL语句
			String sql = "SELECT ra.*, u.username, u.name, u.sex, u.phone FROM reimbursement_application ra LEFT JOIN user u ON ra.user_id = u.userId WHERE 1=1";
			java.util.List<Object> params = new java.util.ArrayList<>();
			
			// 添加报销类型条件
			if(reimbursementType != null && !reimbursementType.isEmpty()) {
				int type = Integer.parseInt(reimbursementType);
				sql += " AND ra.reimbursement_type = ?";
				params.add(type);
			}
			
			// 添加申请时间范围条件
			if(startDate != null && !startDate.isEmpty()) {
				sql += " AND ra.created_time >= ?";
				params.add(startDate);
			}
			
			if(endDate != null && !endDate.isEmpty()) {
				sql += " AND ra.created_time <= ?";
				params.add(endDate);
			}
			
			// 添加状态条件
			if(status != null && !status.isEmpty()) {
				int statusInt = Integer.parseInt(status);
				sql += " AND ra.status = ?";
				params.add(statusInt);
			}
			
			// 添加排序
			sql += " ORDER BY ra.created_time DESC";
			
			// 执行查询
			java.util.List<java.util.HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, params.toArray());
			
			if(list != null && list.size() > 0) {
				rs.setData(list);
				rs.setFlag("success");
			} else {
				rs.setData("未找到报销申请记录");
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