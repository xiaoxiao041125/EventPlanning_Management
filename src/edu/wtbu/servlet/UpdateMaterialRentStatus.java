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
 * Servlet implementation class UpdateMaterialRentStatus
 */
@WebServlet("/UpdateMaterialRentStatus")
public class UpdateMaterialRentStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public UpdateMaterialRentStatus() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String rentId = request.getParameter("rentId");
		
		try {
			if(rentId == null || rentId.isEmpty()) {
				rs.setData("错误: 缺少rentId参数");
			} else {
				// 验证rentId是否为数字
				int rentIdInt = Integer.parseInt(rentId);
				
				// 1. 查询租借记录信息
				String querySql = "SELECT material_id, rent_quantity FROM material_rent WHERE rent_id = ?";
				java.util.List<java.util.HashMap<String, Object>> rentInfo = MySqlHelper.executeQueryReturnMap(querySql, new Object[]{rentIdInt});
				
				if(rentInfo != null && rentInfo.size() > 0) {
					// 获取物料ID和租借数量
					int materialId = Integer.parseInt(rentInfo.get(0).get("material_id").toString());
					String rentQuantityStr = rentInfo.get(0).get("rent_quantity").toString();
					
					// 提取租借数量的数字部分
					int rentQuantity = Integer.parseInt(rentQuantityStr.replaceAll("[^0-9]", ""));
					
					// 2. 查询仓库信息
					String warehouseSql = "SELECT quantity FROM warehouse WHERE material_id = ?";
					java.util.List<java.util.HashMap<String, Object>> warehouseInfo = MySqlHelper.executeQueryReturnMap(warehouseSql, new Object[]{materialId});
					
					if(warehouseInfo != null && warehouseInfo.size() > 0) {
						// 获取仓库数量
						String warehouseQuantityStr = warehouseInfo.get(0).get("quantity").toString();
						
						// 提取仓库数量的数字部分和单位
						String warehouseQuantityNumStr = warehouseQuantityStr.replaceAll("[^0-9]", "");
						String unit = warehouseQuantityStr.replaceAll("[0-9]", "").trim();
						
						// 计算新的数量
						int warehouseQuantity = Integer.parseInt(warehouseQuantityNumStr);
						int newQuantity = warehouseQuantity + rentQuantity;
						String newQuantityStr = newQuantity + unit;
						
						// 3. 更新仓库数量
						String updateWarehouseSql = "UPDATE warehouse SET quantity = ? WHERE material_id = ?";
						int warehouseRowsAffected = MySqlHelper.executeUpdate(updateWarehouseSql, new Object[]{newQuantityStr, materialId});
						
						// 4. 更新租借状态
						String updateRentSql = "UPDATE material_rent SET start = 1 WHERE rent_id = ?";
						int rentRowsAffected = MySqlHelper.executeUpdate(updateRentSql, new Object[]{rentIdInt});
						
						if(rentRowsAffected > 0 && warehouseRowsAffected > 0) {
							rs.setData("状态更新成功，库存已归还");
							rs.setFlag("success");
						} else {
							rs.setData("状态更新失败");
						}
					} else {
						rs.setData("未找到对应的仓库记录");
					}
				} else {
					rs.setData("未找到对应的租借记录");
				}
			}
		} catch (NumberFormatException e) {
			rs.setData("错误: rentId必须是数字");
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