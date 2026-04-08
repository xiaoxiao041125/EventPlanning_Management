package edu.wtbu.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.wtbu.helper.MySqlHelper;
import edu.wtbu.pojo.Result;

public class EmployeeDao {
	public static Result showAllEmployee() {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city, u.start_day FROM `employee` e LEFT JOIN `user` u ON e.userId = u.userId";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, null);
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到员工记录");
		}
		return rs;
	}
	
	public static Result showEmployeeById(String employeeId) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city, u.start_day FROM `employee` e LEFT JOIN `user` u ON e.userId = u.userId WHERE e.employee_id = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{employeeId});
		if(list != null && list.size() > 0) {
			rs.setData(list.get(0));
			rs.setFlag("success");
		} else {
			rs.setData("没有找到该员工记录");
		}
		return rs;
	}
	
	public static Result showEmployeeByPost(String post) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city, u.start_day FROM `employee` e LEFT JOIN `user` u ON e.userId = u.userId WHERE e.post = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{post});
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到该职位的员工记录");
		}
		return rs;
	}
	
	public static Result searchEmployeeById(String employeeId) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city, u.start_day FROM `employee` e LEFT JOIN `user` u ON e.userId = u.userId WHERE e.employee_id LIKE ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{"%" + employeeId + "%"});
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到匹配的员工记录");
		}
		return rs;
	}
	
	public static Result searchEmployeeByName(String name) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city, u.start_day FROM `employee` e LEFT JOIN `user` u ON e.userId = u.userId WHERE e.name LIKE ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{"%" + name + "%"});
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到匹配的员工记录");
		}
		return rs;
	}
	
	public static Result searchEmployeeByUserId(String userId) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city, u.start_day FROM `employee` e LEFT JOIN `user` u ON e.userId = u.userId WHERE e.userId LIKE ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{"%" + userId + "%"});
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到匹配的员工记录");
		}
		return rs;
	}
	
	public static Result showEmployeeByStatus(String status) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city, u.start_day, ew.status as work_status FROM `employee` e LEFT JOIN `user` u ON e.userId = u.userId LEFT JOIN `employee_work` ew ON e.userId = ew.userid WHERE ew.status = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{status});
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到该状态的员工记录");
		}
		return rs;
	}
	
	public static Result searchEmployee(String status, String city, String keyword) {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city, u.start_day FROM `employee` e LEFT JOIN `user` u ON e.userId = u.userId WHERE 1=1";
		
		// 构建参数数组
		Object[] params = null;
		
		// 添加城市条件（使用LIKE操作符，匹配带有空格的城市名称）
		if(city != null && !"" .equals(city) && !"0".equals(city)) {
			sql += " AND e.city LIKE ?";
			params = new Object[]{"%" + city + "%"};
		}
		
		// 添加关键词条件（智能判断：数字则查询工号，否则查询姓名）
		if(keyword != null && !"" .equals(keyword)) {
			if(keyword.matches("\\d+")) {
				// 数字：查询工号
				sql += " AND e.employee_id LIKE ?";
				if(params == null) {
					params = new Object[]{"%" + keyword + "%"};
				} else {
					// 扩展参数数组
					Object[] newParams = new Object[params.length + 1];
					System.arraycopy(params, 0, newParams, 0, params.length);
					newParams[params.length] = "%" + keyword + "%";
					params = newParams;
				}
			} else {
				// 非数字：查询姓名
				sql += " AND e.name LIKE ?";
				if(params == null) {
					params = new Object[]{"%" + keyword + "%"};
				} else {
					// 扩展参数数组
					Object[] newParams = new Object[params.length + 1];
					System.arraycopy(params, 0, newParams, 0, params.length);
					newParams[params.length] = "%" + keyword + "%";
					params = newParams;
				}
			}
		}
		
		// 执行查询
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, params);
		
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到匹配的员工记录");
		}
		
		return rs;
	}
	
	public static Result updateEmployeePost(String employeeId, String post) {
		Result rs = new Result(null, null, "fail");
		// 检查员工是否存在
		String sql = "SELECT * FROM `employee` WHERE employee_id = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{employeeId});
		if(list == null || list.size() == 0) {
			rs.setData("没有找到该员工");
			return rs;
		}
		
		// 更新员工职位
		sql = "UPDATE `employee` SET post = ? WHERE employee_id = ?";
		int updateResult = MySqlHelper.executeUpdate(sql, new Object[]{post, employeeId});
		if(updateResult > 0) {
			// 更新成功后返回包含创建时间和城市的完整员工信息
			sql = "SELECT e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city, u.start_day FROM `employee` e LEFT JOIN `user` u ON e.userId = u.userId WHERE e.employee_id = ?";
			list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{employeeId});
			if(list != null && list.size() > 0) {
				rs.setFlag("success");
				rs.setData(list.get(0));
			} else {
				rs.setFlag("success");
				rs.setData("员工职位修改成功");
			}
		} else {
			rs.setData("员工职位修改失败");
		}
		return rs;
	}

	public static Result register(String username, String password, String employeeName, String phone, String sex, String post, String city) {
		Result rs = new Result(null, null, "fail");
		// 检查user表中是否已存在该用户名
		String userSql = "SELECT * FROM `user` WHERE username = ?";
		List<HashMap<String, Object>> userList = MySqlHelper.executeQueryReturnMap(userSql, new Object[]{username});
		if(userList != null && userList.size() > 0) {
			rs.setData("用户名已存在");
			return rs;
		}
		// 检查employee_register表中是否已存在该用户名
		String registerSql = "SELECT * FROM `employee_register` WHERE username = ?";
		List<HashMap<String, Object>> registerList = MySqlHelper.executeQueryReturnMap(registerSql, new Object[]{username});
		if(registerList != null && registerList.size() > 0) {
			rs.setData("用户名已存在");
		} else {
			String insertSql = "INSERT INTO employee_register (username, password, employee_name, phone, sex, post, city, register_time, status) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 0)";
			int temp = MySqlHelper.executeUpdate(insertSql, new Object[]{username, password, employeeName, phone, sex, post, city});
			if(temp > 0) {
				rs.setFlag("success");
				rs.setData("注册成功，等待审核");
			} else {
				rs.setData("注册失败");
			}
		}
		return rs;
	}
	
	public static Result showAllEmployeeRegister() {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT register_id, username, employee_name, phone, sex, post, city, register_time, status FROM `employee_register`";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, null);
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到员工注册申请");
		}
		return rs;
	}
	
	public static Result handleEmployeeRegister(String registerId, String status, String post) {
		Result rs = new Result(null, null, "fail");
		// 1. 根据registerId获取注册申请信息
		String sql = "SELECT username, password, employee_name, phone, sex, post, city, register_time FROM `employee_register` WHERE register_id = ?";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{registerId});
		if(list == null || list.size() == 0) {
			rs.setData("没有找到该注册申请");
			return rs;
		}
		
		HashMap<String, Object> registerInfo = list.get(0);
		String username = registerInfo.get("username").toString();
		String password = registerInfo.get("password").toString();
		String employeeName = registerInfo.get("employee_name") != null ? registerInfo.get("employee_name").toString() : "";
		String phone = registerInfo.get("phone") != null ? registerInfo.get("phone").toString() : "";
		String sex = registerInfo.get("sex") != null ? registerInfo.get("sex").toString() : "";
		String registerPost = registerInfo.get("post") != null ? registerInfo.get("post").toString() : "";
		String city = registerInfo.get("city") != null ? registerInfo.get("city").toString() : "";
		String registerTime = registerInfo.get("register_time") != null ? registerInfo.get("register_time").toString() : "";
		
		// 2. 更新注册申请状态
		sql = "UPDATE `employee_register` SET status = ? WHERE register_id = ?";
		int updateResult = MySqlHelper.executeUpdate(sql, new Object[]{status, registerId});
		if(updateResult <= 0) {
			rs.setData("更新注册申请状态失败");
			return rs;
		}
		
		// 3. 如果是通过申请，添加到用户表和员工表
		if("1".equals(status)) {
			// 添加到用户表，角色ID为1（普通员工）
			sql = "INSERT INTO `user` (username, password, roleId, phone, sex, name, start_day) VALUES (?, ?, 1, ?, ?, ?, ?)";
			int userId = MySqlHelper.executeUpdateAndReturnId(sql, new Object[]{username, password, phone, sex, employeeName, registerTime});
			if(userId > 0) {
				// 添加到员工表，关联userId和post
				sql = "INSERT INTO `employee` (userId, username, post, phone, sex, name, city) VALUES (?, ?, ?, ?, ?, ?, ?)";
				int employeeResult = MySqlHelper.executeUpdate(sql, new Object[]{userId, username, registerPost, phone, sex, employeeName, city});
				if(employeeResult > 0) {
					// 如果是工人，添加到员工工作表
					if("工人".equals(registerPost)) {
						sql = "INSERT INTO `employee_work` (userid, work_days, status) VALUES (?, 0, 1)";
						MySqlHelper.executeUpdate(sql, new Object[]{userId});
					}
					rs.setFlag("success");
					rs.setData("注册申请通过，已添加到用户表和员工表");
				} else {
					rs.setData("注册申请通过，但添加到员工表失败");
				}
			} else {
				rs.setData("注册申请通过，但添加到用户表失败");
			}
		} else {
			// 拒绝申请，只更新状态
				rs.setFlag("success");
				rs.setData("注册申请已拒绝");
			}
		
		return rs;
	}
	
	// 查看所有工人
	public static Result showEmployeeWork() {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT employee_id, userid, username, post, phone, sex, name, city FROM `employee` WHERE post = '工人'";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, null);
		if(list != null && list.size() > 0) {
			rs.setData(list);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到工人记录");
		}
		return rs;
	}
	
	// 查看员工工作表中状态为1的员工数量
	public static Result countEmployeeWorkByStatusOne() {
		Result rs = new Result(null, null, "fail");
		String sql = "SELECT COUNT(*) as count FROM `employee_work` WHERE status = 1";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, null);
		if(list != null && list.size() > 0) {
			HashMap<String, Object> result = list.get(0);
			rs.setData(result.get("count"));
			rs.setFlag("success");
		} else {
			rs.setData(0);
		}
		return rs;
	}
	
	// 查看用户是否有工作要做
	public static Result checkUserWorkByUserId(String userId) {
		Result rs = new Result(null, null, "fail");
		try {
			// 直接查询employee_work表，使用正确的字段名
			String sql = "SELECT userld, work_start_time, work_end_time, demand_id FROM employee_work WHERE userld = ?";
			List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId});
			if(list != null && list.size() > 0) {
				// 找到了工作记录，连接活动需求表
				String sqlWithJoin = "SELECT ew.userld, ew.work_start_time, ew.work_end_time, ew.demand_id, ad.activity_place, ad.activity_type, ad.start_time, ad.end_time, ad.activity_people, ad.activity_budget FROM employee_work ew LEFT JOIN activity_demand ad ON ew.demand_id = ad.demand_id WHERE ew.userld = ?";
				List<HashMap<String, Object>> joinedList = MySqlHelper.executeQueryReturnMap(sqlWithJoin, new Object[]{userId});
				rs.setData(joinedList);
				rs.setFlag("success");
			} else {
				rs.setData("没有找到该用户的工作记录");
			}
		} catch (Exception e) {
			rs.setData("错误: " + e.getMessage());
		}
		return rs;
	}
	
	// 根据时间范围查询员工工作状态
	public static Result searchEmployeeByTimeRange(String startTime, String endTime) {
		Result rs = new Result(null, null, "fail");
		// 先查询所有员工
		String employeeSql = "SELECT employee_id, userid, username, post, phone, sex, name, city FROM `employee` WHERE post = '工人'";
		List<HashMap<String, Object>> employeeList = MySqlHelper.executeQueryReturnMap(employeeSql, null);
		
		if(employeeList != null && employeeList.size() > 0) {
			// 遍历每个员工，检查是否在时间范围内有工作安排
			for(HashMap<String, Object> employee : employeeList) {
				int userId = (Integer) employee.get("userid");
				// 查询该员工在时间范围内的工作记录
				String workSql = "SELECT * FROM `employee_work` WHERE userid = ? AND ((work_start_time BETWEEN ? AND ?) OR (work_end_time BETWEEN ? AND ?) OR (work_start_time <= ? AND work_end_time >= ?))";
				List<HashMap<String, Object>> workList = MySqlHelper.executeQueryReturnMap(workSql, new Object[]{userId, startTime, endTime, startTime, endTime, startTime, endTime});
				if(workList != null && workList.size() > 0) {
					// 有工作安排，状态为0（忙碌）
					employee.put("status", 0);
					employee.put("busy", true);
				} else {
					// 无工作安排，状态为1（空闲）
					employee.put("status", 1);
					employee.put("busy", false);
				}
			}
			rs.setData(employeeList);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到员工记录");
		}
		return rs;
	}
	
	// 组合查询员工（时间范围、城市、关键词）
	public static Result searchEmployeeCombined(String startTime, String endTime, String city, String keyword) {
		Result rs = new Result(null, null, "fail");
		// 构建基础SQL
		String sql = "SELECT e.employee_id, e.userid, e.username, e.post, e.phone, e.sex, e.name, e.city FROM `employee` e WHERE e.post = '工人'";
		List<Object> params = new ArrayList<>();
		
		// 添加城市条件
		if(city != null && !"" .equals(city) && !"0".equals(city)) {
			sql += " AND e.city = ?";
			params.add(city);
		}
		
		// 添加关键词条件（智能判断：数字则查询工号，否则查询姓名）
		if(keyword != null && !"" .equals(keyword)) {
			if(keyword.matches("\\d+")) {
				// 数字：查询工号
				sql += " AND e.employee_id LIKE ?";
				params.add("%" + keyword + "%");
			} else {
				// 非数字：查询姓名
				sql += " AND e.name LIKE ?";
				params.add("%" + keyword + "%");
			}
		}
		
		// 执行查询
		List<HashMap<String, Object>> employeeList = MySqlHelper.executeQueryReturnMap(sql, params.toArray());
		
		if(employeeList != null && employeeList.size() > 0) {
			// 遍历每个员工，检查是否在时间范围内有工作安排
			for(HashMap<String, Object> employee : employeeList) {
				int userId = (Integer) employee.get("userid");
				// 查询该员工在时间范围内的工作记录
				String workSql = "SELECT * FROM `employee_work` WHERE userid = ? AND ((work_start_time BETWEEN ? AND ?) OR (work_end_time BETWEEN ? AND ?) OR (work_start_time <= ? AND work_end_time >= ?))";
				List<HashMap<String, Object>> workList = MySqlHelper.executeQueryReturnMap(workSql, new Object[]{userId, startTime, endTime, startTime, endTime, startTime, endTime});
				if(workList != null && workList.size() > 0) {
					// 有工作安排，状态为0（忙碌）
					employee.put("status", 0);
					employee.put("busy", true);
				} else {
					// 无工作安排，状态为1（空闲）
					employee.put("status", 1);
					employee.put("busy", false);
				}
			}
			rs.setData(employeeList);
			rs.setFlag("success");
		} else {
			rs.setData("没有找到符合条件的员工记录");
		}
		return rs;
	}
	
	// 通过userid查询工人的工作记录（包含活动需求信息）
	public static Result checkWorkerWorkByUserId(String userId) {
		Result rs = new Result(null, null, "fail");
		// 先测试employee_work表的基本查询
		String sql = "SELECT * FROM `employee_work`";
		List<HashMap<String, Object>> workList = MySqlHelper.executeQueryReturnMap(sql, null);
		
		if(workList != null && workList.size() > 0) {
			// 找到了记录，现在根据userId过滤
			List<HashMap<String, Object>> filteredList = new ArrayList<>();
			for(HashMap<String, Object> record : workList) {
				// 检查userld字段
				if(record.containsKey("userld")) {
					Object userldValue = record.get("userld");
					if(userldValue != null) {
						String userldStr = userldValue.toString();
						if(userldStr.equals(userId)) {
							filteredList.add(record);
						}
					}
				}
			}
			
			if(filteredList.size() > 0) {
				rs.setData(filteredList);
				rs.setFlag("success");
			} else {
				rs.setData("找到了employee_work表记录，但没有匹配的userId: " + userId);
			}
		} else {
			// 没有找到记录，测试employee表
			sql = "SELECT * FROM `employee`";
			List<HashMap<String, Object>> employeeList = MySqlHelper.executeQueryReturnMap(sql, null);
			if(employeeList != null && employeeList.size() > 0) {
				rs.setData("找到了employee表记录，但employee_work表为空");
			} else {
				rs.setData("employee_work和employee表都为空");
			}
		}
		return rs;
	}
	
	// 计算本月工作天数
	public static Result calculateMonthlyWorkDays(String userId) {
		Result rs = new Result(null, null, "fail");
		// 获取当前年份和月份
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		int year = calendar.get(java.util.Calendar.YEAR);
		int month = calendar.get(java.util.Calendar.MONTH) + 1; // 月份从0开始，所以加1
		
		// 构建本月的开始和结束时间
		String monthStart = year + "-" + (month < 10 ? "0" + month : month) + "-01 00:00:00";
		String monthEnd = year + "-" + (month < 10 ? "0" + month : month) + "-" + calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH) + " 23:59:59";
		
		// 查询本月的工作记录
		String sql = "SELECT work_start_time, work_end_time FROM `employee_work` WHERE userid = ? AND ((work_start_time BETWEEN ? AND ?) OR (work_end_time BETWEEN ? AND ?) OR (work_start_time <= ? AND work_end_time >= ?))";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId, monthStart, monthEnd, monthStart, monthEnd, monthStart, monthEnd});
		
		if(list != null && list.size() > 0) {
			int totalDays = 0;
			// 获取今天的日期，设置为 23:59:59
			java.util.Calendar todayCalendar = java.util.Calendar.getInstance();
			todayCalendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
			todayCalendar.set(java.util.Calendar.MINUTE, 59);
			todayCalendar.set(java.util.Calendar.SECOND, 59);
			todayCalendar.set(java.util.Calendar.MILLISECOND, 999);
			java.util.Date today = todayCalendar.getTime();
			
			for(HashMap<String, Object> record : list) {
				Object startTimeObj = record.get("work_start_time");
				Object endTimeObj = record.get("work_end_time");
				if(startTimeObj != null && endTimeObj != null) {
					try {
						// 处理字符串格式的时间
						String startTimeStr = startTimeObj.toString();
						String endTimeStr = endTimeObj.toString();
						
						// 将字符串转换为Date
						java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						java.util.Date startDate = sdf.parse(startTimeStr);
						java.util.Date endDate = sdf.parse(endTimeStr);
						
						// 根据条件计算工作天数
						if(startDate.after(today)) {
							// 如果开始时间大于今天的时间，则不计算这段时间
							continue;
						} else if(endDate.after(today)) {
							// 如果结束时间大于今天的时间，而开始时间小于今天的时间，则用今天的时间 - 开始时间 + 1
							long diff = today.getTime() - startDate.getTime();
							int days = (int) (diff / (1000 * 60 * 60 * 24)) + 1; // +1是因为当天也算一天
							totalDays += days;
						} else {
							// 如果结束时间小于今天时间，那就把这段时间加到总时间中
							long diff = endDate.getTime() - startDate.getTime();
							int days = (int) (diff / (1000 * 60 * 60 * 24)) + 1; // +1是因为当天也算一天
							totalDays += days;
						}
					} catch (Exception e) {
						// 解析失败，跳过这条记录
						continue;
					}
				}
			}
			rs.setData(totalDays);
			rs.setFlag("success");
		} else {
			rs.setData(0);
			rs.setFlag("success");
		}
		return rs;
	}
	
	// 统计已完成的工作项数（结束时间小于今天）
	public static Result countCompletedWorkItems(String userId) {
		Result rs = new Result(null, null, "fail");
		// 获取当前年份和月份
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		int year = calendar.get(java.util.Calendar.YEAR);
		int month = calendar.get(java.util.Calendar.MONTH) + 1; // 月份从0开始，所以加1
		
		// 构建本月的开始和结束时间
		String monthStart = year + "-" + (month < 10 ? "0" + month : month) + "-01 00:00:00";
		String monthEnd = year + "-" + (month < 10 ? "0" + month : month) + "-" + calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH) + " 23:59:59";
		
		// 查询本月的工作记录
		String sql = "SELECT work_end_time FROM `employee_work` WHERE userid = ? AND ((work_start_time BETWEEN ? AND ?) OR (work_end_time BETWEEN ? AND ?) OR (work_start_time <= ? AND work_end_time >= ?))";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId, monthStart, monthEnd, monthStart, monthEnd, monthStart, monthEnd});
		
		if(list != null && list.size() > 0) {
			int completedCount = 0;
			// 获取今天的日期，设置为 00:00:00
			java.util.Calendar todayCalendar = java.util.Calendar.getInstance();
			todayCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
			todayCalendar.set(java.util.Calendar.MINUTE, 0);
			todayCalendar.set(java.util.Calendar.SECOND, 0);
			todayCalendar.set(java.util.Calendar.MILLISECOND, 0);
			java.util.Date today = todayCalendar.getTime();
			
			for(HashMap<String, Object> record : list) {
				Object endTimeObj = record.get("work_end_time");
				if(endTimeObj != null) {
					try {
						// 处理字符串格式的时间
						String endTimeStr = endTimeObj.toString();
						
						// 将字符串转换为Date
						java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						java.util.Date endDate = sdf.parse(endTimeStr);
						
						// 结束时间小于今天的表示已完成的
						if(endDate.before(today)) {
							completedCount++;
						}
					} catch (Exception e) {
						// 解析失败，跳过这条记录
						continue;
					}
				}
			}
			rs.setData(completedCount);
			rs.setFlag("success");
		} else {
			rs.setData(0);
			rs.setFlag("success");
		}
		return rs;
	}
	
	// 统计待完成的工作项数（结束时间大于等于今天）
	public static Result countPendingWorkItems(String userId) {
		Result rs = new Result(null, null, "fail");
		// 获取当前年份和月份
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		int year = calendar.get(java.util.Calendar.YEAR);
		int month = calendar.get(java.util.Calendar.MONTH) + 1; // 月份从0开始，所以加1
		
		// 构建本月的开始和结束时间
		String monthStart = year + "-" + (month < 10 ? "0" + month : month) + "-01 00:00:00";
		String monthEnd = year + "-" + (month < 10 ? "0" + month : month) + "-" + calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH) + " 23:59:59";
		
		// 查询本月的工作记录
		String sql = "SELECT work_end_time FROM `employee_work` WHERE userid = ? AND ((work_start_time BETWEEN ? AND ?) OR (work_end_time BETWEEN ? AND ?) OR (work_start_time <= ? AND work_end_time >= ?))";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(sql, new Object[]{userId, monthStart, monthEnd, monthStart, monthEnd, monthStart, monthEnd});
		
		if(list != null && list.size() > 0) {
			int pendingCount = 0;
			// 获取今天的日期，设置为 00:00:00
			java.util.Calendar todayCalendar = java.util.Calendar.getInstance();
			todayCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
			todayCalendar.set(java.util.Calendar.MINUTE, 0);
			todayCalendar.set(java.util.Calendar.SECOND, 0);
			todayCalendar.set(java.util.Calendar.MILLISECOND, 0);
			java.util.Date today = todayCalendar.getTime();
			
			for(HashMap<String, Object> record : list) {
				Object endTimeObj = record.get("work_end_time");
				if(endTimeObj != null) {
					try {
						// 处理字符串格式的时间
						String endTimeStr = endTimeObj.toString();
						
						// 将字符串转换为Date
						java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						java.util.Date endDate = sdf.parse(endTimeStr);
						
						// 结束时间大于等于今天的表示待完成的
						if(!endDate.before(today)) {
							pendingCount++;
						}
					} catch (Exception e) {
						// 解析失败，跳过这条记录
						continue;
					}
				}
			}
			rs.setData(pendingCount);
			rs.setFlag("success");
		} else {
			rs.setData(0);
			rs.setFlag("success");
		}
		return rs;
	}
	
	// 获取工作统计信息（工作天数、已完成工作项数、待完成工作项数）
	public static Result getWorkStatistics(String userId) {
		Result rs = new Result(null, null, "fail");
		try {
			// 获取工作天数
			Result daysResult = calculateMonthlyWorkDays(userId);
			int workDays = (Integer) daysResult.getData();
			
			// 获取已完成工作项数
			Result completedResult = countCompletedWorkItems(userId);
			int completedItems = (Integer) completedResult.getData();
			
			// 获取待完成工作项数
			Result pendingResult = countPendingWorkItems(userId);
			int pendingItems = (Integer) pendingResult.getData();
			
			// 构建结果
			HashMap<String, Object> statistics = new HashMap<>();
			statistics.put("workDays", workDays);
			statistics.put("completedItems", completedItems);
			statistics.put("pendingItems", pendingItems);
			
			rs.setData(statistics);
			rs.setFlag("success");
		} catch (Exception e) {
			rs.setData("错误: " + e.getMessage());
		}
		return rs;
	}
	
	// 修改员工工作状态
	public static Result updateEmployeeWorkStatus(String userId, String workStartTime, String workEndTime, String demandId) {
		Result rs = new Result(null, null, "fail");
		// 检查员工工作记录是否存在
		String checkSql = "SELECT * FROM `employee` WHERE userid = ? and post = \"工人\"";
		List<HashMap<String, Object>> list = MySqlHelper.executeQueryReturnMap(checkSql, new Object[]{userId});
		if(list == null || list.size() == 0) {
			rs.setData("没有找到该员工的工作记录");
			return rs;
		}
		
		// 更新员工工作状态、时间和活动ID
		String updateSql = "INSERT INTO `employee_work` (work_start_time, work_end_time, demand_id, userid) \r\n" + 
				"VALUES (?, ?, ?, ?);";
		int result = MySqlHelper.executeUpdate(updateSql, new Object[]{workStartTime, workEndTime, demandId, userId});
		if(result > 0) {
			// 更新成功后返回更新后的工作记录
			String querySql = "SELECT ew.userid, ew.work_days, ew.work_start_time, ew.work_end_time, ew.demand_id, e.employee_id, e.username, e.post, e.phone, e.sex, e.name, e.city FROM `employee_work` ew LEFT JOIN `employee` e ON ew.userid = e.userId WHERE ew.userid = ?";
			List<HashMap<String, Object>> updatedList = MySqlHelper.executeQueryReturnMap(querySql, new Object[]{userId});
			if(updatedList != null && updatedList.size() > 0) {
				rs.setData(updatedList.get(0));
			}
			rs.setFlag("success");
		} else {
			rs.setData("更新员工工作状态失败");
		}
		return rs;
	}
}
