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
 * Servlet implementation class GetOutboundDetailById
 */
@WebServlet("/GetOutboundDetailById")
public class GetOutboundDetailById extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public GetOutboundDetailById() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		String outboundId = request.getParameter("outboundId");
		
		if(outboundId != null) {
			try {
				int outboundIdInt = Integer.parseInt(outboundId);
				
				// 查询material_outbound表获取出库记录
				String outboundSql = "SELECT * FROM material_outbound WHERE outbound_id = ?";
				List<HashMap<String, Object>> outboundResult = MySqlHelper.executeQueryReturnMap(outboundSql, new Object[]{outboundIdInt});
				
				if(outboundResult != null && outboundResult.size() > 0) {
					HashMap<String, Object> outboundInfo = outboundResult.get(0);
					int demandId = Integer.parseInt(outboundInfo.get("demand_id").toString());
					
					// 查询activity_demand表获取活动需求信息
					String demandSql = "SELECT * FROM activity_demand WHERE demand_id = ?";
					List<HashMap<String, Object>> demandResult = MySqlHelper.executeQueryReturnMap(demandSql, new Object[]{demandId});
					
					if(demandResult != null && demandResult.size() > 0) {
						HashMap<String, Object> demandInfo = demandResult.get(0);
						
						// 获取principal_userid
						Object principalUserIdObj = demandInfo.get("principal_userid");
						if(principalUserIdObj != null) {
							int principalUserId = Integer.parseInt(principalUserIdObj.toString());
							
							// 查询employee表获取负责人姓名
							String employeeSql = "SELECT name FROM employee WHERE userid = ?";
							List<HashMap<String, Object>> employeeResult = MySqlHelper.executeQueryReturnMap(employeeSql, new Object[]{principalUserId});
							
							if(employeeResult != null && employeeResult.size() > 0) {
								String principalName = employeeResult.get(0).get("name").toString();
								demandInfo.put("principal_name", principalName);
							}
						}
						
						// 将出库信息和活动需求信息合并
						outboundInfo.putAll(demandInfo);
					}
					
					rs.setData(outboundInfo);
					rs.setFlag("success");
				} else {
					rs.setData("没有找到该出库记录");
				}
			} catch (Exception e) {
				e.printStackTrace();
				rs.setData("错误: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
			}
		} else {
			rs.setData("请提供outboundId参数");
		}
		
		response.getWriter().append(JSON.toJSONString(rs));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}