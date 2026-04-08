# 活动策划公司管理系统 - 接口测试指南

## 📋 文档信息

| 项目 | 内容 |
|------|------|
| 测试框架 | Postman + Newman |
| 数据库 | MySQL |
| 测试类型 | 接口自动化测试 |
| 测试覆盖率 | 98% |

---

## 📁 文件说明

### 测试集合文件

| 文件名 | 说明 | 适用场景 |
|--------|------|----------|
| `postman_collection_db.json` | **推荐使用** - 基于数据库数据的测试集合，包含严格的数据验证 | 正式测试、回归测试 |
| `postman_collection_full.json` | 完整测试集合，包含所有接口 | 全面测试 |

### 测试数据文件

| 文件名 | 说明 |
|--------|------|
| `test_data_db.json` | **推荐使用** - 由 Python 脚本从数据库自动生成 |
| `test_data_comprehensive.json` | 综合测试数据（65 个用例） |

### 脚本文件

| 文件名 | 功能 |
|--------|------|
| `generate_test_data.py` | 连接数据库，自动生成测试数据 |
| `run_all_tests.ps1` | 一键运行所有测试并生成中文报告 |
| `run_tests_cn.ps1` | 运行测试并生成中文摘要 |

---

## 🚀 快速开始

### 方式一：一键运行（推荐）

```powershell
# 进入 postman 目录
cd e:\毕业论文\Java_Project\EventPlanning_Management\postman

# 运行一键测试脚本
.\run_all_tests.ps1
```

**脚本会自动完成：**
1. ✅ 检查 Python 和 Newman 环境
2. ✅ 连接数据库生成测试数据
3. ✅ 运行所有测试用例
4. ✅ 生成中文文本报告和 HTML 报告
5. ✅ 显示测试统计（通过率、失败数等）

---

### 方式二：手动运行

#### 1. 安装 Newman

```bash
npm install -g newman
```

#### 2. 生成测试数据（可选）

```bash
# 安装 Python MySQL 库
pip install pymysql

# 运行生成脚本
python generate_test_data.py
```

#### 3. 运行测试

```bash
# 使用数据库生成的测试数据（推荐）
newman run postman_collection_db.json -d test_data_db.json

# 或使用综合测试数据
newman run postman_collection_full.json -d test_data_comprehensive.json
```

#### 4. 生成报告

```bash
# 生成 HTML 报告
newman run postman_collection_db.json -d test_data_db.json -r htmlextra --reporter-htmlextra-export ../reports/test_report.html

# 生成中文文本报告
newman run postman_collection_db.json -d test_data_db.json -r cli > ../reports/test_result_cn.txt
```

---

## ✅ 测试断言说明

### 新的测试集合包含以下断言：

1. **HTTP 状态码验证** - 检查响应状态码是否为 200
2. **JSON 格式验证** - 检查响应是否为 JSON 格式
3. **flag 字段存在性** - 检查响应中是否包含 flag 字段
4. **flag 值验证** - 检查 flag 值是否符合预期（success/fail）
5. **data 字段存在性** - 检查响应中是否包含 data 字段
6. **data 非空验证** - 根据预期判断 data 是否为空
   - 如果 `should_have_data: true`，则 data 不能为空
   - 如果 data 是数组，检查数组长度 > 0
   - 如果 data 是对象，检查对象属性数 > 0

### 测试数据字段说明

```json
{
  "case_id": "测试用例 ID",
  "case_name": "测试用例名称（中文）",
  "endpoint": "接口路径",
  "username": "用户名（可选）",
  "password": "密码（可选）",
  "roleId": "角色 ID（可选）",
  "userId": "用户 ID（可选）",
  "demandId": "活动 ID（可选）",
  "expected_flag": "期望的 flag 值（success 或 fail）",
  "should_have_data": "data 字段是否应该有值（true 或 false）"
}
```

---

## 📊 测试覆盖模块

### 1. 用户管理接口测试
- ✅ 用户登录
- ✅ 用户注册
- ✅ 查询用户信息
- ✅ 查询所有用户
- ✅ 更新用户信息
- ✅ 删除用户

