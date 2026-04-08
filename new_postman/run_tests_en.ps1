# 活动策划管理平台 - 完整接口测试脚本
# 使用 Newman 执行 Postman 参数化测试

$ErrorActionPreference = "Continue"
$Collection = "event_planning_collection.json"
$Delay = 100

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Event Planning Management - API Tests" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Starting tests..." -ForegroundColor Yellow
Write-Host ""

# 定义接口与数据文件的映射关系（使用英文路径避免编码问题）
$testCases = @(
    @{Folder="User Management/Login"; DataFile="login_test_data.json"},
    @{Folder="User Management/Register (User)"; DataFile="register_user_test_data.json"},
    @{Folder="User Management/Register (Employee)"; DataFile="register_employee_test_data.json"},
    @{Folder="User Management/Change Password"; DataFile="change_password_test_data.json"},
    @{Folder="User Management/Get All Users"; DataFile=""},
    @{Folder="User Management/Get User By ID"; DataFile="get_user_by_id_test_data.json"},
    @{Folder="Activity Management/Apply Activity"; DataFile="apply_activity_test_data.json"},
    @{Folder="Activity Management/Cancel Activity"; DataFile="cancel_activity_test_data.json"},
    @{Folder="Activity Management/Update Activity Progress"; DataFile="update_activity_progress_test_data.json"},
    @{Folder="Activity Management/Get All Activity Demands"; DataFile=""},
    @{Folder="Activity Management/Get Activity Demand By ID"; DataFile="get_activity_by_id_test_data.json"},
    @{Folder="Activity Management/Get Activity Demand By User ID"; DataFile="get_activity_by_user_test_data.json"},
    @{Folder="Employee Management/Handle Employee Register"; DataFile="handle_employee_register_test_data.json"},
    @{Folder="Employee Management/Get All Employees"; DataFile=""},
    @{Folder="Salary Management/Calculate Salary"; DataFile="calculate_salary_test_data.json"},
    @{Folder="Salary Management/Pay Salary"; DataFile="pay_salary_test_data.json"},
    @{Folder="Salary Management/Get My Salary"; DataFile="get_my_salary_test_data.json"},
    @{Folder="Salary Management/Get All Salary Records"; DataFile=""},
    @{Folder="Material Management/Add Material"; DataFile="add_material_test_data.json"},
    @{Folder="Material Management/Material Outbound"; DataFile="material_outbound_test_data.json"},
    @{Folder="Material Management/Material Rent"; DataFile="material_rent_test_data.json"},
    @{Folder="Material Management/Get Warehouse Materials"; DataFile=""},
    @{Folder="Reimbursement Management/Add Reimbursement Application"; DataFile="add_reimbursement_test_data.json"},
    @{Folder="Reimbursement Management/Approve Reimbursement Application"; DataFile="approve_reimbursement_test_data.json"},
    @{Folder="Reimbursement Management/Get All Reimbursement Applications"; DataFile=""},
    @{Folder="Reimbursement Management/Get Reimbursement Applications By User ID"; DataFile="get_reimbursement_by_user_test_data.json"}
)

$totalTests = $testCases.Count
$passedTests = 0
$failedTests = 0

Write-Host "Total: $totalTests interfaces to test" -ForegroundColor Yellow
Write-Host ""

# 遍历每个测试用例
for ($i = 0; $i -lt $totalTests; $i++) {
    $testCase = $testCases[$i]
    $folder = $testCase.Folder
    $dataFile = $testCase.DataFile
    $testNum = $i + 1
    
    # 提取接口名称用于显示
    $interfaceName = $folder.Split('/')[-1]
    
    Write-Host "[$testNum/$totalTests] Testing: $interfaceName" -ForegroundColor Cyan
    
    if ($dataFile -eq "") {
        # 无参数化文件的接口
        Write-Host "  -> Running without data file..." -ForegroundColor Gray
        newman run $Collection --folder $folder --delay-request $Delay --reporters cli
    } else {
        # 有参数化文件的接口
        if (Test-Path $dataFile) {
            Write-Host "  -> Using data file: $dataFile" -ForegroundColor Gray
            newman run $Collection --folder $folder -d $dataFile --delay-request $Delay --reporters cli
        } else {
            Write-Host "  -> Warning: Data file not found - $dataFile" -ForegroundColor Yellow
            $failedTests++
            continue
        }
    }
    
    # 检查结果
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [PASS] Test passed" -ForegroundColor Green
        $passedTests++
    } else {
        Write-Host "  [FAIL] Test failed" -ForegroundColor Red
        $failedTests++
    }
    
    Write-Host ""
}

# 输出统计
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Test Summary" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Total Tests: $totalTests" -ForegroundColor White
Write-Host "Passed: $passedTests" -ForegroundColor Green
Write-Host "Failed: $failedTests" -ForegroundColor Red
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

if ($failedTests -eq 0) {
    Write-Host "All tests passed!" -ForegroundColor Green
} else {
    Write-Host "Some tests failed. Please check the error messages above." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
