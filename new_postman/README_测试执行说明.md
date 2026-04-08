# Newman 参数化批量执行脚本使用说明

## 目录结构

```
new_postman/
├── event_planning_collection.json          # Postman 集合文件
├── run_all_tests.bat                       # Windows 批处理脚本
├── run_all_tests.ps1                       # PowerShell 脚本
├── login_test_data.json                    # 登录接口测试数据
├── register_user_test_data.json            # 用户注册接口测试数据
├── register_employee_test_data.json        # 员工注册接口测试数据
├── change_password_test_data.json          # 修改密码接口测试数据
├── get_user_by_id_test_data.json           # 根据 ID 获取用户接口测试数据
├── apply_activity_test_data.json           # 申请活动接口测试数据
├── cancel_activity_test_data.json          # 取消活动接口测试数据
├── update_activity_progress_test_data.json # 更新活动进度接口测试数据
├── get_activity_by_id_test_data.json       # 根据 ID 获取活动需求接口测试数据
├── get_activity_by_user_test_data.json     # 根据用户 ID 获取活动需求接口测试数据
├── handle_employee_register_test_data.json # 处理员工注册接口测试数据
├── calculate_salary_test_data.json         # 计算薪资接口测试数据
├── pay_salary_test_data.json               # 发放薪资接口测试数据
├── get_my_salary_test_data.json            # 获取我的薪资接口测试数据
├── add_material_test_data.json             # 添加物料接口测试数据
├── material_outbound_test_data.json        # 物料出库接口测试数据
├── material_rent_test_data.json            # 物料租赁接口测试数据
├── add_reimbursement_test_data.json        # 添加报销申请接口测试数据
├── approve_reimbursement_test_data.json    # 审批报销申请接口测试数据
└── get_reimbursement_by_user_test_data.json # 根据用户 ID 获取报销申请接口测试数据
```

## 前置要求

1. **安装 Node.js** (v14 或更高版本)
2. **安装 Newman**: 
   ```bash
   npm install -g newman
   ```

3. **确保 Tomcat 服务器已启动**，项目部署在 `http://localhost:8080/EventPlanning_Management`

## 执行方式

### 方式一：使用批处理脚本（推荐）

```bash
cd e:\毕业论文\Java_Project\EventPlanning_Management\new_postman
run_all_tests.bat
```

### 方式二：使用 PowerShell 脚本

```powershell
cd e:\毕业论文\Java_Project\EventPlanning_Management\new_postman
.\run_all_tests.ps1
```

### 方式三：手动执行单个接口测试

```bash
# 语法
newman run event_planning_collection.json --folder "模块名/接口名" -d 测试数据文件.json --delay-request 100

# 示例：执行登录接口测试
newman run event_planning_collection.json --folder "用户管理/登录接口" -d login_test_data.json --delay-request 100

# 示例：执行无参数的接口（获取所有用户）
newman run event_planning_collection.json --folder "用户管理/获取所有用户接口" --delay-request 100
```

## 测试接口列表

### 用户管理（6 个接口）

| 序号 | 接口名称 | 参数化文件 | 说明 |
|------|---------|-----------|------|
| 1 | 登录接口 | login_test_data.json | 测试用户登录功能 |
| 2 | 注册接口（用户） | register_user_test_data.json | 测试普通用户注册 |
| 3 | 注册接口（员工） | register_employee_test_data.json | 测试员工注册 |
| 4 | 修改密码接口 | change_password_test_data.json | 测试密码修改 |
| 5 | 获取所有用户接口 | 无 | 获取所有用户列表 |
| 6 | 根据 ID 获取用户接口 | get_user_by_id_test_data.json | 根据用户 ID 查询 |

### 活动管理（6 个接口）

