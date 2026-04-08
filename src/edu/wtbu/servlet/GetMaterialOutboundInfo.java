package edu.wtbu.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.helper.MySqlHelper;
import edu.wtbu.pojo.Result;

/**
 * Servlet implementation class GetMaterialOutboundInfo
 */
@WebServlet("/GetMaterialOutboundInfo")
public class GetMaterialOutboundInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public GetMaterialOutboundInfo() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String materialId = request.getParameter("materialId");
		
		if(materialId != null) {
			try {
				int materialIdInt = Integer.parseInt(materialId);
				
				// 查询该物料在material_outbound表中的所有记录
				String outboundSql = "SELECT * FROM material_outbound WHERE material_id = ?";
				List<HashMap<String, Object>> outboundResult = MySqlHelper.executeQueryReturnMap(outboundSql, new Object[]{materialIdInt});
				
				// 查询warehouse表获取物料信息
				String warehouseSql = "SELECT material_id, category, material_type, quantity, price, remarks FROM warehouse WHERE material_id = ?";
				List<HashMap<String, Object>> warehouseResult = MySqlHelper.executeQueryReturnMap(warehouseSql, new Object[]{materialIdInt});
				
				if(warehouseResult != null && warehouseResult.size() > 0) {
					HashMap<String, Object> warehouseInfo = warehouseResult.get(0);
					// 提取仓库数量中的单位
					String warehouseQuantityStr = warehouseInfo.get("quantity").toString();
					String unit = warehouseQuantityStr.replaceAll("\\d+", "").trim();
					
					if(outboundResult != null && outboundResult.size() > 0) {
						// 有出库记录，返回所有出库记录，每条记录包含仓库信息
						for(HashMap<String, Object> outboundRecord : outboundResult) {
							// 获取出库数量
							int outboundQuantity = Integer.parseInt(outboundRecord.get("quantity").toString());
							// 构建新的数量字符串（出库数量 + 仓库单位）
							String newQuantityStr = outboundQuantity + (unit.isEmpty() ? "" : unit);
							
							// 先移除warehouseInfo中的quantity字段，避免覆盖
							HashMap<String, Object> warehouseInfoWithoutQuantity = new HashMap<>(warehouseInfo);
							warehouseInfoWithoutQuantity.remove("quantity");
							
							// 将仓库信息合并到出库记录中
							outboundRecord.putAll(warehouseInfoWithoutQuantity);
							// 设置新的quantity
							outboundRecord.put("quantity", newQuantityStr);
							outboundRecord.put("status", "已出库");
						}
						rs.setData(outboundResult);
						rs.setFlag("success");
					} else {
						// 无出库记录，返回仓库信息
						warehouseInfo.put("status", "未出库");
						rs.setData(warehouseInfo);
						rs.setFlag("success");
					}
				} else {
					rs.setData("没有找到该物料记录");
				}
			} catch (Exception e) {
				e.printStackTrace();
				rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
			}
		} else {
			rs.setData("请提供materialId参数");
		}
		
		response.getWriter().append(JSON.toJSONString(rs));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}