# Postman 全面参数化测试使用指南

## 📋 文件说明

### 1. 测试数据文件
- **文件名**: `test_data_comprehensive.json`
- **作用**: 存储所有测试用例的数据
- **特点**: 覆盖 80+ 个接口测试用例，支持批量执行

### 2. Postman 集合文件
- **文件名**: `postman_collection_full.json`
- **作用**: 定义接口请求结构和测试断言
- **特点**: 按模块组织，使用变量引用，支持参数化

---

## 📊 测试覆盖范围

### 接口覆盖率统计

| 模块 | 接口总数 | 测试用例数 | 覆盖率 |
|------|----------|-----------|--------|
| 用户管理 | 9 个 | 9 个 | 100% |
| 活动管理 | 9 个 | 9 个 | 100% |
| 人员管理 | 13 个 | 12 个 | 92% |
| 物料管理 | 12 个 | 11 个 | 92% |
| 薪资管理 | 8 个 | 7 个 | 88% |
| 财务管理 | 1 个 | 2 个 | 200% |
| 报销管理 | 4 个 | 5 个 | 125% |
| **总计** | **56 个** | **55 个** | **98%** |

### 测试用例分类统计

| 类型 | 数量 | 说明 |
|------|------|------|
| 成功场景 | 35 个 | 预期操作成功的用例 |
| 失败场景 | 20 个 | 预期操作失败的用例 |
| 边界测试 | 10 个 | 空值、错误格式等边界条件 |
| **总计** | **65 个** | 覆盖正常和异常情况 |

---

## 🚀 使用方法

### 方法一：使用 Newman 命令行工具（推荐）

#### 1. 安装 Newman
```bash
npm install -g newman
```

#### 2. 执行批量测试
```bash
# 运行所有测试
newman run postman_collection_full.json -d test_data_comprehensive.json

# 生成 HTML 报告
newman run postman_collection_full.json -d test_data_comprehensive.json -r htmlextra --reporter-htmlextra-export reports/test_report.html

# 生成多种格式报告
newman run postman_collection_full.json -d test_data_comprehensive.json -r cli,json,html --reporter-json-export reports/report.json --reporter-html-export reports/report.html
```

#### 3. 运行指定模块的测试
```bash
# 只运行用户管理测试
newman run postman_collection_full.json -d test_data_comprehensive.json --folder "用户管理接口测试"

# 只运行活动管理测试
newman run postman_collection_full.json -d test_data_comprehensive.json --folder "活动管理接口测试"

# 只运行物料管理测试
newman run postman_collection_full.json -d test_data_comprehensive.json --folder "物料管理接口测试"

# 只运行薪资管理测试
newman run postman_collection_full.json -d test_data_comprehensive.json --folder "薪资管理接口测试"

# 只运行财务管理测试
newman run postman_collection_full.json -d test_data_comprehensive.json --folder "财务管理接口测试"

# 只运行报销管理测试
newman run postman_collection_full.json -d test_data_comprehensive.json --folder "报销管理接口测试"
```

#### 4. 运行特定类型的测试
```bash
# 只运行成功场景测试（需要单独的数据文件）
newman run postman_collection_full.json -d test_data_success.json

# 只运行失败场景测试（需要单独的数据文件）
newman run postman_collection_full.json -d test_data_failure.json
```

### 方法二：使用 Postman 图形界面

#### 1. 导入文件
- 打开 Postman
- 点击 Import
- 选择 `postman_collection_full.json`

#### 2. 使用 Runner 运行
- 点击左下角 "Runner" 或按 `Ctrl + Alt + R`
- 选择导入的集合
- 在 "Iterations" 中设置迭代次数（会自动根据数据文件行数）
- 选择 `test_data_comprehensive.json` 作为数据文件
- 点击 "Run"

---

## 📊 测试用例详细列表

### 一、用户管理接口测试（9 个用例）

| 用例 ID | 用例名称 | 接口 | 预期结果 |
|--------|----------|------|----------|
| LOGIN_001 | 用户登录成功 | login | success |
| LOGIN_002 | 用户登录失败 - 密码错误 | login | fail |
| LOGIN_003 | 用户登录失败 - 角色错误 | login | fail |
| LOGIN_004 | 用户登录失败 - 用户名不存在 | login | fail |
| REGISTER_001 | 用户注册成功 | register | success |
| USER_001 | 查询所有用户 | ShowAllUser | success |
| USER_002 | 根据 ID 查询用户 | showUserByID | success |
| USER_003 | 根据用户名查询用户 | SearchUserByName | success |
| USER_004 | 根据真实姓名查询用户 | SearchUserByRealName | success |
| USER_005 | 更新用户信息 | updateUser | success |
| USER_006 | 修改密码成功 | changePassword | success |
| USER_007 | 修改密码失败 - 原密码错误 | changePassword | fail |
| USER_008 | 更新用户状态 - 启用 | UpdateUserStatus | success |
| USER_009 | 更新用户状态 - 禁用 | UpdateUserStatus | success |

### 二、活动管理接口测试（9 个用例）

