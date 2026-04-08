package edu.wtbu.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.pojo.Result;

/**
 * Servlet implementation class TestDatabase
 */
@WebServlet("/TestDatabase")
public class TestDatabase extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestDatabase() {
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
			// 直接测试数据库连接
			java.sql.Connection conn = null;
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				String url = "jdbc:mysql://localhost:3306/eventplanning_management?serverTimezone=GMT%2B8&useOldAliasMetadataBehavior=true&useUnicode=true&characterEncoding=UTF-8";
				String userName = "root";
				String password = "123456";
				conn = java.sql.DriverManager.getConnection(url, userName, password);
				System.out.println("Database connection successful!");
				
				// 测试employee表是否存在
				java.sql.Statement stmt = conn.createStatement();
				java.sql.ResultSet rsTables = conn.getMetaData().getTables(null, null, "employee", null);
				if(rsTables.next()) {
					System.out.println("Employee table exists!");
					
					// 测试employee表的字段
					java.sql.ResultSet rsColumns = conn.getMetaData().getColumns(null, null, "employee", null);
					System.out.println("Employee table columns:");
					while(rsColumns.next()) {
						System.out.println(rsColumns.getString("COLUMN_NAME"));
					}
					
					// 测试查询employee表数据
					java.sql.ResultSet rsData = stmt.executeQuery("SELECT * FROM employee");
					int count = 0;
					while(rsData.next()) {
						count++;
						// 打印第一条记录的所有字段
						if(count == 1) {
							java.sql.ResultSetMetaData rsmd = rsData.getMetaData();
							System.out.println("First record:");
							for(int i = 1; i <= rsmd.getColumnCount(); i++) {
								System.out.println(rsmd.getColumnName(i) + ": " + rsData.getObject(i));
							}
						}
					}
					System.out.println("Employee table has " + count + " records");
					
					if(count > 0) {
						rs.setData("Employee table has " + count + " records");
						rs.setFlag("success");
					} else {
						rs.setData("Employee table is empty");
					}
				} else {
					System.out.println("Employee table does NOT exist!");
					rs.setData("Employee table does NOT exist");
				}
			} finally {
				if(conn != null) {
					conn.close();
				}
			}
		} catch (Exception e) {
			System.out.println("Database error: " + e.getMessage());
			rs.setData("Database error: " + e.getMessage());
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