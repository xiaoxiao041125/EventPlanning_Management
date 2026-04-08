package edu.wtbu.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.dao.UserDao;
import edu.wtbu.pojo.Result;

/**
 * Servlet implementation class login
 */
@WebServlet("/login")
public class login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public login() {
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
		String roleId = request.getParameter("roleId");
		Result rs = new Result(null,null,"fail");
		if("".equals(username)||"".equals(password)||"".equals(roleId)) {
			rs.setData("请填写用户名、密码和角色");
			response.getWriter().append(JSON.toJSONString(rs));
			return;
		}
		rs = UserDao.login(username, roleId, password);
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
