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
 * Servlet implementation class AddMaterialRent
 */
@WebServlet("/AddMaterialRent")
public class AddMaterialRent extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public AddMaterialRent() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String materialId = request.getParameter("materialId");
		String rentQuantity = request.getParameter("rentQuantity");
		String unitPrice = request.getParameter("unitPrice");
		String rentUserName = request.getParameter("rentUserName");
		String rentPhone = request.getParameter("rentPhone");
		String returnTime = request.getParameter("returnTime");
		
		// 检查必需参数
		if(materialId != null && rentQuantity != null && unitPrice != null && rentUserName != null && rentPhone != null && returnTime != null) {
			try {
				// 转换参数类型
				int materialIdInt = Integer.parseInt(materialId);
				int rentQuantityInt = Integer.parseInt(rentQuantity);
				int unitPriceInt = Integer.parseInt(unitPrice);
				
				// 检查物料是否存在并获取当前库存
				String checkMaterialSql = "SELECT quantity FROM warehouse WHERE material_id = ?";
				System.out.println("执行SQL: " + checkMaterialSql);
				System.out.println("参数: materialId=" + materialIdInt);
				java.util.List<java.util.HashMap<String, Object>> materialResult = MySqlHelper.executeQueryReturnMap(checkMaterialSql, new Object[]{materialIdInt});
				
				if(materialResult == null || materialResult.size() == 0 || materialResult.get(0).get("quantity") == null) {
					System.out.println("物料不存在: " + materialIdInt);
					rs.setData("错误: 物料ID不存在");
					return;
				}
				
				// 获取当前库存数量和单位
				String currentQuantityStr = materialResult.get(0).get("quantity").toString();
				System.out.println("当前库存: " + currentQuantityStr);
				// 提取数字部分
				String quantityNumStr = currentQuantityStr.replaceAll("\\D+", "");
				System.out.println("库存数量: " + quantityNumStr);
				int currentQuantity = Integer.parseInt(quantityNumStr);
				// 提取单位部分
				String unit = currentQuantityStr.replaceAll("\\d+", "").trim();
				System.out.println("库存单位: " + unit);
				
				// 检查库存是否足够
				System.out.println("租借数量: " + rentQuantityInt);
				if(currentQuantity < rentQuantityInt) {
					System.out.println("库存不足: 当前" + currentQuantity + "，需要" + rentQuantityInt);
					rs.setData("错误: 库存不足，当前库存为" + currentQuantityStr);
					return;
				}
				
				// 计算新的库存数量
				int newQuantity = currentQuantity - rentQuantityInt;
				String newQuantityStr = newQuantity + (unit.isEmpty() ? "" : unit);
				System.out.println("新库存: " + newQuantityStr);
				
				// 更新仓库库存
				String updateWarehouseSql = "UPDATE warehouse SET quantity = ? WHERE material_id = ?";
				System.out.println("执行SQL: " + updateWarehouseSql);
				System.out.println("参数: newQuantityStr=" + newQuantityStr + ", materialId=" + materialIdInt);
				// 确保参数类型正确
				Object[] updateParams = new Object[]{newQuantityStr, Integer.valueOf(materialIdInt)};
				int updateRows = MySqlHelper.executeUpdate(updateWarehouseSql, updateParams);
				System.out.println("更新库存影响行数: " + updateRows);
				
				if(updateRows > 0) {
					// 插入租借记录
					String sql = "INSERT INTO material_rent (rent_id, material_id, rent_quantity, unit_price, rent_user_name, rent_phone, return_time) VALUES (NULL, ?, ?, ?, ?, ?, ?)";
					System.out.println("执行SQL: " + sql);
					System.out.println("参数: materialId=" + materialIdInt + ", rentQuantity=" + rentQuantityInt + ", unitPrice=" + unitPriceInt + ", rentUserName=" + rentUserName + ", rentPhone=" + rentPhone + ", returnTime=" + returnTime);
					int rows = MySqlHelper.executeUpdate(sql, new Object[]{materialIdInt, rentQuantityInt, unitPriceInt, rentUserName, rentPhone, returnTime});
					System.out.println("插入租借记录影响行数: " + rows);
					
					if(rows > 0) {
						rs.setData("租借记录添加成功，库存已更新");
						rs.setFlag("success");
					} else {
						rs.setData("租借记录添加失败");
					}
				} else {
					rs.setData("库存更新失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
				rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
			}
		} else {
			rs.setData("请提供materialId、rentQuantity、unitPrice、rentUserName、rentPhone和returnTime参数");
		}
		
		response.getWriter().append(JSON.toJSONString(rs));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}