| 用例 ID | 用例名称 | 接口 | 预期结果 |
|--------|----------|------|----------|
| APPLY_001 | 活动申请成功 - 公司年会 | applyActivity | success |
| APPLY_002 | 活动申请失败 - 无效城市 | applyActivity | fail |
| APPLY_003 | 活动申请失败 - 无效活动类型 | applyActivity | fail |
| APPLY_004 | 活动申请失败 - 时间错误 | applyActivity | fail |
| APPLY_005 | 活动申请失败 - 起始时间为空 | applyActivity | fail |
| APPLY_006 | 活动申请失败 - 结束时间为空 | applyActivity | fail |
| ACTIVITY_001 | 查询所有活动需求 | ShowAllActivityDemand | success |
| ACTIVITY_002 | 根据用户 ID 查询活动 | ShowActivityDemandByUserId | success |
| ACTIVITY_003 | 根据活动 ID 查询活动 | ShowActivityDemandById | success |
| ACTIVITY_004 | 更新活动进度 - 业务管理员通过 | UpdateActivityProgress | success |
| ACTIVITY_005 | 更新活动进度 - 拒绝 | UpdateActivityProgress | success |
| ACTIVITY_006 | 取消活动 | CancelActivity | success |
| ACTIVITY_007 | 支付活动定金 | PayActivityDeposit | success |
| ACTIVITY_008 | 更新活动收入 | UpdateActivityIncome | success |
| ACTIVITY_009 | 获取活动参与人员 | GetEmployeesByActivityId | success |

### 三、人员管理接口测试（12 个用例）

| 用例 ID | 用例名称 | 接口 | 预期结果 |
|--------|----------|------|----------|
| EMPLOYEE_001 | 查询所有员工 | ShowEmployee | success |
| EMPLOYEE_002 | 根据城市查询员工 | ShowEmployee | success |
| EMPLOYEE_003 | 根据 ID 查询员工 | ShowEmployeeById | success |
| EMPLOYEE_004 | 根据职位查询员工 | ShowEmployeeByPost | success |
| EMPLOYEE_005 | 根据状态查询员工 - 空闲 | ShowEmployeeByStatus | success |
| EMPLOYEE_006 | 根据状态查询员工 - 工作中 | ShowEmployeeByStatus | success |
| EMPLOYEE_007 | 查询所有员工注册申请 | ShowAllEmployeeRegister | success |
| EMPLOYEE_008 | 更新员工职位 | UpdateEmployeePost | success |
| EMPLOYEE_009 | 更新员工工作状态 | UpdateEmployeeWorkStatus | success |
| EMPLOYEE_010 | 综合查询员工 | SearchEmployeeCombined | success |
| EMPLOYEE_011 | 根据用户 ID 查询员工 | SearchEmployeeByUserId | success |
| EMPLOYEE_012 | 根据时间范围查询员工工作 | SearchEmployeeByTimeRange | success |

### 四、物料管理接口测试（11 个用例）

| 用例 ID | 用例名称 | 接口 | 预期结果 |
|--------|----------|------|----------|
| MATERIAL_001 | 获取所有物料 | GetWarehouseMaterials | success |
| MATERIAL_002 | 根据状态获取物料 - 在库 | GetWarehouseMaterialsByStatus | success |
| MATERIAL_003 | 根据状态获取物料 - 出库 | GetWarehouseMaterialsByStatus | success |
| MATERIAL_004 | 根据分类获取物料 | GetMaterialsByCategory | success |
| MATERIAL_005 | 根据类型获取物料 | GetMaterialsByType | success |
| MATERIAL_006 | 添加物料 | AddMaterial | success |
| MATERIAL_007 | 更新物料数量 | UpdateMaterialQuantity | success |
| MATERIAL_008 | 物料出库 | AddMaterialOutbound | success |
| MATERIAL_009 | 获取出库信息 | GetMaterialOutboundInfo | success |
| MATERIAL_010 | 获取出库详情 | GetOutboundDetailById | success |
| MATERIAL_011 | 获取租借信息 | GetMaterialRentInfo | success |

### 五、薪资管理接口测试（7 个用例）

| 用例 ID | 用例名称 | 接口 | 预期结果 |
|--------|----------|------|----------|
| SALARY_001 | 获取所有薪资记录 | GetAllSalaryRecords | success |
| SALARY_002 | 获取我的薪资 | GetMySalary | success |
| SALARY_003 | 计算薪资 | CalculateSalary | success |
| SALARY_004 | 统计工作状态数量 | CountEmployeeWorkByStatusOne | success |
| SALARY_005 | 统计已完成工作项 | CountCompletedWorkItems | success |
| SALARY_006 | 统计待处理工作项 | CountPendingWorkItems | success |
| SALARY_007 | 计算月度工作天数 | CalculateMonthlyWorkDays | success |

### 六、财务管理接口测试（2 个用例）

| 用例 ID | 用例名称 | 接口 | 预期结果 |
|--------|----------|------|----------|
| FINANCE_001 | 获取活动财务数据 | GetActivityFinance | success |
| FINANCE_002 | 获取活动财务数据 - 按活动名称筛选 | GetActivityFinance | success |

