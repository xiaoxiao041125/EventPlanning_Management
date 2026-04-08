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
 * Servlet implementation class AddMaterialOutbound
 */
@WebServlet("/AddMaterialOutbound")
public class AddMaterialOutbound extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddMaterialOutbound() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String demandId = request.getParameter("demandId");
		String materialId = request.getParameter("materialId");
		String materialTotalPrice = request.getParameter("materialTotalPrice");
		String quantity = request.getParameter("quantity");
		String userId = request.getParameter("userId");
		
		// Check required parameters
		if(demandId == null || demandId.trim().isEmpty() ||
		   materialId == null || materialId.trim().isEmpty() ||
		   materialTotalPrice == null || materialTotalPrice.trim().isEmpty() ||
		   quantity == null || quantity.trim().isEmpty() ||
		   userId == null || userId.trim().isEmpty()) {
			rs.setData("请提供demandId、materialId、materialTotalPrice、quantity和userId参数");
			response.getWriter().append(JSON.toJSONString(rs));
			return;
		}
		
		try {
			// Convert parameters to Integer
			int demandIdInt = Integer.parseInt(demandId);
			int materialIdInt = Integer.parseInt(materialId);
			int materialTotalPriceInt = Integer.parseInt(materialTotalPrice);
			int quantityInt = Integer.parseInt(quantity);
			int userIdInt = Integer.parseInt(userId);
			
			// Check if material exists and get stock quantity
			String checkMaterialSql = "SELECT quantity FROM warehouse WHERE material_id = ?";
			java.util.List<java.util.HashMap<String, Object>> materialResult = MySqlHelper.executeQueryReturnMap(checkMaterialSql, new Object[]{materialIdInt});
			if(materialResult == null || materialResult.size() == 0) {
				rs.setData("错误: 物料ID不存在");
				response.getWriter().append(JSON.toJSONString(rs));
				return;
			}
			
			// 获取仓库中的数量
			String warehouseQuantityStr = materialResult.get(0).get("quantity").toString();
			// 提取数字部分
			String warehouseQuantityNumStr = warehouseQuantityStr.replaceAll("\\D+", "");
			if(warehouseQuantityNumStr.isEmpty()) {
				rs.setData("错误: 物料数量格式不正确");
				response.getWriter().append(JSON.toJSONString(rs));
				return;
			}
			int warehouseQuantity = Integer.parseInt(warehouseQuantityNumStr);
			
			// 检查库存是否足够
			if(warehouseQuantity < quantityInt) {
				rs.setData("数量不足，请重试");
				response.getWriter().append(JSON.toJSONString(rs));
				return;
			}
			
			// Calculate new stock quantity
			int newQuantity = warehouseQuantity - quantityInt;
			// Extract unit part
			String unit = warehouseQuantityStr.replaceAll("\\d+", "").trim();
			// Build new quantity string
			String newQuantityStr = newQuantity + (unit.isEmpty() ? "" : unit);
			
			// Check if user exists
			String checkUserSql = "SELECT COUNT(*) FROM employee WHERE userid = ?";
			java.util.List<java.util.HashMap<String, Object>> userResult = MySqlHelper.executeQueryReturnMap(checkUserSql, new Object[]{userIdInt});
			if(userResult == null || userResult.size() == 0 || userResult.get(0).get("COUNT(*)") == null || Integer.parseInt(userResult.get(0).get("COUNT(*)").toString()) == 0) {
				rs.setData("错误: 用户ID不存在");
				response.getWriter().append(JSON.toJSONString(rs));
				return;
			}
			
			// 检查活动需求是否存在（如果demandId不为0）
			if(demandIdInt != 0) {
				String checkDemandSql = "SELECT COUNT(*) FROM activity_demand WHERE demand_id = ?";
				java.util.List<java.util.HashMap<String, Object>> demandResult = MySqlHelper.executeQueryReturnMap(checkDemandSql, new Object[]{demandIdInt});
				if(demandResult == null || demandResult.size() == 0 || demandResult.get(0).get("COUNT(*)") == null || Integer.parseInt(demandResult.get(0).get("COUNT(*)").toString()) == 0) {
					rs.setData("错误: 活动需求ID不存在");
					response.getWriter().append(JSON.toJSONString(rs));
					return;
				}
			}
			
			// 插入出库记录
			String sql = "INSERT INTO material_outbound (outbound_id, demand_id, material_id, material_total_price, quantity, userid) VALUES (NULL, ?, ?, ?, ?, ?)";
			System.out.println("执行SQL: " + sql);
			System.out.println("参数: demandId=" + demandIdInt + ", materialId=" + materialIdInt + ", materialTotalPrice=" + materialTotalPriceInt + ", quantity=" + quantityInt + ", userId=" + userIdInt);
			int rows = MySqlHelper.executeUpdate(sql, new Object[]{demandIdInt, materialIdInt, materialTotalPriceInt, quantityInt, userIdInt});
			System.out.println("影响行数: " + rows);
			if(rows > 0) {
				// 更新仓库库存数量
				String updateWarehouseSql = "UPDATE warehouse SET quantity = ? WHERE material_id = ?";
				int updateRows = MySqlHelper.executeUpdate(updateWarehouseSql, new Object[]{newQuantityStr, materialIdInt});
				System.out.println("更新库存影响行数: " + updateRows);
				
				// 计算并更新活动需求的物料总金额
				if(demandIdInt != 0) {
					// 计算相同demand_id的物料总金额
					String calculateTotalSql = "SELECT SUM(material_total_price) as total_amount FROM material_outbound WHERE demand_id = ?";
					java.util.List<java.util.HashMap<String, Object>> totalResult = MySqlHelper.executeQueryReturnMap(calculateTotalSql, new Object[]{demandIdInt});
					if(totalResult != null && totalResult.size() > 0 && totalResult.get(0).get("total_amount") != null) {
						int totalAmount = Integer.parseInt(totalResult.get(0).get("total_amount").toString());
						// 更新activity_demand表中的activity_material_amount字段
						String updateDemandSql = "UPDATE activity_demand SET activity_material_amount = ? WHERE demand_id = ?";
						int demandUpdateRows = MySqlHelper.executeUpdate(updateDemandSql, new Object[]{totalAmount, demandIdInt});
						System.out.println("更新活动需求物料金额影响行数: " + demandUpdateRows);
					}
				}
				
				if(updateRows > 0) {
					rs.setData("物料出库记录添加成功，库存已更新");
					rs.setFlag("success");
				} else {
					rs.setData("物料出库记录添加成功，但库存更新失败");
					rs.setFlag("success");
				}
			} else {
				rs.setData("物料出库记录添加失败");
			}
		} catch (NumberFormatException e) {
			rs.setData("错误: 参数格式不正确，请检查数字参数");
		} catch (Exception e) {
			e.printStackTrace();
			rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
		}
		
		response.getWriter().append(JSON.toJSONString(rs));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}