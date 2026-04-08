# EventPlanning Management UI自动化测试框架

基于 Python + Pytest + Selenium 的UI自动化测试框架，采用4层架构设计。

## 项目结构

```
selenium/
├── base/                   # 基础封装层 (Base Layer)
│   ├── __init__.py
│   ├── base_page.py       # 基础页面类，封装通用Selenium操作
│   └── driver_manager.py  # WebDriver管理类
├── config/                 # 配置层 (Config Layer)
│   ├── __init__.py
│   ├── settings.py        # 项目配置
│   └── logger_config.py   # 日志配置
├── data/                   # 数据层 (Data Layer)
│   ├── __init__.py
│   ├── test_data_manager.py  # 测试数据管理
│   ├── login_data.json    # 登录测试数据
│   └── activity_data.json # 活动申请测试数据
├── page_objects/           # 页面对象层 (PO Layer)
│   ├── __init__.py
│   ├── login_page.py      # 登录页面
│   ├── register_page.py   # 注册页面
│   ├── activity_page.py   # 活动申请页面
│   ├── material_page.py   # 物料管理页面
│   ├── salary_page.py     # 薪资管理页面
│   └── finance_page.py    # 活动财务页面
├── tests/                  # 测试用例层 (Test Layer)
│   ├── __init__.py
│   ├── conftest.py        # Pytest配置和Fixture
│   ├── test_login.py      # 登录测试
│   ├── test_activity.py   # 活动申请测试
│   ├── test_material.py   # 物料管理测试
│   ├── test_salary.py     # 薪资管理测试
│   └── test_finance.py    # 活动财务测试
├── utils/                  # 工具层 (Utils Layer)
│   ├── __init__.py
│   └── helpers.py         # 辅助工具类
├── logs/                   # 日志目录
├── reports/                # 测试报告目录
│   ├── allure-results/    # Allure结果
│   └── allure-report/     # Allure报告
├── screenshots/            # 截图目录
├── driver/                 # 浏览器驱动目录
├── pytest.ini             # Pytest配置
├── requirements.txt       # 依赖包
├── run_tests.py          # 测试运行脚本
└── README.md             # 项目说明
```

## 4层架构说明

### 1. Base层（基础封装层）
- **base_page.py**: 封装所有页面通用的Selenium操作
  - 元素查找（显式等待）
  - 元素交互（点击、输入、清除）
  - 页面导航
  - 截图功能
  - JavaScript执行
  
- **driver_manager.py**: WebDriver管理
  - 浏览器驱动创建
  - 浏览器配置
  - 驱动生命周期管理

### 2. Config层（配置层）
- **settings.py**: 集中管理所有配置
  - 项目路径
  - URL配置
  - 浏览器配置
  - 超时配置
  - 日志配置
  
- **logger_config.py**: 日志配置管理
  - 统一的日志格式
  - 文件和控制台输出

### 3. Data层（数据层）
- **test_data_manager.py**: 测试数据管理
  - 支持JSON、Excel、CSV格式
  - 数据读取和写入
  - 各类测试数据获取方法
  
- **数据文件**: 存储测试数据
  - login_data.json: 登录测试数据
  - activity_data.json: 活动申请测试数据

### 4. PO层（页面对象层）
每个页面对应一个类，包含：
- 页面URL
- 元素定位器
- 页面操作方法
- 业务逻辑封装

主要页面：
- LoginPage: 登录页面
- RegisterPage: 注册页面
- ActivityPage: 活动申请页面
- MaterialPage: 物料管理页面
- SalaryPage: 薪资管理页面
- FinancePage: 活动财务页面

### 5. Test层（测试用例层）
- 使用Pytest框架
- 支持参数化测试
- Allure报告集成
- 测试标记分类

## 环境要求

- Python 3.8+
- Chrome/Firefox/Edge浏览器
- Java 8+ (用于Allure报告)

## 安装依赖

```bash
pip install -r requirements.txt
```

