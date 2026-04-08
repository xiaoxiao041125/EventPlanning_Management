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
 * Servlet implementation class UpdateMaterialQuantity
 */
@WebServlet("/UpdateMaterialQuantity")
public class UpdateMaterialQuantity extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateMaterialQuantity() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String materialId = request.getParameter("materialId");
		String quantity = request.getParameter("quantity");
		
		if(materialId != null && quantity != null) {
			try {
				// 先获取物料的当前数量
				String getQuantitySql = "SELECT quantity FROM warehouse WHERE material_id = ?";
				java.util.List<java.util.HashMap<String, Object>> result = MySqlHelper.executeQueryReturnMap(getQuantitySql, new Object[]{materialId});
				if(result == null || result.size() == 0) {
					rs.setData("没有找到该物料记录");
					return;
				}
				
				// 获取当前数量
				String currentQuantityStr = result.get(0).get("quantity").toString();
				// 提取数字部分
				String currentQuantityNumStr = currentQuantityStr.replaceAll("\\D+", "");
				if(currentQuantityNumStr.isEmpty()) {
					rs.setData("物料数量格式不正确");
					return;
				}
				int currentQuantity = Integer.parseInt(currentQuantityNumStr);
				
				// 提取单位部分
				String unit = currentQuantityStr.replaceAll("\\d+", "").trim();
				
				// 解析前端传来的数量
				String newQuantityNumStr = quantity.replaceAll("\\D+", "");
				if(newQuantityNumStr.isEmpty()) {
					rs.setData("输入的数量格式不正确");
					return;
				}
				int addQuantity = Integer.parseInt(newQuantityNumStr);
				
				// 计算新的数量
				int newTotalQuantity = currentQuantity + addQuantity;
				// 构建新的数量字符串（保留原单位）
				String newQuantityStr = newTotalQuantity + (unit.isEmpty() ? "" : unit);
				
				// 更新物料数量
				String updateSql = "UPDATE warehouse SET quantity = ? WHERE material_id = ?";
				int rows = MySqlHelper.executeUpdate(updateSql, new Object[]{newQuantityStr, materialId});
				if(rows > 0) {
					rs.setData("物料数量更新成功");
					rs.setFlag("success");
				} else {
					rs.setData("没有找到该物料记录");
				}
			} catch (Exception e) {
				rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
			}
		} else {
			rs.setData("请提供materialId和quantity参数");
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
