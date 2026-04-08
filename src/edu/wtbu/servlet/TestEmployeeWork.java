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
 * Servlet implementation class TestEmployeeWork
 */
@WebServlet("/TestEmployeeWork")
public class TestEmployeeWork extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestEmployeeWork() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		try {
			// 直接测试查询employee_work表
			String sql = "SELECT * FROM employee_work";
			java.util.List<java.util.HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, null);
			if(list != null && list.size() > 0) {
				rs.setData(list);
				rs.setFlag("success");
			} else {
				rs.setData("employee_work表为空");
			}
		} catch (Exception e) {
			rs.setData("错误: " + e.getMessage());
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