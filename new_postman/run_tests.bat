@echo off
chcp 65001 >nul
echo ==========================================
echo 活动策划管理平台 - 接口测试
echo ==========================================
echo.
echo 正在执行所有接口测试...
echo.

REM 执行测试并生成 HTML 报告
newman run event_planning_collection.json --delay-request 100 --reporters cli,html --reporter-html-export report.html

echo.
echo ==========================================
echo 测试完成！
echo ==========================================
echo.
echo HTML 报告已生成：report.html
echo.
echo 按任意键查看报告或关闭窗口...
pause >nul
start report.html
exit
