# 活动策划管理平台 - 完整接口测试脚本
# 使用 Newman 执行 Postman 参数化测试

$ErrorActionPreference = "Stop"
$Collection = "event_planning_collection.json"
$Delay = 100

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "活动策划管理平台 - 完整接口测试" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# 运行整个集合，使用所有参数化数据文件
Write-Host "提示：由于中文文件夹名称编码问题，建议使用以下命令手动运行：" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. 运行整个集合（不使用 -d 参数）:" -ForegroundColor Green
Write-Host "   newman run $Collection --delay-request $Delay"
Write-Host ""
Write-Host "2. 或者使用英文文件夹路径运行特定模块:" -ForegroundColor Green
Write-Host "   newman run $Collection --folder '用户管理/登录接口' -d login_test_data.json --delay-request $Delay"
Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "开始执行测试..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# 尝试运行整个集合（不指定文件夹，让集合自己处理）
Write-Host "执行完整集合测试..." -ForegroundColor Yellow
try {
    newman run $Collection --delay-request $Delay
} catch {
    Write-Host "错误：$_" -ForegroundColor Red
    Write-Host ""
    Write-Host "请尝试手动运行以下命令：" -ForegroundColor Yellow
    Write-Host "newman run $Collection --delay-request $Delay" -ForegroundColor White
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "测试执行完成！" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
