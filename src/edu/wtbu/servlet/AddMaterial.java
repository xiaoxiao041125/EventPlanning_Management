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
 * Servlet implementation class AddMaterial
 */
@WebServlet("/AddMaterial")
public class AddMaterial extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddMaterial() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String materialType = request.getParameter("materialType");
		String quantity = request.getParameter("quantity");
		String price = request.getParameter("price");
		String remarks = request.getParameter("remarks");
		String category = request.getParameter("category");
		
		// 检查必需参数（不为null且不为空字符串）
		if(materialType != null && !materialType.trim().isEmpty() &&
		   quantity != null && !quantity.trim().isEmpty() &&
		   price != null && !price.trim().isEmpty()) {
			try {
				// 插入新物料
				String sql = "INSERT INTO warehouse (material_type, quantity, price, remarks, category) VALUES (?, ?, ?, ?, ?)";
				int rows = MySqlHelper.executeUpdate(sql, new Object[]{materialType, quantity, price, remarks, category});
				if(rows > 0) {
					rs.setData("物料添加成功");
					rs.setFlag("success");
				} else {
					rs.setData("物料添加失败");
				}
			} catch (Exception e) {
				rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
			}
		} else {
			rs.setData("请提供materialType、quantity和price参数");
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
