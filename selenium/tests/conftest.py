# -*- coding: utf-8 -*-
"""
Pytest配置文件
定义fixture和钩子函数
"""
import pytest
import logging
from pathlib import Path
from datetime import datetime

from base.driver_manager import DriverManager
from config.settings import settings
from config.logger_config import setup_logging


# 初始化日志配置
def pytest_configure(config):
    """
    Pytest配置钩子
    """
    setup_logging()


def pytest_sessionstart(session):
    """
    测试会话开始时执行
    清除上次的 Allure 结果
    """
    import shutil
    allure_results_dir = Path("reports/allure-results")
    if allure_results_dir.exists():
        shutil.rmtree(allure_results_dir)
        allure_results_dir.mkdir(parents=True, exist_ok=True)
        print(f"\n[Allure] 已清除上次的测试结果: {allure_results_dir}\n")


@pytest.fixture(scope="session")
def driver_manager():
    """
    会话级别的浏览器驱动管理器
    :return: DriverManager实例
    """
    manager = DriverManager()
    yield manager
    # 会话结束时清理


@pytest.fixture(scope="function")
def driver(driver_manager):
    """
    函数级别的WebDriver实例
    每个测试函数都会创建新的浏览器实例
    :return: WebDriver实例
    """
    driver = driver_manager.create_driver()
    driver.maximize_window()
    yield driver
    # 测试结束后关闭浏览器
    driver_manager.quit_driver()


@pytest.fixture(scope="function")
def logged_in_driver(driver_manager, request):
    """
    已登录的WebDriver实例
    用于需要登录状态的测试
    :return: (driver, user_info) 元组
    """
    driver = driver_manager.create_driver()
    driver.maximize_window()
    
    # 获取测试用例标记中的登录信息
    marker = request.node.get_closest_marker("login")
    if marker:
        username = marker.kwargs.get("username", "user1")
        password = marker.kwargs.get("password", "123456")
        role_id = marker.kwargs.get("role_id", "1")
    else:
        username = "user1"
        password = "123456"
        role_id = "1"
    
    # 执行登录
    from page_objects.login_page import LoginPage
    login_page = LoginPage(driver)
    login_page.open()
    login_page.login(username, password, role_id)
    
    # 等待登录完成
    import time
    time.sleep(1)
    
    yield driver, {"username": username, "role_id": role_id}
    
    # 测试结束后关闭浏览器
    driver_manager.quit_driver()


@pytest.fixture(scope="function")
def screenshot_on_failure(request, driver):
    """
    测试失败时自动截图
    """
    yield
    # 测试结束后检查是否失败
    if request.node.rep_call.failed:
        # 创建截图目录
        screenshot_dir = settings.SCREENSHOT_DIR / "failures"
        screenshot_dir.mkdir(parents=True, exist_ok=True)
        
        # 生成截图文件名
        test_name = request.node.name
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        screenshot_path = screenshot_dir / f"{test_name}_{timestamp}.png"
        
        # 截图
        screenshot_path_str = str(screenshot_path)
        driver.save_screenshot(screenshot_path_str)
        logging.getLogger(__name__).info(f"测试失败截图已保存: {screenshot_path_str}")


@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """
    测试报告钩子
    用于获取测试结果
    """
    outcome = yield
    rep = outcome.get_result()
    
    # 将测试结果附加到测试节点
    if rep.when == "call":
        item.rep_call = rep


def pytest_addoption(parser):
    """
    添加命令行选项
    """
    parser.addoption(
        "--browser",
        action="store",
        default="chrome",
        help="选择浏览器: chrome, firefox, edge"
    )
    parser.addoption(
        "--headless",
        action="store_true",
        default=False,
        help="启用无头模式"
    )
    parser.addoption(
        "--base-url",
        action="store",
        default=None,
        help="设置基础URL"
    )


@pytest.fixture(scope="session", autouse=True)
def configure_environment(pytestconfig):
    """
    根据命令行参数配置环境
    """
    browser = pytestconfig.getoption("--browser")
    headless = pytestconfig.getoption("--headless")
    base_url = pytestconfig.getoption("--base-url")
    
    if browser:
        settings.BROWSER = browser
    if headless:
        settings.HEADLESS = True
    if base_url:
        settings.BASE_URL = base_url
        settings.LOGIN_URL = f"{base_url}/html/index.html"
        settings.HOME_URL = f"{base_url}/html/index.html"


@pytest.fixture(scope="function")
def test_data():
    """
    测试数据fixture
    :return: TestDataManager实例
    """
    from data.test_data_manager import data_manager
    return data_manager
