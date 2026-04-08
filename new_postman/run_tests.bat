@echo on

REM 检查是否安装了 newman
echo 检查 newman 是否安装...
where newman >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到 newman，请先安装 newman
    echo 安装命令: npm install -g newman newman-reporter-allure
    pause
    exit /b 1
) else (
    echo newman 已安装
)

REM 检查是否安装了 allure
echo 检查 allure 是否安装...
where allure >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到 allure，请先安装 allure
    echo 安装方法: 从 https://github.com/allure-framework/allure2/releases 下载并添加到环境变量
    pause
    exit /b 1
) else (
    echo allure 已安装
)

REM 创建报告目录
echo 创建报告目录...
mkdir reports 2>nul

REM 运行登录接口测试
echo 运行登录接口测试...
newman run event_planning_collection.json --folder "用户管理" --environment "" --iteration-data login_test_data.json --reporters cli,allure --reporter-allure-export reports/login
if %errorlevel% neq 0 (
    echo 登录接口测试失败
    pause
    exit /b 1
)

REM 运行用户注册接口测试
echo 运行用户注册接口测试...
newman run event_planning_collection.json --folder "用户管理" --environment "" --iteration-data register_user_test_data.json --reporters cli,allure --reporter-allure-export reports/register_user
if %errorlevel% neq 0 (
    echo 用户注册接口测试失败
    pause
    exit /b 1
)

REM 运行员工注册接口测试
echo 运行员工注册接口测试...
newman run event_planning_collection.json --folder "用户管理" --environment "" --iteration-data register_employee_test_data.json --reporters cli,allure --reporter-allure-export reports/register_employee
if %errorlevel% neq 0 (
    echo 员工注册接口测试失败
    pause
    exit /b 1
)

REM 运行修改密码接口测试
echo 运行修改密码接口测试...
newman run event_planning_collection.json --folder "用户管理" --environment "" --iteration-data change_password_test_data.json --reporters cli,allure --reporter-allure-export reports/change_password
if %errorlevel% neq 0 (
    echo 修改密码接口测试失败
    pause
    exit /b 1
)

REM 运行根据ID获取用户接口测试
echo 运行根据ID获取用户接口测试...
newman run event_planning_collection.json --folder "用户管理" --environment "" --iteration-data get_user_by_id_test_data.json --reporters cli,allure --reporter-allure-export reports/get_user_by_id
if %errorlevel% neq 0 (
    echo 根据ID获取用户接口测试失败
    pause
    exit /b 1
)

REM 运行根据ID获取活动需求接口测试
echo 运行根据ID获取活动需求接口测试...
newman run event_planning_collection.json --folder "活动管理" --environment "" --iteration-data get_activity_by_id_test_data.json --reporters cli,allure --reporter-allure-export reports/get_activity_by_id
if %errorlevel% neq 0 (
    echo 根据ID获取活动需求接口测试失败
    pause
    exit /b 1
)

REM 运行根据用户ID获取活动需求接口测试
echo 运行根据用户ID获取活动需求接口测试...
newman run event_planning_collection.json --folder "活动管理" --environment "" --iteration-data get_activity_by_user_test_data.json --reporters cli,allure --reporter-allure-export reports/get_activity_by_user
if %errorlevel% neq 0 (
    echo 根据用户ID获取活动需求接口测试失败
    pause
    exit /b 1
)

REM 运行申请活动接口测试
echo 运行申请活动接口测试...
newman run event_planning_collection.json --folder "活动管理" --environment "" --iteration-data apply_activity_test_data.json --reporters cli,allure --reporter-allure-export reports/apply_activity
if %errorlevel% neq 0 (
    echo 申请活动接口测试失败
    pause
    exit /b 1
)

REM 运行其他接口测试（无参数化）
echo 运行其他接口测试...
newman run event_planning_collection.json --reporters cli,allure --reporter-allure-export reports/other
if %errorlevel% neq 0 (
    echo 其他接口测试失败
    pause
    exit /b 1
)

REM 清理测试数据
echo 清理测试数据...
mysql -u root -p123456 eventplanning_management < cleanup_test_data.sql
echo 测试数据清理完成！

REM 生成 Allure 报告
echo 生成 Allure 报告...
allure generate reports --clean -o allure-report
if %errorlevel% neq 0 (
    echo 生成 Allure 报告失败
    pause
    exit /b 1
)

REM 打开 Allure 报告
echo 打开 Allure 报告...
start allure open allure-report

echo 测试完成！
pause

REM 防止脚本自动关闭
cmd /k
