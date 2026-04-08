import pytest
from playwright.sync_api import sync_playwright


@pytest.fixture(scope="session")
def browser():
    """
    会话级别的浏览器 fixture
    在整个测试会话期间只启动一次浏览器
    """
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=False, slow_mo=100)
        yield browser
        browser.close()


@pytest.fixture(scope="function")
def page(browser):
    """
    函数级别的页面 fixture
    每个测试函数都会创建一个新的页面
    """
    context = browser.new_context(viewport={"width": 1280, "height": 720})
    page = context.new_page()
    yield page
    context.close()


@pytest.fixture(scope="function")
def page_with_video(browser):
    """
    带录屏功能的页面 fixture
    用于需要录制测试过程的场景
    """
    context = browser.new_context(
        viewport={"width": 1280, "height": 720},
        record_video_dir="videos/"
    )
    page = context.new_page()
    yield page
    context.close()


@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """
    自定义测试报告钩子
    在测试失败时自动截图
    """
    outcome = yield
    report = outcome.get_result()
    
    # 只在测试失败时截图
    if report.when == "call" and report.failed:
        try:
            page = item.funcargs.get("page")
            if page:
                screenshot_path = f"screenshots/{item.name}.png"
                page.screenshot(path=screenshot_path, full_page=True)
                print(f"\n测试失败截图已保存: {screenshot_path}")
        except Exception as e:
            print(f"截图失败: {e}")


def pytest_configure(config):
    """
    配置 pytest
    """
    # 添加自定义标记
    config.addinivalue_line("markers", "smoke: 冒烟测试")
    config.addinivalue_line("markers", "login: 登录相关测试")
    config.addinivalue_line("markers", "activity: 活动申请相关测试")
    config.addinivalue_line("markers", "ui: UI 界面测试")
    config.addinivalue_line("markers", "api: 接口测试")
    config.addinivalue_line("markers", "slow: 慢速测试")