| 序号 | 接口名称 | 参数化文件 | 说明 |
|------|---------|-----------|------|
| 7 | 申请活动接口 | apply_activity_test_data.json | 提交活动申请 |
| 8 | 取消活动接口 | cancel_activity_test_data.json | 取消已申请的活动 |
| 9 | 更新活动进度接口 | update_activity_progress_test_data.json | 更新活动进度状态 |
| 10 | 获取所有活动需求接口 | 无 | 获取所有活动需求 |
| 11 | 根据 ID 获取活动需求接口 | get_activity_by_id_test_data.json | 根据 ID 查询活动需求 |
| 12 | 根据用户 ID 获取活动需求接口 | get_activity_by_user_test_data.json | 根据用户 ID 查询 |

### 员工管理（2 个接口）

| 序号 | 接口名称 | 参数化文件 | 说明 |
|------|---------|-----------|------|
| 13 | 处理员工注册接口 | handle_employee_register_test_data.json | 审核员工注册申请 |
| 14 | 获取所有员工接口 | 无 | 获取所有员工列表 |

### 薪资管理（4 个接口）

| 序号 | 接口名称 | 参数化文件 | 说明 |
|------|---------|-----------|------|
| 15 | 计算薪资接口 | calculate_salary_test_data.json | 计算员工薪资 |
| 16 | 发放薪资接口 | pay_salary_test_data.json | 发放员工薪资 |
| 17 | 获取我的薪资接口 | get_my_salary_test_data.json | 员工查询自己的薪资 |
| 18 | 获取所有薪资记录接口 | 无 | 管理员查询所有薪资记录 |

### 物料管理（4 个接口）

| 序号 | 接口名称 | 参数化文件 | 说明 |
|------|---------|-----------|------|
| 19 | 添加物料接口 | add_material_test_data.json | 添加新物料到仓库 |
| 20 | 物料出库接口 | material_outbound_test_data.json | 物料出库操作 |
| 21 | 物料租赁接口 | material_rent_test_data.json | 物料租赁操作 |
| 22 | 获取仓库物料接口 | 无 | 查询仓库物料库存 |

### 报销管理（4 个接口）

| 序号 | 接口名称 | 参数化文件 | 说明 |
|------|---------|-----------|------|
| 23 | 添加报销申请接口 | add_reimbursement_test_data.json | 提交报销申请 |
| 24 | 审批报销申请接口 | approve_reimbursement_test_data.json | 审批报销申请 |
| 25 | 获取所有报销申请接口 | 无 | 获取所有报销申请 |
| 26 | 根据用户 ID 获取报销申请接口 | get_reimbursement_by_user_test_data.json | 根据用户 ID 查询 |

## 参数说明

- `--folder "模块名/接口名"`: 指定要执行的接口文件夹
- `-d 测试数据文件.json`: 指定参数化测试数据文件
- `--delay-request 100`: 每个请求之间延迟 100ms
- `--reporters cli`: 使用命令行报告格式（默认）

## 输出说明

测试执行后会显示：
- ✅ 通过的测试（绿色）
- ❌ 失败的测试（红色）
- 请求响应时间
- 响应状态码

## 常见问题

### 1. Newman 未找到
```bash
npm install -g newman
```

### 2. 集合文件未找到
确保在 `new_postman` 目录下执行脚本，或者使用完整路径：
```bash
newman run e:\毕业论文\Java_Project\EventPlanning_Management\new_postman\event_planning_collection.json ...
```

### 3. 接口返回 404
确保 Tomcat 服务器已启动，并且项目已正确部署。

### 4. 中文乱码
脚本已设置 UTF-8 编码，如果仍有问题，请确保：
- 系统区域设置支持 UTF-8
- Postman 集合文件使用 UTF-8 编码保存

## 自定义测试

如果需要修改测试数据，编辑对应的 `*_test_data.json` 文件。每个文件的格式如下：

```json
[
  {
    "id": "TEST_001",
    "param1": "value1",
    "param2": "value2",
    "description": "测试场景描述"
  },
  {
    "id": "TEST_002",
    "param1": "value3",
    "param2": "value4",
    "description": "另一个测试场景"
  }
]
```

## 联系支持

如有问题，请检查：
1. Tomcat 服务器是否运行
2. 数据库连接是否正常
3. 测试数据是否存在
4. Newman 版本是否兼容（建议 v6.x）
