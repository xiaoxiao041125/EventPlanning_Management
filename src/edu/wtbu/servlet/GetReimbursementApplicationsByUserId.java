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
 * Servlet implementation class GetReimbursementApplicationsByUserId
 */
@WebServlet("/GetReimbursementApplicationsByUserId")
public class GetReimbursementApplicationsByUserId extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public GetReimbursementApplicationsByUserId() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String userId = request.getParameter("userId");
		
		try {
			if(userId == null || userId.isEmpty()) {
				rs.setData("错误: 缺少userId参数");
			} else {
				// 验证userId是否为数字
				int userIdInt = Integer.parseInt(userId);
				
				// 构建SQL语句
				String sql = "SELECT ra.*, u.username, u.name, u.phone FROM reimbursement_application ra LEFT JOIN user u ON ra.user_id = u.userId WHERE ra.user_id = ? ORDER BY ra.created_time DESC";
				Object[] params = new Object[]{userIdInt};
				
				// 执行查询
				java.util.List<java.util.HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, params);
				
				if(list != null && list.size() > 0) {
					rs.setData(list);
					rs.setFlag("success");
				} else {
					rs.setData("未找到报销申请记录");
				}
			}
		} catch (NumberFormatException e) {
			rs.setData("错误: userId必须是数字");
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