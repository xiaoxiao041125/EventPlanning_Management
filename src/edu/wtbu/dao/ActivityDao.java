package edu.wtbu.dao;

import java.util.HashMap;
import java.util.List;

import edu.wtbu.helper.MySqlHelper;
import edu.wtbu.pojo.Result;

public class ActivityDao {
	
	// 有效的城市列表
	private static final String[] VALID_CITIES = {"武汉", "北京", "广州", "深圳", "上海", "南京", "杭州", "苏州", "成都", "重庆"};
	
	// 有效的活动类型列表
	private static final String[] VALID_ACTIVITY_TYPES = {
		"公司年会", "周年庆典", "会议论坛", "开业庆典", "新品发布会", "团建拓展", 
		"仪式活动", "酒会宴会", "节点活动", "商场地产", "路演巡展", "快闪美陈",
		"展会展览", "时尚走秀", "答谢会", "家庭日", "嘉年华", "演唱会", "其他活动"
	};
	
	public static Result applyActivity(String userId, String activity_place, String activity_type, 
			String start_time, String end_time, String activity_people, String activity_budget, 
			String activity_progress, String requirement_desc, String phone, String name) {
		Result rs = new Result(null, null, "fail");
		
		System.out.println("ActivityDao.applyActivity开始验证:");
		System.out.println("  activity_place=" + activity_place + ", 是否有效=" + isValidCity(activity_place));
		System.out.println("  activity_type=" + activity_type + ", 是否有效=" + isValidActivityType(activity_type));
		System.out.println("  start_time=" + start_time);
		System.out.println("  name=" + name + ", 长度=" + (name != null ? name.length() : 0));
		
		// 1. 校验手机号（必填）
		if (phone == null || phone.trim().isEmpty()) {
			rs.setData("手机号不能为空");
			System.out.println("  验证失败: 手机号为空");
			return rs;
		}
		if (!phone.matches("^1[3-9]\\d{9}$")) {
			rs.setData("手机号格式不正确，请输入11位有效手机号");
			System.out.println("  验证失败: 手机号格式不正确");
			return rs;
		}
		
		// 2. 校验称呼/姓名（必填）
		if (name == null || name.trim().isEmpty()) {
			rs.setData("称呼不能为空");
			System.out.println("  验证失败: 称呼为空");
			return rs;
		}
		if (name.length() < 2 || name.length() >= 20) {
			rs.setData("称呼长度应在2-19个字符之间");
			System.out.println("  验证失败: 称呼长度不在2-19之间, 实际长度=" + name.length());
			return rs;
		}
		if (!name.matches("^[\u4e00-\u9fa5a-zA-Z]+$")) {
			rs.setData("称呼只能包含中文或英文字母");
			System.out.println("  验证失败: 称呼包含非法字符");
			return rs;
		}
		
		// 3. 校验城市
		if (!isValidCity(activity_place)) {
			rs.setData("暂无该城市业务，目前支持的城市：武汉、北京、广州、深圳、上海、南京、杭州、苏州、成都、重庆");
			System.out.println("  验证失败: 城市无效, activity_place=" + activity_place);
			return rs;
		}
		
		// 4. 校验活动类型
		if (!isValidActivityType(activity_type)) {
			rs.setData("请选择正确的活动类型");
			System.out.println("  验证失败: 活动类型无效, activity_type=" + activity_type);
			return rs;
		}
		
		// 5. 校验活动人数
		if (activity_people == null || activity_people.trim().isEmpty()) {
			rs.setData("活动人数不能为空");
			System.out.println("  验证失败: 活动人数为空");
			return rs;
		}
		
		// 6. 校验活动预算
		if (activity_budget == null || activity_budget.trim().isEmpty()) {
			rs.setData("活动预算不能为空");
			System.out.println("  验证失败: 活动预算为空");
			return rs;
		}
		
		// 7. 校验时间
		if (start_time == null || start_time.trim().isEmpty()) {
			rs.setData("起始时间不能为空");
			System.out.println("  验证失败: 起始时间为空");
			return rs;
		}
		if (end_time == null || end_time.trim().isEmpty()) {
			rs.setData("结束时间不能为空");
			System.out.println("  验证失败: 结束时间为空");
			return rs;
		}
		if (!isValidDate(start_time)) {
			rs.setData("起始时间格式不正确，请使用正确的日期格式");
			return rs;
		}
		if (!isValidDate(end_time)) {
			rs.setData("结束时间格式不正确，请使用正确的日期格式");
			return rs;
		}
		if (!isEndTimeAfterStartTime(start_time, end_time)) {
			rs.setData("结束时间必须晚于起始时间");
			return rs;
		}
		
		// 6. 校验用户是否存在，并检查name和phone是否需要更新
		String sql = "SELECT * FROM `user` WHERE userId = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[] {userId});
		if(list == null || list.size() == 0) {
			rs.setData("用户不存在");
			return rs;
		}
		
		// 获取用户当前信息
		HashMap<String, Object> user = list.get(0);
		String dbPhone = user.get("phone") != null ? (String) user.get("phone") : "";
		String dbName = user.get("name") != null ? (String) user.get("name") : "";
		
		// 如果用户表中phone为空，则更新phone
		if (dbPhone == null || dbPhone.trim().isEmpty()) {
			sql = "UPDATE `user` SET phone = ? WHERE userId = ?";
			MySqlHelper.executeUpdate(sql, new Object[] {phone, userId});
		}
		
		// 如果用户表中name为空，则更新name
		if (dbName == null || dbName.trim().isEmpty()) {
			sql = "UPDATE `user` SET name = ? WHERE userId = ?";
			MySqlHelper.executeUpdate(sql, new Object[] {name, userId});
		}
		
		// 7. 插入活动申请
		sql = "INSERT INTO activity_demand (userId, activity_place, activity_type, start_time, end_time, activity_people, activity_budget, activity_progress, requirement_desc, principal_userid) " +
	              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
		int temp = MySqlHelper.executeUpdate(sql, new Object[] {userId, activity_place, activity_type, start_time, end_time, activity_people, activity_budget, activity_progress, requirement_desc});
		if(temp > 0) {
			rs.setData("提交活动申请成功！");
			rs.setFlag("success");
		} else {
			rs.setData("提交活动申请失败，请重试");
		}
		return rs;
	}
	
