@echo off
chcp 65001
echo ==========================================
echo 活动策划管理平台 - 完整接口测试
echo ==========================================
echo.

set COLLECTION=event_planning_collection.json
set DELAY=100

echo [1/20] 登录接口测试...
newman run %COLLECTION% --folder "用户管理/登录接口" -d login_test_data.json --delay-request %DELAY%

echo.
echo [2/20] 用户注册接口测试...
newman run %COLLECTION% --folder "用户管理/用户注册接口" -d register_user_test_data.json --delay-request %DELAY%

echo.
echo [3/20] 根据ID查询用户接口测试...
newman run %COLLECTION% --folder "用户管理/根据ID查询用户接口" -d get_user_by_id_test_data.json --delay-request %DELAY%

echo.
echo [4/20] 修改密码接口测试...
newman run %COLLECTION% --folder "用户管理/修改密码接口" -d change_password_test_data.json --delay-request %DELAY%

echo.
echo [5/20] 申请活动接口测试...
newman run %COLLECTION% --folder "活动管理/申请活动接口" -d apply_activity_test_data.json --delay-request %DELAY%

echo.
echo [6/20] 取消活动接口测试...
newman run %COLLECTION% --folder "活动管理/取消活动接口" -d cancel_activity_test_data.json --delay-request %DELAY%

echo.
echo [7/20] 根据ID查询活动需求接口测试...
newman run %COLLECTION% --folder "活动管理/根据ID查询活动需求接口" -d get_activity_by_id_test_data.json --delay-request %DELAY%

echo.
echo [8/20] 根据用户ID查询活动接口测试...
newman run %COLLECTION% --folder "活动管理/根据用户ID查询活动接口" -d get_activity_by_user_test_data.json --delay-request %DELAY%

echo.
echo [9/20] 更新活动接口测试...
newman run %COLLECTION% --folder "活动管理/更新活动接口" -d update_activity_test_data.json --delay-request %DELAY%

echo.
echo [10/20] 员工注册接口测试...
newman run %COLLECTION% --folder "员工管理/员工注册接口" -d register_employee_test_data.json --delay-request %DELAY%

echo.
echo [11/20] 处理员工注册接口测试...
newman run %COLLECTION% --folder "员工管理/处理员工注册接口" -d handle_employee_register_test_data.json --delay-request %DELAY%

echo.
echo [12/20] 计算薪资接口测试...
newman run %COLLECTION% --folder "薪资管理/计算薪资接口" -d calculate_salary_test_data.json --delay-request %DELAY%

echo.
echo [13/20] 发放薪资接口测试...
newman run %COLLECTION% --folder "薪资管理/发放薪资接口" -d pay_salary_test_data.json --delay-request %DELAY%

echo.
echo [14/20] 获取我的薪资接口测试...
newman run %COLLECTION% --folder "薪资管理/获取我的薪资接口" -d get_my_salary_test_data.json --delay-request %DELAY%

echo.
echo [15/20] 添加物料接口测试...
newman run %COLLECTION% --folder "物料管理/添加物料接口" -d add_material_test_data.json --delay-request %DELAY%

echo.
echo [16/20] 物料出库接口测试...
newman run %COLLECTION% --folder "物料管理/物料出库接口" -d material_outbound_test_data.json --delay-request %DELAY%

echo.
echo [17/20] 物料租赁接口测试...
newman run %COLLECTION% --folder "物料管理/物料租赁接口" -d material_rent_test_data.json --delay-request %DELAY%

echo.
echo [18/20] 添加报销申请接口测试...
newman run %COLLECTION% --folder "报销管理/添加报销申请接口" -d add_reimbursement_test_data.json --delay-request %DELAY%

echo.
echo [19/20] 审批报销申请接口测试...
newman run %COLLECTION% --folder "报销管理/审批报销申请接口" -d approve_reimbursement_test_data.json --delay-request %DELAY%

echo.
echo [20/20] 根据用户ID获取报销申请接口测试...
newman run %COLLECTION% --folder "报销管理/根据用户ID获取报销申请接口" -d get_reimbursement_by_user_test_data.json --delay-request %DELAY%

echo.
echo ==========================================
echo 所有接口测试执行完成！
echo ==========================================
pause