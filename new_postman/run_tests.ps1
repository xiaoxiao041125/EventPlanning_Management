# 活动策划管理平台 - 接口测试脚本
# 使用 Newman 执行测试并生成 HTML 报告

$ErrorActionPreference = "Continue"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Event Planning Management - API Tests" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

$Collection = "event_planning_collection.json"
$Delay = 100
$ReportFile = "report.html"

Write-Host "Running all interface tests..." -ForegroundColor Yellow
Write-Host "Collection: $Collection" -ForegroundColor Gray
Write-Host "Generating report: $ReportFile" -ForegroundColor Gray
Write-Host ""

# 运行 Newman 测试
newman run $Collection --delay-request $Delay --reporters cli,html --reporter-html-export $ReportFile

$exitCode = $LASTEXITCODE

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Tests Completed!" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# 检查报告是否生成
if (Test-Path $ReportFile) {
    Write-Host "HTML Report generated: $ReportFile" -ForegroundColor Green
    Write-Host "Opening report in browser..." -ForegroundColor Yellow
    Start-Process $ReportFile
} else {
    Write-Host "Warning: Report file was not generated." -ForegroundColor Red
}

Write-Host ""
if ($exitCode -eq 0) {
    Write-Host "All tests passed!" -ForegroundColor Green
} else {
    Write-Host "Some tests failed. Exit code: $exitCode" -ForegroundColor Red
}

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
