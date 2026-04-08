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
 * Servlet implementation class EmployeeRegister
 */
@WebServlet("/EmployeeRegister")
public class EmployeeRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeeRegister() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String employeeName = request.getParameter("employeeName");
		String phone = request.getParameter("phone");
		String sex = request.getParameter("sex");
		String post = request.getParameter("post");
		String city = request.getParameter("city");
		Result rs = new Result(null, null, "fail");
		// 检查必填字段
		if ("" .equals(username) || "" .equals(password) || "" .equals(employeeName) || "" .equals(phone)) {
			rs.setData("用户名、密码、姓名、电话不能为空");
			response.getWriter().append(JSON.toJSONString(rs));
			return;
		}
		rs = EmployeeDao.register(username, password, employeeName, phone, sex, post, city);
		response.getWriter().append(JSON.toJSONString(rs));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 设置请求编码，确保中文参数正确处理
		request.setCharacterEncoding("UTF-8");
		doGet(request, response);
	}

}