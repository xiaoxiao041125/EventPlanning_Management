@echo off
chcp 65001 >nul
echo ==========================================
echo活动策划管理平台 - 完整接口测试
echo ==========================================
echo.

set COLLECTION=event_planning_collection.json
set DELAY=100

echo [1/26] 登录接口测试...
call newman run %COLLECTION% --folder "用户管理/登录接口" -d login_test_data.json --delay-request %DELAY%

echo.
echo [2/26] 用户注册接口测试...
call newman run %COLLECTION% --folder "用户管理/注册接口（用户）" -d register_user_test_data.json --delay-request %DELAY%

echo.
echo [3/26] 员工注册接口测试...
call newman run %COLLECTION% --folder "用户管理/注册接口（员工）" -d register_employee_test_data.json --delay-request %DELAY%

echo.
echo [4/26] 修改密码接口测试...
call newman run %COLLECTION% --folder "用户管理/修改密码接口" -d change_password_test_data.json --delay-request %DELAY%

echo.
echo [5/26] 获取所有用户接口测试...
call newman run %COLLECTION% --folder "用户管理/获取所有用户接口" --delay-request %DELAY%

echo.
echo [6/26] 根据 ID 获取用户接口测试...
call newman run %COLLECTION% --folder "用户管理/根据 ID 获取用户接口" -d get_user_by_id_test_data.json --delay-request %DELAY%

echo.
echo [7/26] 申请活动接口测试...
call newman run %COLLECTION% --folder "活动管理/申请活动接口" -d apply_activity_test_data.json --delay-request %DELAY%

echo.
echo [8/26] 取消活动接口测试...
call newman run %COLLECTION% --folder "活动管理/取消活动接口" -d cancel_activity_test_data.json --delay-request %DELAY%

echo.
echo [9/26] 更新活动进度接口测试...
call newman run %COLLECTION% --folder "活动管理/更新活动进度接口" -d update_activity_progress_test_data.json --delay-request %DELAY%

echo.
echo [10/26] 获取所有活动需求接口测试...
call newman run %COLLECTION% --folder "活动管理/获取所有活动需求接口" --delay-request %DELAY%

echo.
echo [11/26] 根据 ID 获取活动需求接口测试...
call newman run %COLLECTION% --folder "活动管理/根据 ID 获取活动需求接口" -d get_activity_by_id_test_data.json --delay-request %DELAY%

echo.
echo [12/26] 根据用户 ID 获取活动需求接口测试...
call newman run %COLLECTION% --folder "活动管理/根据用户 ID 获取活动需求接口" -d get_activity_by_user_test_data.json --delay-request %DELAY%

echo.
echo [13/26] 处理员工注册接口测试...
call newman run %COLLECTION% --folder "员工管理/处理员工注册接口" -d handle_employee_register_test_data.json --delay-request %DELAY%

echo.
echo [14/26] 获取所有员工接口测试...
call newman run %COLLECTION% --folder "员工管理/获取所有员工接口" --delay-request %DELAY%

echo.
echo [15/26] 计算薪资接口测试...
call newman run %COLLECTION% --folder "薪资管理/计算薪资接口" -d calculate_salary_test_data.json --delay-request %DELAY%

echo.
echo [16/26] 发放薪资接口测试...
call newman run %COLLECTION% --folder "薪资管理/发放薪资接口" -d pay_salary_test_data.json --delay-request %DELAY%

echo.
echo [17/26] 获取我的薪资接口测试...
call newman run %COLLECTION% --folder "薪资管理/获取我的薪资接口" -d get_my_salary_test_data.json --delay-request %DELAY%

echo.
echo [18/26] 获取所有薪资记录接口测试...
call newman run %COLLECTION% --folder "薪资管理/获取所有薪资记录接口" --delay-request %DELAY%

echo.
echo [19/26] 添加物料接口测试...
call newman run %COLLECTION% --folder "物料管理/添加物料接口" -d add_material_test_data.json --delay-request %DELAY%

echo.
echo [20/26] 物料出库接口测试...
call newman run %COLLECTION% --folder "物料管理/物料出库接口" -d material_outbound_test_data.json --delay-request %DELAY%

echo.
echo [21/26] 物料租赁接口测试...
call newman run %COLLECTION% --folder "物料管理/物料租赁接口" -d material_rent_test_data.json --delay-request %DELAY%

echo.
echo [22/26] 获取仓库物料接口测试...
call newman run %COLLECTION% --folder "物料管理/获取仓库物料接口" --delay-request %DELAY%

echo.
echo [23/26] 添加报销申请接口测试...
call newman run %COLLECTION% --folder "报销管理/添加报销申请接口" -d add_reimbursement_test_data.json --delay-request %DELAY%

echo.
echo [24/26] 审批报销申请接口测试...
call newman run %COLLECTION% --folder "报销管理/审批报销申请接口" -d approve_reimbursement_test_data.json --delay-request %DELAY%

echo.
echo [25/26] 获取所有报销申请接口测试...
call newman run %COLLECTION% --folder "报销管理/获取所有报销申请接口" --delay-request %DELAY%

echo.
echo [26/26] 根据用户 ID 获取报销申请接口测试...
call newman run %COLLECTION% --folder "报销管理/根据用户 ID 获取报销申请接口" -d get_reimbursement_by_user_test_data.json --delay-request %DELAY%

echo.
echo ==========================================
echo 所有接口测试执行完成！
echo ==========================================
pause
