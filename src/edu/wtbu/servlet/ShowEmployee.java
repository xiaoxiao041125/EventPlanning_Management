package edu.wtbu.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.dao.EmployeeDao;
import edu.wtbu.pojo.Result;

/**
 * Servlet implementation class ShowEmployee
 */
@WebServlet("/ShowEmployee")
public class ShowEmployee extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowEmployee() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		String city = request.getParameter("city");
		
		if(city != null && !"" .equals(city) && !"0".equals(city)) {
			// 测试城市查询
			Result rs = new Result(null, null, "fail");
			String sql = "SELECT e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city, u.start_day FROM `employee` e LEFT JOIN `user` u ON e.userId = u.userId WHERE e.city LIKE ?";
			java.util.List<java.util.HashMap<String, Object>> list = edu.wtbu.helper.MySqlHelper.executeQueryReturnMap(sql, new Object[]{"%" + city + "%"});
			if(list != null && list.size() > 0) {
				rs.setData(list);
				rs.setFlag("success");
			} else {
				rs.setData("没有找到匹配的员工记录");
			}
			response.getWriter().append(JSON.toJSONString(rs));
		} else {
			// 返回所有员工
			Result rs = EmployeeDao.showAllEmployee();
			response.getWriter().append(JSON.toJSONString(rs));
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
