package edu.wtbu.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * Servlet implementation class TestDatabaseStructure
 */
@WebServlet("/TestDatabaseStructure")
public class TestDatabaseStructure extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestDatabaseStructure() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		Result rs = new Result(null, null, "fail");
		
		try {
			Connection conn = MySqlHelper.getConnection();
			if(conn != null) {
				// 获取所有表
				DatabaseMetaData metaData = conn.getMetaData();
				ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
				
				List<HashMap<String, Object>> tableList = new ArrayList<>();
				while(tables.next()) {
					String tableName = tables.getString("TABLE_NAME");
					HashMap<String, Object> tableInfo = new HashMap<>();
					tableInfo.put("tableName", tableName);
					
					// 获取表的所有字段
					ResultSet columns = metaData.getColumns(null, null, tableName, "%");
					List<HashMap<String, Object>> columnList = new ArrayList<>();
					while(columns.next()) {
						HashMap<String, Object> columnInfo = new HashMap<>();
						columnInfo.put("columnName", columns.getString("COLUMN_NAME"));
						columnInfo.put("columnType", columns.getString("TYPE_NAME"));
						columnList.add(columnInfo);
					}
					columns.close();
					tableInfo.put("columns", columnList);
					tableList.add(tableInfo);
				}
				tables.close();
				conn.close();
				
				rs.setData(tableList);
				rs.setFlag("success");
			} else {
				rs.setData("数据库连接失败");
			}
		} catch (SQLException e) {
			rs.setData("错误: " + e.getMessage());
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