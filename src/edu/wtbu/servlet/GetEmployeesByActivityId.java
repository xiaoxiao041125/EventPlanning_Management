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
 * Servlet implementation class GetEmployeesByActivityId
 */
@WebServlet("/GetEmployeesByActivityId")
public class GetEmployeesByActivityId extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetEmployeesByActivityId() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String demandId = request.getParameter("demandId");
		if(demandId != null) {
			try {
				// 通过活动ID查询员工工作记录，并连接员工表获取详细信息
				String sql = "SELECT e.employee_id, e.userid, e.username, e.post, e.phone, e.sex, e.name, e.city, ew.work_start_time, ew.work_end_time FROM `employee` e JOIN `employee_work` ew ON e.userid = ew.userid WHERE ew.demand_id = ?";
				java.util.List<java.util.HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{demandId});
				if(list != null && list.size() > 0) {
					rs.setData(list);
					rs.setFlag("success");
				} else {
					rs.setData("没有找到该活动的员工记录");
				}
			} catch (Exception e) {
				rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
			}
		} else {
			rs.setData("请提供demandId参数");
		}
		
		response.getWriter().append(JSON.toJSONString(rs));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