### 2. 活动管理接口测试
- ✅ 创建活动
- ✅ 查询活动
- ✅ 更新活动
- ✅ 取消活动
- ✅ 查询所有活动

### 3. 人员管理接口测试
- ✅ 添加员工
- ✅ 查询员工
- ✅ 更新员工信息
- ✅ 查询所有员工
- ✅ 按条件搜索员工

### 4. 物料管理接口测试
- ✅ 添加物料
- ✅ 查询物料
- ✅ 更新物料
- ✅ 按分类查询
- ✅ 查询所有物料

### 5. 薪资管理接口测试
- ✅ 创建薪资记录
- ✅ 查询薪资
- ✅ 更新薪资
- ✅ 按月查询
- ✅ 查询所有薪资

### 6. 报销管理接口测试
- ✅ 创建报销申请
- ✅ 查询报销
- ✅ 审核报销
- ✅ 按状态查询
- ✅ 查询所有报销

---

## 🔍 查看测试结果

### 文本报告

```bash
# 查看最新的中文测试报告
type ../reports/test_result_cn.txt

# 或使用记事本打开
notepad ../reports/test_result_cn.txt
```

### HTML 报告

```bash
# 在浏览器中打开 HTML 报告
start ../reports/test_report.html
```

### 控制台输出

直接运行测试时，控制台会显示详细的测试结果：
- ✅ 表示测试通过
- ❌ 表示测试失败
- 包含失败原因和期望值

---

## 📞 常见问题

### Q: 如何跳过某些测试用例？
A: 在测试数据文件中删除该用例，或将 `expected_flag` 改为实际期望值

### Q: 如何查看测试报告？
A: 使用 `-r htmlextra` 参数生成 HTML 报告，或使用 `-r cli` 生成文本报告

### Q: 报告是英文的，有中文版吗？
A: 
- HTML 报告是英文界面，但包含中文测试用例名
- 使用 `-r cli > report.txt` 生成中文文本报告
- 运行 `.\run_all_tests.ps1` 自动生成中文报告

### Q: 测试覆盖率是多少？
A: 当前覆盖了 56 个接口中的 55 个，覆盖率约 98%

### Q: 如何添加新的测试用例？
A: 
1. 在测试数据 JSON 文件中添加新用例
2. 确保包含所有必填字段
3. 设置正确的 `expected_flag` 和 `should_have_data`

### Q: 数据库连接失败怎么办？
A: 
1. 检查 MySQL 服务是否启动
2. 确认数据库配置正确（用户名、密码、数据库名）
3. 检查 `generate_test_data.py` 中的数据库连接配置

---

## 📝 数据库配置

测试脚本使用的数据库连接信息：

```java
private static String url = "jdbc:mysql://localhost:3306/eventplanning_management?serverTimezone=GMT%2B8&useOldAliasMetadataBehavior=true&useUnicode=true&characterEncoding=UTF-8";
private static String userName = "root";
private static String password = "123456";
private static String driver = "com.mysql.cj.jdbc.Driver";
```

如需修改数据库配置，请编辑 `generate_test_data.py` 文件中的 `DB_CONFIG` 部分。

---

## 🎯 最佳实践

### 1. 测试前准备
- ✅ 确保 Tomcat 服务器已启动
- ✅ 确保 MySQL 数据库已启动
- ✅ 确保测试数据库中有测试数据

### 2. 运行测试
- ✅ 使用 `run_all_tests.ps1` 一键运行
- ✅ 检查测试报告中的失败用例
- ✅ 分析失败原因并修复

### 3. 持续集成
- ✅ 将测试脚本加入 CI/CD 流程
- ✅ 定期运行回归测试
- ✅ 保存测试报告作为文档

---

## 📧 技术支持

如有问题，请检查：
1. Tomcat 服务器是否运行
2. MySQL 数据库是否连接正常
3. Newman 和 Python 是否正确安装
4. 测试数据文件是否完整

---

**最后更新**: 2026-03-28  
**版本**: v3.0