	// 校验城市是否有效
	private static boolean isValidCity(String city) {
		if (city == null || city.trim().isEmpty()) return false;
		for (String validCity : VALID_CITIES) {
			if (validCity.equals(city.trim())) {
				return true;
			}
		}
		return false;
	}
	
	// 校验活动类型是否有效
	private static boolean isValidActivityType(String type) {
		if (type == null || type.trim().isEmpty()) return false;
		for (String validType : VALID_ACTIVITY_TYPES) {
			if (validType.equals(type.trim())) {
				return true;
			}
		}
		return false;
	}
	
	// 校验日期格式是否有效
	private static boolean isValidDate(String dateStr) {
		if (dateStr == null || dateStr.trim().isEmpty()) return false;
		return dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$");
	}
	
	// 校验结束时间是否晚于起始时间
	private static boolean isEndTimeAfterStartTime(String startTime, String endTime) {
		if (startTime == null || endTime == null) return false;
		return endTime.compareTo(startTime) >= 0;
	}
	
	// 查看所有活动申请信息
	public static Result showAllActivityDemand() {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT *FROM `activity_demand` a LEFT JOIN `user` u ON a.userid = u.userId LEFT JOIN `employee` e ON a.principal_userid = e.userId";
		System.out.println("执行SQL: " + sql);
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, null);
		System.out.println("查询结果: " + (list == null ? "null" : list.size() + "条记录"));
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到活动申请记录");
		}
		return rs;
	}
	
	// 根据用户ID查看活动申请信息 - 返回所有状态的活动
	public static Result showActivityDemandByUserId(String userId) {
		Result rs = new Result(null, null, "fail");
		// 查询该用户的所有活动，不限制状态
		// 注意：数据库中activity_demand表的字段是userid(小写i)，user表的是userId(大写I)
		String sql = "SELECT a.demand_id, a.userid, a.activity_place, a.activity_type, a.start_time, a.end_time, a.activity_people, a.activity_budget, a.activity_progress, a.requirement_desc, a.refuse_reason, a.principal_userid, a.total_activity_price, u.username, u.phone, u.sex, u.name AS user_name, e.name AS principal_name FROM `activity_demand` a LEFT JOIN `user` u ON a.userid = u.userId LEFT JOIN `employee` e ON a.principal_userid = e.userId WHERE a.userid = ? ORDER BY a.demand_id DESC";
		System.out.println("showActivityDemandByUserId查询，userId=" + userId);
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId});
		System.out.println("查询结果数量: " + (list != null ? list.size() : "null"));
		if(list != null && list.size() > 0) {
			// 为每条记录添加状态文本
			for(HashMap<String, Object> item : list) {
				int progress = Integer.parseInt(item.get("activity_progress").toString());
				String statusText = getActivityStatusText(progress);
				item.put("statusText", statusText);
			}
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到该用户的活动申请记录");
		}
		return rs;
	}
	
	// 获取活动状态文本
	private static String getActivityStatusText(int activityProgress) {
		switch(activityProgress) {
			case -2: return "仓库管理员拒绝";
			case -1: return "业务管理员拒绝";
			case 0: return "待处理";
			case 1: return "业务管理员通过";
			case 2: return "仓库管理员通过";
			case 3: return "人员安排完成";
			case 4: return "用户已支付定金";
			case 5: return "用户已支付尾款";
			default: return "未知状态";
		}
	}
	
	// 根据活动ID查看活动申请信息
	public static Result showActivityDemandById(String demandId) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT a.demand_id, a.userid, a.activity_place, a.activity_type, a.start_time, a.end_time, a.activity_people, a.activity_budget, a.activity_progress, a.requirement_desc, a.refuse_reason, a.principal_userid, u.username, u.phone, u.sex, u.name AS user_name, e.name AS principal_name FROM `activity_demand` a LEFT JOIN `user` u ON a.userid = u.userId LEFT JOIN `employee` e ON a.principal_userid = e.userId WHERE a.demand_id = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{demandId});
		if(list != null && list.size() > 0) {
			rs.setData(list.get(0));
			rs.setFlag("success");
		} else {
			rs.setData("没有找到该活动申请记录");
		}
		return rs;
	}
	
	// 更新活动进度
	public static Result updateActivityProgress(String demandId, String activityProgress, String refuseReason, String principalUserId) {
		Result rs = new Result(null, null, "fail");
		// 检查活动是否存在
		String sql = "SELECT * FROM `activity_demand` WHERE demand_id = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{demandId});
		if(list == null || list.size() == 0) {
			rs.setData("没有找到该活动申请");
			return rs;
		}
		
		// 验证activityProgress是否为数字
		int progress;
		try {
			progress = Integer.parseInt(activityProgress);
		} catch (NumberFormatException e) {
			rs.setData("活动进度必须是数字");
			return rs;
		}
		
		// 根据活动进度值决定是否更新拒绝理由
		if(progress < 0) {
			// 拒绝状态，更新活动进度、拒绝理由和负责人ID
			sql = "UPDATE `activity_demand` SET activity_progress = ?, refuse_reason = ?, principal_userid = ? WHERE demand_id = ?";
			int updateResult = MySqlHelper.executeUpdate(sql, new Object[]{activityProgress, refuseReason, principalUserId, demandId});
			if(updateResult > 0) {
				// 更新成功后返回包含用户信息和管理员名字的完整活动申请记录
				sql = "SELECT a.*, u.username, u.phone, u.sex, u.name AS user_name, e.name AS principal_name FROM `activity_demand` a LEFT JOIN `user` u ON a.userid = u.userId LEFT JOIN `employee` e ON a.principal_userid = e.userId WHERE a.demand_id = ?";
				list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{demandId});
				if(list != null && list.size() > 0) {
					rs.setFlag("success");
					rs.setData(list.get(0));
				} else {
					rs.setFlag("success");
					rs.setData("活动进度更新成功");
				}
			} else {
				rs.setData("活动进度更新失败");
			}
		} else {
			// 非拒绝状态，更新活动进度和负责人ID
			sql = "UPDATE `activity_demand` SET activity_progress = ?, principal_userid = ? WHERE demand_id = ?";
			int updateResult = MySqlHelper.executeUpdate(sql, new Object[]{activityProgress, principalUserId, demandId});
			if(updateResult > 0) {
				// 更新成功后返回包含用户信息和管理员名字的完整活动申请记录
				sql = "SELECT a.*, u.username, u.phone, u.sex, u.name AS user_name, e.name AS principal_name FROM `activity_demand` a LEFT JOIN `user` u ON a.userid = u.userId LEFT JOIN `employee` e ON a.principal_userid = e.userId WHERE a.demand_id = ?";
				list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{demandId});
				if(list != null && list.size() > 0) {
					rs.setFlag("success");
					rs.setData(list.get(0));
				} else {
					rs.setFlag("success");
					rs.setData("活动进度更新成功");
				}
			} else {
				rs.setData("活动进度更新失败");
			}
		}
		return rs;
	}
	
	// 用户取消活动 - 将状态改为-3
	public static Result cancelActivity(String demandId) {
		Result rs = new Result(null, null, "fail");
		// 检查活动是否存在
		String sql = "SELECT * FROM `activity_demand` WHERE demand_id = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{demandId});
		if(list == null || list.size() == 0) {
			rs.setData("没有找到该活动申请");
			return rs;
		}
		
		// 获取当前状态
		int currentProgress = Integer.parseInt(list.get(0).get("activity_progress").toString());
		// 只有状态0-3可以取消
		if(currentProgress < 0 || currentProgress > 3) {
			rs.setData("当前状态不允许取消活动");
			return rs;
		}
		
		// 更新状态为-3（用户取消）
		sql = "UPDATE `activity_demand` SET activity_progress = -3 WHERE demand_id = ?";
		int updateResult = MySqlHelper.executeUpdate(sql, new Object[]{demandId});
		if(updateResult > 0) {
			rs.setFlag("success");
			rs.setData("活动已取消");
		} else {
			rs.setData("取消活动失败");
		}
		return rs;
	}
	
	// 用户支付定金 - 将状态从3改为4
	public static Result payActivityDeposit(String demandId) {
		Result rs = new Result(null, null, "fail");
		// 检查活动是否存在
		String sql = "SELECT * FROM `activity_demand` WHERE demand_id = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{demandId});
		if(list == null || list.size() == 0) {
			rs.setData("没有找到该活动申请");
			return rs;
		}
		
		// 获取当前状态
		int currentProgress = Integer.parseInt(list.get(0).get("activity_progress").toString());
		// 只有状态3可以支付定金
		if(currentProgress != 3) {
			rs.setData("当前状态不允许支付定金");
			return rs;
		}
		
		// 更新状态为4（用户已支付定金）
		sql = "UPDATE `activity_demand` SET activity_progress = 4 WHERE demand_id = ?";
		int updateResult = MySqlHelper.executeUpdate(sql, new Object[]{demandId});
		if(updateResult > 0) {
			rs.setFlag("success");
			rs.setData("定金支付成功");
		} else {
			rs.setData("支付定金失败");
		}
		return rs;
	}
}