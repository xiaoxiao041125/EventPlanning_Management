# Postman 测试报告生成脚本
# 生成中文测试摘要报告

param(
    [string]$CollectionFile = "postman_collection_full.json",
    [string]$DataFile = "test_data_comprehensive.json",
    [string]$OutputDir = "../reports"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Postman 接口测试 - 中文报告" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 运行 Newman 测试并捕获输出
$testOutput = newman run $CollectionFile -d $DataFile -r cli 2>&1

# 统计测试结果
$totalTests = 0
$passedTests = 0
$failedTests = 0

# 解析输出
foreach ($line in $testOutput) {
    if ($line -match "AssertionError") {
        $failedTests++
    }
    if ($line -match "✓") {
        $passedTests++
    }
}

# 显示摘要
Write-Host "测试执行摘要" -ForegroundColor Yellow
Write-Host "----------------------------------------" -ForegroundColor Gray
Write-Host "总测试用例数：$totalTests" -ForegroundColor White
Write-Host "通过：$passedTests" -ForegroundColor Green
Write-Host "失败：$failedTests" -ForegroundColor Red

if ($totalTests -gt 0) {
    $passRate = [math]::Round(($passedTests / $totalTests) * 100, 2)
    Write-Host "通过率：$passRate%" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "详细报告已生成在：$OutputDir/test_report.html" -ForegroundColor Cyan
Write-Host ""

# 导出失败详情到文本文件
$failedDetails = @()
foreach ($line in $testOutput) {
    if ($line -match "AssertionError") {
        $failedDetails += $line
    }
}

if ($failedDetails.Count -gt 0) {
    $failedDetails | Out-File -FilePath "$OutputDir/failed_tests.txt" -Encoding UTF8
    Write-Host "失败详情已保存到：$OutputDir/failed_tests.txt" -ForegroundColor Yellow
}
