"""
数据库连接和测试数据生成脚本
用于获取数据库中的实际数据来生成 Postman 测试用例
"""

import pymysql
import json
from datetime import datetime

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'eventplanning_management',
    'charset': 'utf8mb4'
}

def get_connection():
    """获取数据库连接"""
    return pymysql.connect(**DB_CONFIG)

def get_users():
    """获取用户数据"""
    conn = get_connection()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    cursor.execute("SELECT userID, username, name, roleId, sex, phone FROM user LIMIT 10")
    users = cursor.fetchall()
    cursor.close()
    conn.close()
    return users

def get_activities():
    """获取活动数据"""
    conn = get_connection()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    cursor.execute("SELECT demand_id, userID, activity_place, activity_type, start_time, end_time, activity_progress FROM activity_demand LIMIT 10")
    activities = cursor.fetchall()
    cursor.close()
    conn.close()
    return activities

def get_employees():
    """获取员工数据"""
    conn = get_connection()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    try:
        cursor.execute("SELECT employee_id, userId, post, city FROM employee LIMIT 10")
        employees = cursor.fetchall()
    except:
        # 如果字段不存在，尝试其他字段名
        cursor.execute("SELECT employee_id, userID, post, city FROM employee LIMIT 10")
        employees = cursor.fetchall()
    cursor.close()
    conn.close()
    return employees

def get_materials():
    """获取物料数据"""
    conn = get_connection()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    try:
        cursor.execute("SELECT material_id, category, material_type, quantity, price FROM warehouse_material LIMIT 10")
        materials = cursor.fetchall()
        cursor.close()
        conn.close()
        return materials
    except:
        cursor.close()
        conn.close()
        return []

def get_salary_records():
    """获取薪资记录"""
    conn = get_connection()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    try:
        cursor.execute("SELECT record_id, userId, year, month FROM salary_record LIMIT 10")
        records = cursor.fetchall()
        cursor.close()
        conn.close()
        return records
    except:
        cursor.close()
        conn.close()
        return []

def get_reimbursements():
    """获取报销记录"""
    conn = get_connection()
    cursor = conn.cursor(pymysql.cursors.DictCursor)
    try:
        cursor.execute("SELECT reimbursement_id, userId, reimbursement_type, status FROM reimbursement_application LIMIT 10")
        records = cursor.fetchall()
        cursor.close()
        conn.close()
        return records
    except:
        cursor.close()
        conn.close()
        return []

