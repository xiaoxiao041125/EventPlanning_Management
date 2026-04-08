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
 * Servlet implementation class GetMaterialRentInfo
 */
@WebServlet("/GetMaterialRentInfo")
public class GetMaterialRentInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public GetMaterialRentInfo() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String materialId = request.getParameter("materialId");
		String rentUserName = request.getParameter("rentUserName");
		String category = request.getParameter("category");
		String status = request.getParameter("status");
		
		try {
			String sql = "SELECT * FROM material_rent";
			Object[] params = null;
			
			// 根据参数构建查询条件
			if(materialId != null || rentUserName != null || category != null || status != null) {
				sql += " WHERE ";
				boolean hasCondition = false;
				
				if(materialId != null) {
					sql += " material_id = ?";
					hasCondition = true;
				} else if(rentUserName != null) {
					sql += " rent_user_name LIKE ?";
					hasCondition = true;
				} else if(category != null) {
					// 根据category查询，需要连接warehouse表
					sql = "SELECT mr.* FROM material_rent mr JOIN warehouse w ON mr.material_id = w.material_id WHERE w.category = ?";
					hasCondition = true;
				} else if(status != null) {
					// 根据状态查询（前端传数字：0=租借，1=已归还）
					if("1".equals(status)) {
						sql += " start = 1";
					} else if("0".equals(status)) {
						sql += " start = 0";
					}
					hasCondition = true;
				}
				
				// 设置参数
				if(hasCondition) {
					if(materialId != null || rentUserName != null || category != null) {
						params = new Object[1];
						if(materialId != null) {
							params[0] = Integer.parseInt(materialId);
						} else if(rentUserName != null) {
							params[0] = "%" + rentUserName + "%";
						} else if(category != null) {
							params[0] = category;
						}
					}
					// status参数不需要参数值
				}
			}
			
			// 执行查询
			java.util.List<java.util.HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, params);
			
			if(list != null && list.size() > 0) {
				// 为每条租借记录添加仓库信息
				for(java.util.HashMap<String, Object> record : list) {
					if(record.get("material_id") != null) {
						int materialIdInt = Integer.parseInt(record.get("material_id").toString());
						// 查询仓库信息（除了price）
						String warehouseSql = "SELECT material_id, category, material_type, quantity, remarks FROM warehouse WHERE material_id = ?";
						java.util.List<java.util.HashMap<String, Object>> warehouseResult = MySqlHelper.executeQueryReturnMap(warehouseSql, new Object[]{materialIdInt});
						if(warehouseResult != null && warehouseResult.size() > 0) {
							// 合并仓库信息到租借记录
							record.putAll(warehouseResult.get(0));
						}
					}
				}
				rs.setData(list);
				rs.setFlag("success");
			} else {
				rs.setData("没有找到租借记录");
			}
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