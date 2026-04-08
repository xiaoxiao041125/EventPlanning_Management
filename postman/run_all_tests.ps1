# Postman 接口测试自动执行脚本
# 功能：连接数据库生成测试数据，运行测试，生成中文报告

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  活动策划公司管理系统 - 接口测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 设置工作目录
Set-Location $PSScriptRoot

# 检查 Newman 是否安装
Write-Host "检查 Newman 环境..." -ForegroundColor Yellow
$newmanCheck = newman --version 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "Newman 已安装：v$newmanCheck" -ForegroundColor Green
} else {
    Write-Host "Newman 未安装，请先运行：npm install -g newman" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  步骤 1: 生成测试数据" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 尝试运行 Python 脚本生成测试数据
if (Test-Path "generate_test_data.py") {
    Write-Host "正在连接数据库生成测试数据..." -ForegroundColor Yellow
    python generate_test_data.py
    if (Test-Path "test_data_db.json") {
        Write-Host "测试数据生成成功" -ForegroundColor Green
        $testDataFile = "test_data_db.json"
    } else {
        Write-Host "测试数据生成失败，使用默认数据" -ForegroundColor Yellow
        $testDataFile = "test_data_comprehensive.json"
    }
} else {
    Write-Host "使用默认测试数据" -ForegroundColor Yellow
    $testDataFile = "test_data_comprehensive.json"
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  步骤 2: 运行接口测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 确保 reports 目录存在
if (-not (Test-Path "../reports")) {
    New-Item -ItemType Directory -Path "../reports" | Out-Null
    Write-Host "创建 reports 目录" -ForegroundColor Yellow
}

# 运行测试
Write-Host "正在执行测试用例..." -ForegroundColor Yellow
Write-Host ""

# 运行 Newman 测试并捕获输出
$testOutput = newman run postman_collection_db.json -d $testDataFile -r cli 2>&1

# 显示输出
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试结果" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 统计测试结果
$totalTests = 0
$passedTests = 0
$failedTests = 0

foreach ($line in $testOutput) {
    if ($line -match "AssertionError") {
        $failedTests++
    }
    if ($line -match "OK") {
        $passedTests++
    }
    $totalTests++
}

# 计算通过率
if ($totalTests -gt 0) {
    $passRate = [math]::Round(($passedTests / $totalTests) * 100, 2)
} else {
    $passRate = 0
}

# 显示摘要
Write-Host ""
Write-Host "测试执行摘要：" -ForegroundColor Yellow
Write-Host "  总测试数：$totalTests" -ForegroundColor White
Write-Host "  通过：$passedTests" -ForegroundColor Green
Write-Host "  失败：$failedTests" -ForegroundColor Red
Write-Host "  通过率：$passRate%" -ForegroundColor Cyan
Write-Host ""

# 保存中文报告
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$reportFile = "../reports/test_report_$timestamp.txt"

$reportContent = @()
$reportContent += "========================================"
$reportContent += "  活动策划公司管理系统 - 接口测试报告"
$reportContent += "========================================"
$reportContent += ""
$reportContent += "测试时间：" + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
$reportContent += "测试集合：postman_collection_db.json"
$reportContent += "测试数据：$testDataFile"
$reportContent += ""
$reportContent += "测试执行摘要："
$reportContent += "  总测试数：$totalTests"
$reportContent += "  通过：$passedTests"
$reportContent += "  失败：$failedTests"
$reportContent += "  通过率：$passRate%"
$reportContent += ""
$reportContent += "详细测试结果："
$reportContent += "----------------------------------------"
foreach ($line in $testOutput) {
    $reportContent += $line
}

$reportContent | Out-File -FilePath $reportFile -Encoding UTF8
Write-Host "测试报告已保存：$reportFile" -ForegroundColor Green

# 同时生成 HTML 报告
Write-Host ""
Write-Host "正在生成 HTML 报告..." -ForegroundColor Yellow
$htmlReport = "../reports/test_report_$timestamp.html"
newman run postman_collection_db.json -d $testDataFile -r htmlextra --reporter-htmlextra-export $htmlReport 2>&1 | Out-Null
Write-Host "HTML 报告已生成：$htmlReport" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "查看报告：" -ForegroundColor Yellow
Write-Host "  文本报告：$reportFile"
Write-Host "  HTML 报告：$htmlReport"
Write-Host ""
