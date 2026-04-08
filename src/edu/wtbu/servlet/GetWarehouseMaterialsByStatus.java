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
 * Servlet implementation class GetWarehouseMaterialsByStatus
 */
@WebServlet("/GetWarehouseMaterialsByStatus")
public class GetWarehouseMaterialsByStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public GetWarehouseMaterialsByStatus() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String status = request.getParameter("status");
		
		if(status != null) {
			try {
				String sql = "SELECT material_id, category, material_type, quantity, price, remarks FROM warehouse";
				
				// 根据状态构建查询条件
				if("in_stock".equals(status)) {
					// 有库存（数量大于0）
					sql += " WHERE quantity REGEXP '^[1-9][0-9]*'";
				} else if("out_of_stock".equals(status)) {
					// 无库存（数量为0）
					sql += " WHERE quantity REGEXP '^0'";
				}
				
				java.util.List<java.util.HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, null);
				if(list != null && list.size() > 0) {
					rs.setData(list);
					rs.setFlag("success");
				} else {
					rs.setData("没有找到符合状态的物料记录");
				}
			} catch (Exception e) {
				e.printStackTrace();
				rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
			}
		} else {
			rs.setData("请提供status参数（in_stock/out_of_stock）");
		}
		
		response.getWriter().append(JSON.toJSONString(rs));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}