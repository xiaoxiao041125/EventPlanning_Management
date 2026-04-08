import pytest
import json
from playwright.sync_api import Page, expect


# 从 JSON 文件加载登录测试数据
def load_login_test_data():
    """从 test_login_data.json 加载登录测试数据"""
    with open("test_login_data.json", "r", encoding="utf-8") as f:
        data = json.load(f)
    return data


# 加载测试数据
LOGIN_TEST_DATA = load_login_test_data()


# 使用 pytest 参数化装饰器，从 JSON 文件读取数据
@pytest.mark.parametrize("test_data", LOGIN_TEST_DATA, ids=lambda x: f"{x['id']}_{x['description']}")
def test_login(page: Page, test_data: dict):
    """
    登录功能参数化测试
    
    Args:
        page: Playwright page 对象
        test_data: 测试数据字典（从 JSON 文件加载）
    """
    print(f"\n执行测试: {test_data['id']} - {test_data['description']}")
    
    # 访问登录页面
    page.goto("http://localhost:8080/EventPlanning_Management/html/index.html")
    
    # 点击登录按钮打开弹窗
    page.get_by_role("button", name="登录").click()
    
    # 等待弹窗出现
    page.wait_for_selector("#loginModal", state="visible", timeout=5000)
    
    # 填写用户名
    if test_data.get("username"):
        page.locator("#modalUsername").fill(test_data["username"])
    
    # 填写密码
    if test_data.get("password"):
        page.locator("#modalPassword").fill(test_data["password"])
    
    # 选择角色 - JSON 中是 roleId，页面中是 role
    if test_data.get("roleId"):
        # 根据 roleId 选择对应的角色值
        # 页面选项: <option value="admin">管理员</option>
        #          <option value="user">普通用户</option>
        role_mapping = {
            "1": "admin",      # 管理员
            "2": "user"        # 普通用户
        }
        role_value = role_mapping.get(test_data["roleId"], test_data["roleId"])
        page.locator("#loginRole").select_option(role_value)
    
    # 处理弹窗提示
    dialog_messages = []
    def handle_dialog(dialog):
        dialog_messages.append(dialog.message)
        dialog.dismiss()
    
    page.on("dialog", handle_dialog)
    
    # 点击登录按钮 - 使用多种方式尝试
    try:
        # 方式1: 通过 ID
        page.locator("#loginBtn").click()
    except:
        try:
            # 方式2: 通过 role 和 name
            page.get_by_role("button", name="登录").click()
        except:
            # 方式3: 通过 CSS 选择器（模态框内的登录按钮）
            page.locator("#loginModal button.btn-primary").click()
    
    # 等待响应
    page.wait_for_timeout(1500)
    
    # 根据 description 判断预期结果
    is_normal_case = "正常" in test_data.get("description", "")
    
    if is_normal_case:
        # 预期成功的情况
        try:
            # 检查是否登录成功（通过检查页面是否包含特定元素）
            page.wait_for_selector("text=退出", timeout=5000)
            assert True, "登录成功"
        except:
            # 如果没有找到退出按钮，检查是否有成功提示
            if any("成功" in msg or "welcome" in msg.lower() for msg in dialog_messages):
                assert True, "登录成功 - 检测到成功提示"
            else:
                pytest.fail(f"预期登录成功，但未检测到成功标志。弹窗消息: {dialog_messages}")
    else:
        # 预期失败的情况
        # 验证是否有错误提示弹窗
        assert len(dialog_messages) > 0, "预期登录失败并显示错误提示，但未检测到弹窗"
        
        # 验证错误提示内容
        error_keywords = ["错误", "失败", "不能为空", "incorrect", "invalid", "失败", "禁用", "不存在", "封禁"]
        has_error = any(
            any(keyword in msg for keyword in error_keywords)
            for msg in dialog_messages
        )
        assert has_error, f"预期显示错误提示，但实际弹窗消息为: {dialog_messages}"
    
    print(f"✅ 测试通过: {test_data['id']}")


@pytest.fixture(scope="function")
def logged_in_page(page: Page):
    """
    fixture: 提供已登录的页面
    """
    page.goto("http://localhost:8080/EventPlanning_Management/html/index.html")
    page.get_by_role("button", name="登录").click()
    page.wait_for_selector("#loginModal", state="visible")
    page.locator("#modalUsername").fill("admin")
    page.locator("#modalPassword").fill("admin@123")
    # 使用正确的角色值 admin
    page.locator("#loginRole").select_option("admin")
    
    # 处理登录弹窗
    page.once("dialog", lambda dialog: dialog.dismiss())
    page.locator("#loginBtn").click()
    page.wait_for_timeout(1000)
    
    yield page


def test_logout(logged_in_page: Page):
    """
    测试退出登录功能
    """
    page = logged_in_page
    
    # 点击退出按钮
    page.get_by_role("link", name="退出").click()
    
    # 验证是否返回到登录状态
    page.wait_for_timeout(1000)
    
    # 检查登录按钮是否重新出现
    login_button = page.get_by_role("button", name="登录")
    expect(login_button).to_be_visible()
    
    print("✅ 退出登录测试通过")


def test_login_modal_validation(page: Page):
    """
    测试登录弹窗表单验证
    """
    page.goto("http://localhost:8080/EventPlanning_Management/html/index.html")
    
    # 打开登录弹窗
    page.get_by_role("button", name="登录").click()
    page.wait_for_selector("#loginModal", state="visible")
    
    # 验证弹窗中的表单元素存在
    expect(page.locator("#modalUsername")).to_be_visible()
    expect(page.locator("#modalPassword")).to_be_visible()
    expect(page.locator("#loginRole")).to_be_visible()
    expect(page.locator("#loginBtn")).to_be_visible()
    
    # 验证角色选项 - 根据实际页面选项
    role_options = page.locator("#loginRole").evaluate("el => Array.from(el.options).map(o => o.value)")
    assert "admin" in role_options, "角色选项应包含 admin"
    assert "user" in role_options, "角色选项应包含 user"
    
    print("✅ 登录弹窗验证测试通过")
