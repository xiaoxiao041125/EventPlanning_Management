# 活动策划管理平台 - 完整接口测试脚本
# 使用 Newman 执行 Postman 参数化测试

$ErrorActionPreference = "Continue"
$Collection = "event_planning_collection.json"
$Delay = 100

# 防止闪退：脚本开始时暂停
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "活动策划管理平台 - 完整接口测试" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "准备开始测试..." -ForegroundColor Yellow
Write-Host "按任意键开始执行测试..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
Write-Host ""

Write-Host "共发现 26 个接口测试..." -ForegroundColor Yellow
Write-Host ""

# 定义接口与数据文件的映射关系
$testCases = @(
    @{Folder="用户管理/登录接口"; DataFile="login_test_data.json"},
    @{Folder="用户管理/注册接口（用户）"; DataFile="register_user_test_data.json"},
    @{Folder="用户管理/注册接口（员工）"; DataFile="register_employee_test_data.json"},
    @{Folder="用户管理/修改密码接口"; DataFile="change_password_test_data.json"},
    @{Folder="用户管理/获取所有用户接口"; DataFile=""},
    @{Folder="用户管理/根据 ID 获取用户接口"; DataFile="get_user_by_id_test_data.json"},
    @{Folder="活动管理/申请活动接口"; DataFile="apply_activity_test_data.json"},
    @{Folder="活动管理/取消活动接口"; DataFile="cancel_activity_test_data.json"},
    @{Folder="活动管理/更新活动进度接口"; DataFile="update_activity_progress_test_data.json"},
    @{Folder="活动管理/获取所有活动需求接口"; DataFile=""},
    @{Folder="活动管理/根据 ID 获取活动需求接口"; DataFile="get_activity_by_id_test_data.json"},
    @{Folder="活动管理/根据用户 ID 获取活动需求接口"; DataFile="get_activity_by_user_test_data.json"},
    @{Folder="员工管理/处理员工注册接口"; DataFile="handle_employee_register_test_data.json"},
    @{Folder="员工管理/获取所有员工接口"; DataFile=""},
    @{Folder="薪资管理/计算薪资接口"; DataFile="calculate_salary_test_data.json"},
    @{Folder="薪资管理/发放薪资接口"; DataFile="pay_salary_test_data.json"},
    @{Folder="薪资管理/获取我的薪资接口"; DataFile="get_my_salary_test_data.json"},
    @{Folder="薪资管理/获取所有薪资记录接口"; DataFile=""},
    @{Folder="物料管理/添加物料接口"; DataFile="add_material_test_data.json"},
    @{Folder="物料管理/物料出库接口"; DataFile="material_outbound_test_data.json"},
    @{Folder="物料管理/物料租赁接口"; DataFile="material_rent_test_data.json"},
    @{Folder="物料管理/获取仓库物料接口"; DataFile=""},
    @{Folder="报销管理/添加报销申请接口"; DataFile="add_reimbursement_test_data.json"},
    @{Folder="报销管理/审批报销申请接口"; DataFile="approve_reimbursement_test_data.json"},
    @{Folder="报销管理/获取所有报销申请接口"; DataFile=""},
    @{Folder="报销管理/根据用户 ID 获取报销申请接口"; DataFile="get_reimbursement_by_user_test_data.json"}
)

$totalTests = $testCases.Count
$passedTests = 0
$failedTests = 0

# 遍历每个测试用例
for ($i = 0; $i -lt $totalTests; $i++) {
    $testCase = $testCases[$i]
    $folder = $testCase.Folder
    $dataFile = $testCase.DataFile
    $testNum = $i + 1
    
    # 提取接口名称用于显示
    $interfaceName = $folder.Split('/')[-1]
    
    Write-Host "[$testNum/$totalTests] 测试：$interfaceName" -ForegroundColor Cyan
    
    if ($dataFile -eq "") {
        # 无参数化文件的接口
        Write-Host "  → 无参数化数据，直接运行..." -ForegroundColor Gray
        newman run $Collection --folder $folder --delay-request $Delay --reporters cli
    } else {
        # 有参数化文件的接口
        if (Test-Path $dataFile) {
            Write-Host "  → 使用数据文件：$dataFile" -ForegroundColor Gray
            newman run $Collection --folder $folder -d $dataFile --delay-request $Delay --reporters cli
        } else {
            Write-Host "  → 警告：数据文件不存在 - $dataFile" -ForegroundColor Yellow
            $failedTests++
            continue
        }
    }
    
    # 检查结果
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ 测试通过" -ForegroundColor Green
        $passedTests++
    } else {
        Write-Host "  ✗ 测试失败" -ForegroundColor Red
        $failedTests++
    }
    
    Write-Host ""
}

# 输出统计
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "测试总结" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "总测试数：$totalTests" -ForegroundColor White
Write-Host "通过：$passedTests" -ForegroundColor Green
Write-Host "失败：$failedTests" -ForegroundColor Red
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

if ($failedTests -eq 0) {
    Write-Host "所有测试通过！" -ForegroundColor Green
} else {
    Write-Host "部分测试失败，请检查上面的错误信息" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "按任意键退出..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
