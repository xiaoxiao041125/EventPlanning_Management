@echo off
chcp 65001 >nul
echo 正在清除旧的 Allure 结果...
if exist reports\allure-results (
    rmdir /s /q reports\allure-results
)
mkdir reports\allure-results

echo 正在运行测试...
pytest tests/test_login.py -v --alluredir=reports/allure-results

echo 正在生成 Allure 报告...
allure serve reports/allure-results
