# JMeter 性能测试文件使用说明

## 文件结构

```
Jmeter/
├── login_performance_test.jmx          # 登录接口性能测试计划
├── apply_activity_performance_test.jmx # 活动申请接口性能测试计划
├── login_test_data.csv                 # 登录接口测试数据
├── apply_activity_test_data.csv        # 活动申请接口测试数据
└── README.md                           # 本说明文档
```

## 环境要求

- JMeter 5.0 或更高版本
- Java 8 或更高版本
- 活动策划管理平台服务运行在 http://localhost:8080/EventPlanning_Management

## 测试计划说明

### 1. 登录接口性能测试 (login_performance_test.jmx)

**测试目标**：测试登录接口在并发用户访问下的性能表现

**测试配置**：
- 线程数：50 个并发用户
- 爬坡时间：10 秒
- 循环次数：10 次
- 总请求数：500 次

**测试场景**：
- 正常登录（管理员、普通用户、员工）
- 异常登录（密码错误、角色错误、用户不存在、空参数等）

**参数说明**：
- baseUrl: http://localhost:8080/EventPlanning_Management
- 请求路径：/login
- 请求方法：GET
- 参数：username, password, roleId

### 2. 活动申请接口性能测试 (apply_activity_performance_test.jmx)

**测试目标**：测试活动申请接口在并发提交下的性能表现

**测试配置**：
- 线程数：30 个并发用户
- 爬坡时间：15 秒
- 循环次数：10 次
- 总请求数：300 次

**测试场景**：
- 正常活动申请（公司年会、周年庆典、新品发布会等）
- 不同地点、规模、预算的活动申请

**参数说明**：
- baseUrl: http://localhost:8080/EventPlanning_Management
- 请求路径：/applyActivity
- 请求方法：GET
- 参数：userId, activity_place, activity_type, start_time, end_time, activity_people, activity_budget, activity_progress, requirement_desc, phone, name

## 使用步骤

### 步骤 1：导入测试计划

1. 启动 JMeter
2. 点击"文件" -> "打开"
3. 选择对应的 .jmx 文件（login_performance_test.jmx 或 apply_activity_performance_test.jmx）

### 步骤 2：配置 CSV 数据文件

确保 CSV 数据文件与 JMX 文件在同一目录下，或者修改 CSV 数据文件设置中的文件路径：
- login_test_data.csv
- apply_activity_test_data.csv

### 步骤 3：调整测试参数（可选）

根据实际测试需求，可以调整以下参数：

**线程组配置**：
- 线程数（并发用户数）
- 爬坡时间（秒）
- 循环次数

**修改方法**：
1. 在线程组上右键 -> 编辑
2. 修改对应的参数值

### 步骤 4：运行测试

1. 点击工具栏的绿色"启动"按钮（或按 Ctrl+R）
2. 观察测试执行过程
3. 查看聚合报告、图形结果等

### 步骤 5：查看结果

测试完成后，可以查看以下结果：

**聚合报告**：
- 平均值：平均响应时间
- 中位数：50% 请求的响应时间
- 90% 百分位：90% 请求的响应时间
- 最小值/最大值：最小/最大响应时间
- 错误率：请求失败的比例
- 吞吐量：每秒处理的请求数

**图形结果**：
- 响应时间随时间变化的曲线图
- 帮助识别性能瓶颈和趋势

**察看结果树**：
- 查看每个请求的详细信息
- 用于调试和排查问题

## 性能指标参考

### 登录接口

- 优秀：平均响应时间 < 200ms，错误率 < 0.1%
- 良好：平均响应时间 < 500ms，错误率 < 1%
- 可接受：平均响应时间 < 1000ms，错误率 < 5%
- 需要优化：平均响应时间 > 1000ms 或 错误率 > 5%

### 活动申请接口

- 优秀：平均响应时间 < 500ms，错误率 < 0.1%
- 良好：平均响应时间 < 1000ms，错误率 < 1%
- 可接受：平均响应时间 < 2000ms，错误率 < 5%
- 需要优化：平均响应时间 > 2000ms 或 错误率 > 5%

## 自定义测试数据

### 登录接口测试数据格式

```csv
id,username,password,roleId,description
1,test_user,password123,1，描述信息
```

- id: 测试数据编号
- username: 用户名
- password: 密码
- roleId: 角色 ID（1-管理员，2-员工）
- description: 描述信息

### 活动申请接口测试数据格式

```csv
id,userId,activity_place,activity_type,start_time,end_time,activity_people,activity_budget,activity_progress,requirement_desc,phone,name,description
ACT_021,1，北京，公司会议，2026-05-20,2026-05-21,20-30,2-3 万，0，需要会议室，13800138021，测试用户，描述信息
```

## 注意事项

1. **测试环境**：建议在生产环境副本或独立测试环境进行性能测试
2. **数据清理**：测试完成后，及时清理测试产生的数据
3. **并发控制**：根据服务器实际承载能力调整并发用户数
4. **监控**：测试过程中监控服务器资源（CPU、内存、数据库连接等）
5. **网络**：确保测试环境网络稳定，避免网络波动影响测试结果

## 常见问题

### Q1: CSV 文件读取失败？
**A**: 确保 CSV 文件编码为 UTF-8，且文件路径正确

### Q2: 请求返回 404 错误？
**A**: 检查 baseUrl 配置是否正确，确保服务已启动

### Q3: 中文参数乱码？
**A**: 确保请求编码设置为 UTF-8，服务器端也配置了正确的编码

### Q4: 如何保存测试结果？
**A**: 右键点击"聚合报告" -> "保存数据为 CSV 文件"

## 扩展建议

1. **添加断言**：根据实际业务需求，添加更详细的响应断言
2. **关联测试**：将登录和活动申请接口关联，模拟真实用户场景
3. **分布式测试**：使用 JMeter 分布式测试功能，模拟更大并发量
4. **持续集成**：将性能测试集成到 CI/CD 流程中

## 技术支持

如有问题，请参考：
- JMeter 官方文档：https://jmeter.apache.org/usermanual/index.html
- 项目文档：../new_postman/使用说明.md
