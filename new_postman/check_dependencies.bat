@echo on

REM 检查系统环境
echo 检查系统环境...
echo 当前目录: %cd%
echo PATH 环境变量:
echo %PATH%

echo.
echo 检查 Node.js 是否安装...
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到 Node.js，请先安装 Node.js
    pause
    exit /b 1
) else (
    echo Node.js 已安装
    node --version
)

echo.
echo 检查 npm 是否安装...
where npm >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到 npm，请先安装 npm
    pause
    exit /b 1
) else (
    echo npm 已安装
    npm --version
)

echo.
echo 检查 newman 是否安装...
where newman >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到 newman，请先安装 newman
    echo 安装命令: npm install -g newman newman-reporter-allure
    pause
    exit /b 1
) else (
    echo newman 已安装
    newman --version
)

echo.
echo 检查 allure 是否安装...
where allure >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未找到 allure，请先安装 allure
    echo 安装方法: 从 https://github.com/allure-framework/allure2/releases 下载并添加到环境变量
    pause
    exit /b 1
) else (
    echo allure 已安装
    allure --version
)

echo.
echo 检查文件是否存在...
echo 检查 event_planning_collection.json: %~dp0event_planning_collection.json
if exist "%~dp0event_planning_collection.json" (
    echo event_planning_collection.json 存在
) else (
    echo 错误: event_planning_collection.json 不存在
    pause
    exit /b 1
)

echo 检查 login_test_data.json: %~dp0login_test_data.json
if exist "%~dp0login_test_data.json" (
    echo login_test_data.json 存在
) else (
    echo 错误: login_test_data.json 不存在
    pause
    exit /b 1
)

echo 检查 apply_activity_test_data.json: %~dp0apply_activity_test_data.json
if exist "%~dp0apply_activity_test_data.json" (
    echo apply_activity_test_data.json 存在
) else (
    echo 错误: apply_activity_test_data.json 不存在
    pause
    exit /b 1
)

echo 检查 register_test_data.json: %~dp0register_test_data.json
if exist "%~dp0register_test_data.json" (
    echo register_test_data.json 存在
) else (
    echo 错误: register_test_data.json 不存在
    pause
    exit /b 1
)

echo.
echo 所有依赖检查通过！
pause

REM 防止脚本自动关闭
cmd /k