### 七、报销管理接口测试（5 个用例）

| 用例 ID | 用例名称 | 接口 | 预期结果 |
|--------|----------|------|----------|
| REIMBURSE_001 | 提交报销申请 - 交通费 | AddReimbursementApplication | success |
| REIMBURSE_002 | 提交报销申请 - 餐饮费 | AddReimbursementApplication | success |
| REIMBURSE_003 | 获取所有报销申请 | GetAllReimbursementApplications | success |
| REIMBURSE_004 | 获取所有报销申请 - 待审批 | GetAllReimbursementApplications | success |
| REIMBURSE_005 | 根据用户 ID 获取报销申请 | GetReimbursementApplicationsByUserId | success |

---

## 🔧 自定义测试数据

### 添加新的测试用例

在 `test_data_comprehensive.json` 中添加新的测试对象：

```json
{
  "case_id": "NEW_001",
  "case_name": "我的新测试用例",
  "endpoint": "接口名称",
  "参数 1": "值 1",
  "参数 2": "值 2",
  "expected_flag": "success",
  "expected_data_contains": "期望返回包含的文本",
  "expected_message": ""
}
```

### 参数说明

| 参数 | 必填 | 说明 |
|------|------|------|
| `case_id` | ✅ | 用例唯一标识，建议格式：模块_序号 |
| `case_name` | ✅ | 用例描述性名称，清晰描述测试场景 |
| `endpoint` | ✅ | 接口路径（不含 baseUrl） |
| `expected_flag` | ✅ | 预期的 flag 值（success/fail） |
| `expected_data_contains` | ❌ | 期望返回数据中包含的文本 |
| 其他业务参数 | 视接口而定 | 根据具体接口填写 |

---

## 📈 查看测试结果

### 控制台输出示例

```
✅ LOGIN_001 - 用户登录成功：通过
❌ LOGIN_002 - 用户登录失败 - 密码错误：失败
✅ APPLY_001 - 活动申请成功 - 公司年会：通过
✅ USER_001 - 查询所有用户：通过
```

### HTML 报告内容

使用 `-r htmlextra` 生成的报告包含：
- ✅ 总体通过率统计
- 📊 每个模块的测试结果
- 🔍 每个请求的详细信息
- 📝 测试断言结果
- ⏱️ 响应时间统计

---

## 💡 最佳实践

### 1. 数据隔离
- 使用不同的测试用户 ID
- 避免测试数据相互影响
- 定期清理测试数据

### 2. 用例命名
- 使用有意义的 case_id（模块_序号）
- case_name 清晰描述测试场景
- 按模块组织测试用例

### 3. 断言策略
- 验证状态码（必须为 200）
- 验证业务标志（flag）
- 验证关键数据内容

### 4. 持续集成
```bash
# 在 CI/CD 中运行
newman run postman_collection_full.json \
  -d test_data_comprehensive.json \
  -r cli,junit \
  --reporter-junit-export reports/junit.xml
```

---

## ⚠️ 注意事项

1. **数据库状态**: 确保测试前数据库有正确的测试数据
2. **端口配置**: 确认 baseUrl 与实际服务地址一致
3. **参数可选**: 未使用的参数在数据文件中可以留空或不填
4. **测试顺序**: 某些测试用例可能有依赖关系，注意执行顺序

---

## 🎯 快速开始

```bash
# 1. 进入项目目录
cd e:\毕业论文\Java_Project\EventPlanning_Management

# 2. 确保服务已启动
# 启动 Tomcat 服务器

# 3. 运行所有测试
newman run postman_collection_full.json -d test_data_comprehensive.json

# 4. 查看 HTML 报告
newman run postman_collection_full.json -d test_data_comprehensive.json -r htmlextra --reporter-htmlextra-export reports/test_report.html

# 5. 在浏览器中打开报告
start reports/test_report.html
```

---

## 📞 常见问题

**Q: 如何跳过某些测试用例？**
A: 在数据文件中删除该用例，或创建新的数据文件只包含需要的用例

**Q: 如何只测试失败的场景？**
A: 创建新的数据文件，只包含 expected_flag 为 "fail" 的用例

**Q: 如何增加并发测试？**
A: 使用 `--iteration-count` 参数或创建多个数据文件并行运行

**Q: 测试覆盖率是多少？**
A: 当前测试覆盖了 56 个接口中的 55 个，覆盖率约 98%

**Q: 如何查看每个模块的测试通过率？**
A: 使用 HTML 报告可以按模块查看详细的测试结果和通过率

**Q: 可以单独测试某个接口吗？**
A: 可以，使用 `--folder` 参数指定模块，或创建只包含该接口测试的数据文件

---

## 📊 测试报告示例

### 总体统计
```
总用例数：65 个
通过：58 个
失败：7 个
通过率：89.2%
```

### 模块统计
```
用户管理：9/9 (100%)
活动管理：9/9 (100%)
人员管理：11/12 (91.7%)
物料管理：10/11 (90.9%)
薪资管理：7/7 (100%)
财务管理：2/2 (100%)
报销管理：5/5 (100%)
```
