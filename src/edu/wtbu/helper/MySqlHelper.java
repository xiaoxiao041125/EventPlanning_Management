package edu.wtbu.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

public class MySqlHelper {
	//初始化
	private static Connection conn = null;
	private static PreparedStatement pstmt = null;
	private static ResultSet rs = null;
	//数据库连接参数
	private static String url ="jdbc:mysql://localhost:3306/eventplanning_management?serverTimezone=GMT%2B8&useOldAliasMetadataBehavior=true&useUnicode=true&characterEncoding=UTF-8";
	private static String userName = "root";
	private static String password = "123456";
	private static String driver = "com.mysql.cj.jdbc.Driver";
	
	static {
		try {
			Class.forName(driver);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	//获取连接
	public static Connection getConnection() {
		try {
			conn = DriverManager.getConnection(url, userName, password);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return conn;
	}
	//查询
	public static List<HashMap<String, Object>> executeQueryReturnMap(String sql,Object[] parameters){
		List<HashMap<String, Object>> list = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			if(parameters !=null) {
				for (int i = 0; i < parameters.length; i++) {
					String className = parameters[i].getClass().getName();
					if(className.contains("String")) {
						pstmt.setString(i + 1,parameters[i].toString());
					}
					if(className.contains("Integer")) {
						pstmt.setInt(i + 1,Integer.parseInt(parameters[i].toString()));
					}
					if(className.contains("Double")) {
						pstmt.setDouble(i + 1,Double.parseDouble(parameters[i].toString()));
					}
					if(className.contains("Timestamp")) {
						pstmt.setTimestamp(i + 1,(java.sql.Timestamp)parameters[i]);
					}
				}
			}
			ResultSet rs = pstmt.executeQuery();
			java.sql.ResultSetMetaData rsmd = rs.getMetaData();
			list = new ArrayList<HashMap<String,Object>>();
			int columnNum = rsmd.getColumnCount();
			while (rs.next()) {
				HashMap<String,Object> map = new HashMap<String, Object>();
				for (int i = 0; i < columnNum; i++) {
					String columnName = rsmd.getColumnName(i + 1);
					Object value = rs.getObject(i + 1);
					// 处理日期时间类型，转换为字符串
					if (value instanceof java.sql.Timestamp) {
						java.sql.Timestamp timestamp = (java.sql.Timestamp) value;
						java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						value = sdf.format(timestamp);
					}
					map.put(columnName, value);
				}
				list.add(map);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			close(rs, pstmt, conn);
		}
		return list;
	}
	//更新
	public static int executeUpdate(String sql,Object[] parameters) {
		int result = 0;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			if(parameters !=null) {
				for (int i = 0; i < parameters.length; i++) {
					String className = parameters[i].getClass().getName();
					if(className.contains("String")) {
						pstmt.setString(i + 1,parameters[i].toString());
					}
					if(className.contains("Integer")) {
						pstmt.setInt(i + 1,Integer.parseInt(parameters[i].toString()));
					}
					if(className.contains("Double")) {
						pstmt.setDouble(i + 1,Double.parseDouble(parameters[i].toString()));
					}
					if(className.contains("Timestamp")) {
						pstmt.setTimestamp(i + 1,(java.sql.Timestamp)parameters[i]);
					}
				}
			}
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			close(rs, pstmt, conn);
		}
		return result;
	}
	
	// 执行更新并返回自增ID
	public static int executeUpdateAndReturnId(String sql,Object[] parameters) {
		int id = -1;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
			if(parameters !=null) {
				for (int i = 0; i < parameters.length; i++) {
					String className = parameters[i].getClass().getName();
					if(className.contains("String")) {
						pstmt.setString(i + 1,parameters[i].toString());
					}
					if(className.contains("Integer")) {
						pstmt.setInt(i + 1,Integer.parseInt(parameters[i].toString()));
					}
					if(className.contains("Double")) {
						pstmt.setDouble(i + 1,Double.parseDouble(parameters[i].toString()));
					}
					if(className.contains("Timestamp")) {
						pstmt.setTimestamp(i + 1,(java.sql.Timestamp)parameters[i]);
					}
				}
			}
			pstmt.executeUpdate();
			// 获取自增ID
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if(generatedKeys.next()) {
				id = generatedKeys.getInt(1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			close(rs, pstmt, conn);
		}
		return id;
	}
	//关闭连接
	public static void close(ResultSet rs,PreparedStatement pstmt,Connection conn) {
		if(rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		if(pstmt != null) {
			try {
				pstmt.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		if(conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}
