package edu.wtbu.dao;

import java.util.HashMap;
import java.util.List;

import edu.wtbu.helper.MySqlHelper;
import edu.wtbu.pojo.Result;

public class UserDao {
	public static Result login(String username, String roleId, String password) {
		Result rs = new Result(null, null, "fail");
		
		// 第一步：检查用户名是否存在
		String sql = "SELECT * FROM `user` WHERE username = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new String[] { username });
		if (list == null || list.size() == 0) {
			rs.setData("用户名不存在");
			return rs;
		}
		
		// 用户名存在，获取用户信息
		HashMap<String, Object> user = list.get(0);
		String dbPassword = (String) user.get("password");
		String dbRoleId = String.valueOf(user.get("roleId"));
		
		// 第二步：检查密码是否正确
		if (!password.equals(dbPassword)) {
			rs.setData("密码错误");
			return rs;
		}
		
		// 第三步：检查角色是否正确
		if (!roleId.equals(dbRoleId)) {
			rs.setData("角色错误");
			return rs;
		}
		
		// 用户名、密码、角色都正确，查询完整用户信息（包含职位）
		sql = "SELECT " +
				"    u.*, " +
				"    IFNULL(e.post, '无职位') AS post " +
				"FROM " +
				"    `user` u " +
				"LEFT JOIN " +
				"    employee e " +
				"ON " +
				"    u.userID = e.userID " +
				"WHERE " +
				"    u.username = ? " +
				"    AND u.`password` = ? " +
				"    AND u.roleId = ?";
		list = MySqlHelper.executeQueryReturnMap(sql, new String[] { username, password, roleId });
		if (list != null && list.size() > 0) {
			rs.setFlag("success");
			rs.setData(list.get(0));
		}
		return rs;
	}

	public static Result register(String username, String password, String roleId) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT * FROM `user` WHERE username = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new String[] { username });
		if (list != null && list.size() > 0) {
			rs.setData("用户已存在");
		} else {
			sql = "INSERT INTO user (username, password, roleId) VALUES (?, ?, ?)";
			int tempRs = MySqlHelper.executeUpdate(sql, new Object[] { username, password, roleId });
			if (tempRs > 0) {
				rs.setFlag("success");
				rs.setData("注册成功");
			} else {
				rs.setData("注册失败，请重试");
			}
		}
		return rs;
	}

	public static Result updateUser(String userId, String phone, String sex, String name) {
		Result rs = new Result(null, null, "fail");
		String sql = "UPDATE user SET phone = ?, sex = ?, name = ? WHERE userId = ?";
		int temp = MySqlHelper.executeUpdate(sql, new Object[] { phone, sex, name, userId });
		if (temp > 0) {
			rs.setData("更新成功");
			rs.setFlag("success");
		} else {
			rs.setData("更新失败");
		}
		return rs;
	}

	public static Result updatePassword(String userId, String password, String newpassword) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT * FROM `user` WHERE userID = ? AND `password` = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new String[] { userId, password });
		if(list != null && list.size() > 0) {
			sql = "UPDATE user SET `password` = ? WHERE userId = ?";
			int temp = MySqlHelper.executeUpdate(sql, new Object[] { newpassword, userId });
			if(temp > 0) {
				rs.setData("修改成功");
				rs.setFlag("success");
			} else {
				rs.setData("修改失败");
			}
		} else {
			rs.setData("原密码错误");
		}
		return rs;
	}
	
	public static Result showUserByID(String userId) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT * FROM `user` WHERE userId = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[] { userId });
		if(list != null && list.size() > 0) {
			rs.setData(list.get(0));
			rs.setFlag("success");
		} else {
			rs.setData("用户不存在");
		}
		return rs;
	}
	
	public static Result showAllUser() {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT u.*, IFNULL(e.post, '') AS post FROM `user` u LEFT JOIN `employee` e ON u.userId = e.userId";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, null);
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到用户记录");
		}
		return rs;
	}
	
	public static Result searchUserByName(String username) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT u.*, IFNULL(e.post, '') AS post FROM `user` u LEFT JOIN `employee` e ON u.userId = e.userId WHERE u.username LIKE ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{"%" + username + "%"});
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到匹配的用户记录");
		}
		return rs;
	}
	
	public static Result searchUserByRealName(String name) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT u.*, IFNULL(e.post, '') AS post FROM `user` u LEFT JOIN `employee` e ON u.userId = e.userId WHERE u.name LIKE ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{"%" + name + "%"});
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到匹配的用户记录");
		}
		return rs;
	}
	
	public static Result searchUserById(String userId) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT u.*, IFNULL(e.post, '') AS post FROM `user` u LEFT JOIN `employee` e ON u.userId = e.userId WHERE u.userId LIKE ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{"%" + userId + "%"});
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到匹配的用户记录");
		}
		return rs;
	}
	
	public static Result updateUserStatus(String userId, String status) {
		Result rs = new Result(null, null, "fail");
		// 检查用户是否存在
		String sql = "SELECT * FROM `user` WHERE userId = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId});
		if(list == null || list.size() == 0) {
			rs.setData("没有找到该用户");
			return rs;
		}
		
		// 更新用户状态
		sql = "UPDATE `user` SET status = ? WHERE userId = ?";
		int updateResult = MySqlHelper.executeUpdate(sql, new Object[]{status, userId});
		if(updateResult > 0) {
			rs.setFlag("success");
			rs.setData("用户状态修改成功");
		} else {
			rs.setData("用户状态修改失败");
		}
		return rs;
	}
}