def generate_test_data():
    """生成测试数据"""
    print("正在连接数据库获取测试数据...")
    
    test_cases = []
    
    # 获取用户数据
    users = get_users()
    if users:
        # 用户登录测试 - 成功
        test_cases.append({
            "case_id": "LOGIN_001",
            "case_name": "用户登录成功",
            "endpoint": "login",
            "username": users[0]['username'],
            "password": "123",  # 默认密码
            "roleId": str(users[0]['roleId']),
            "expected_flag": "success",
            "should_have_data": True
        })
        
        # 用户登录测试 - 密码错误
        test_cases.append({
            "case_id": "LOGIN_002",
            "case_name": "用户登录失败 - 密码错误",
            "endpoint": "login",
            "username": users[0]['username'],
            "password": "wrongpassword",
            "roleId": str(users[0]['roleId']),
            "expected_flag": "fail",
            "should_have_data": True
        })
        
        # 根据 ID 查询用户
        test_cases.append({
            "case_id": "USER_001",
            "case_name": "根据 ID 查询用户",
            "endpoint": "showUserByID",
            "userId": str(users[0].get('userID', users[0].get('userId'))),
            "expected_flag": "success",
            "should_have_data": True
        })
    
    # 查询所有用户
    test_cases.append({
        "case_id": "USER_002",
        "case_name": "查询所有用户",
        "endpoint": "ShowAllUser",
        "expected_flag": "success",
        "should_have_data": True
    })
    
    # 获取活动数据
    activities = get_activities()
    if activities:
        # 根据 ID 查询活动
        test_cases.append({
            "case_id": "ACTIVITY_001",
            "case_name": "根据活动 ID 查询活动",
            "endpoint": "ShowActivityDemandById",
            "demandId": str(activities[0]['demand_id']),
            "expected_flag": "success",
            "should_have_data": True
        })
        
        # 根据用户 ID 查询活动
        test_cases.append({
            "case_id": "ACTIVITY_002",
            "case_name": "根据用户 ID 查询活动",
            "endpoint": "ShowActivityDemandByUserId",
            "userId": str(activities[0].get('userID', activities[0].get('userId'))),
            "expected_flag": "success",
            "should_have_data": True
        })
    
    # 查询所有活动
    test_cases.append({
        "case_id": "ACTIVITY_003",
        "case_name": "查询所有活动需求",
        "endpoint": "ShowAllActivityDemand",
        "expected_flag": "success",
        "should_have_data": True
    })
    
    # 获取员工数据
    employees = get_employees()
    if employees:
        # 根据 ID 查询员工
        test_cases.append({
            "case_id": "EMPLOYEE_001",
            "case_name": "根据 ID 查询员工",
            "endpoint": "ShowEmployeeById",
            "employeeId": str(employees[0]['employee_id']),
            "expected_flag": "success",
            "should_have_data": True
        })
        
        # 根据用户 ID 查询员工
        test_cases.append({
            "case_id": "EMPLOYEE_002",
            "case_name": "根据用户 ID 查询员工",
            "endpoint": "SearchEmployeeByUserId",
            "userId": str(employees[0].get('userId', employees[0].get('userID'))),
            "expected_flag": "success",
            "should_have_data": True
        })
    
    # 查询所有员工
    test_cases.append({
        "case_id": "EMPLOYEE_003",
        "case_name": "查询所有员工",
        "endpoint": "ShowEmployee",
        "city": "",
        "expected_flag": "success",
        "should_have_data": True
    })
    
    # 获取物料数据
    materials = get_materials()
    if materials:
        # 获取所有物料
        test_cases.append({
            "case_id": "MATERIAL_001",
            "case_name": "获取所有物料",
            "endpoint": "GetWarehouseMaterials",
            "expected_flag": "success",
            "should_have_data": True
        })
        
        # 根据分类获取物料
        test_cases.append({
            "case_id": "MATERIAL_002",
            "case_name": "根据分类获取物料",
            "endpoint": "GetMaterialsByCategory",
            "category": str(materials[0]['category']),
            "expected_flag": "success",
            "should_have_data": True
        })
    
    # 获取薪资记录
    salary_records = get_salary_records()
    if salary_records:
        # 获取所有薪资记录
        test_cases.append({
            "case_id": "SALARY_001",
            "case_name": "获取所有薪资记录",
            "endpoint": "GetAllSalaryRecords",
            "year": str(salary_records[0]['year']),
            "month": str(salary_records[0]['month']),
            "expected_flag": "success",
            "should_have_data": True
        })
    
    # 获取报销记录
    reimbursements = get_reimbursements()
    if reimbursements:
        # 获取所有报销申请
        test_cases.append({
            "case_id": "REIMBURSE_001",
            "case_name": "获取所有报销申请",
            "endpoint": "GetAllReimbursementApplications",
            "status": "",
            "expected_flag": "success",
            "should_have_data": True
        })
        
        # 根据用户 ID 获取报销申请
        test_cases.append({
            "case_id": "REIMBURSE_002",
            "case_name": "根据用户 ID 获取报销申请",
            "endpoint": "GetReimbursementApplicationsByUserId",
            "userId": str(reimbursements[0].get('userId', reimbursements[0].get('userID'))),
            "expected_flag": "success",
            "should_have_data": True
        })
    
    # 添加一些失败的测试用例
    test_cases.append({
        "case_id": "FAIL_001",
        "case_name": "查询不存在的用户",
        "endpoint": "showUserByID",
        "userId": "99999",
        "expected_flag": "fail",
        "should_have_data": True
    })
    
    test_cases.append({
        "case_id": "FAIL_002",
        "case_name": "查询不存在的活动",
        "endpoint": "ShowActivityDemandById",
        "demandId": "99999",
        "expected_flag": "fail",
        "should_have_data": True
    })
    
    return test_cases

def save_test_cases(test_cases, filename):
    """保存测试用例到 JSON 文件"""
    with open(filename, 'w', encoding='utf-8') as f:
        json.dump(test_cases, f, ensure_ascii=False, indent=2)
    print(f"测试用例已保存到：{filename}")
    print(f"共生成 {len(test_cases)} 个测试用例")

if __name__ == "__main__":
    try:
        test_cases = generate_test_data()
        save_test_cases(test_cases, "test_data_db.json")
        print("\n✅ 测试数据生成成功！")
    except Exception as e:
        print(f"\n❌ 生成测试数据失败：{str(e)}")
        print("请检查数据库连接配置")