## 运行测试

### 1. 运行所有测试
```bash
python run_tests.py
```

### 2. 运行特定测试文件
```bash
python run_tests.py -p tests/test_login.py
```

### 3. 使用特定浏览器
```bash
python run_tests.py -b firefox
```

### 4. 无头模式运行
```bash
python run_tests.py --headless
```

### 5. 并行执行
```bash
python run_tests.py --parallel -w 4
```

### 6. 运行特定标记的测试
```bash
python run_tests.py -m smoke
```

### 7. 直接运行pytest
```bash
# 运行所有测试
pytest tests/ -v

# 运行登录测试
pytest tests/test_login.py -v

# 生成HTML报告
pytest tests/ --html=reports/report.html

# 生成Allure报告
pytest tests/ --alluredir=reports/allure-results
allure generate reports/allure-results -o reports/allure-report --clean
```

## 查看报告

### HTML报告
测试完成后，报告位于 `reports/test_report.html`

### Allure报告
```bash
# 生成并启动Allure服务
python run_tests.py --serve

# 或手动启动
allure serve reports/allure-results
```

## 测试数据参数化

框架支持多种参数化方式：

### 1. 代码内参数化
```python
@pytest.mark.parametrize("test_data", [
    {"username": "user1", "password": "123456", "roleId": "1"},
    {"username": "admin", "password": "123456", "roleId": "2"},
])
def test_login(self, driver, test_data):
    # 测试代码
```

### 2. 从JSON文件读取
```python
def test_login_from_file(self, driver, test_data):
    login_cases = test_data.get_login_data()
    for case in login_cases:
        # 测试代码
```

### 3. 从Excel读取
```python
def test_from_excel(self, driver, test_data):
    cases = test_data.load_excel("test_data.xlsx")
    # 测试代码
```

## 配置说明

### 修改配置
编辑 `config/settings.py` 文件：

```python
# 修改基础URL
BASE_URL = "http://localhost:8080/EventPlanning_Management"

# 修改浏览器
BROWSER = "chrome"

# 修改超时时间
IMPLICIT_WAIT = 10
EXPLICIT_WAIT = 20
```

### 命令行参数
```bash
# 修改基础URL
pytest --base-url=http://example.com

# 修改浏览器
pytest --browser=firefox

# 启用无头模式
pytest --headless
```

## 添加新测试

### 1. 创建页面对象（如需要）
在 `page_objects/` 目录下创建新的页面类

### 2. 创建测试文件
在 `tests/` 目录下创建 `test_xxx.py`

### 3. 编写测试用例
```python
import pytest
import allure
from page_objects.xxx_page import XxxPage

@allure.feature("功能名称")
class TestXxx:
    @allure.title("测试用例标题")
    def test_xxx(self, driver):
        with allure.step("步骤1"):
            page = XxxPage(driver)
            page.open()
        
        with allure.step("步骤2"):
            page.do_something()
        
        with allure.step("验证结果"):
            assert page.is_successful()
```

## 日志查看

- 控制台日志：实时显示
- 文件日志：`logs/test_YYYYMMDD.log`

## 截图查看

- 失败截图：`screenshots/failures/`
- 手动截图：调用 `base_page.take_screenshot()`

## 常见问题

### 1. 浏览器驱动问题
框架使用 `webdriver-manager` 自动管理驱动，如需手动指定：
```python
# 在 config/settings.py 中设置
DRIVER_DIR = Path("/path/to/driver")
```

### 2. 元素定位失败
- 增加等待时间
- 检查元素是否在iframe中
- 检查元素是否被动态加载

### 3. 测试不稳定
- 使用显式等待代替time.sleep
- 添加重试机制
- 检查网络延迟

## 贡献指南

1. 遵循PEP 8编码规范
2. 添加适当的注释和文档字符串
3. 使用类型提示
4. 编写测试用例时添加Allure注解

## 许可证

MIT License
