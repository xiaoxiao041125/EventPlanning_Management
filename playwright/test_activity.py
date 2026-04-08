import pytest
import json
from playwright.sync_api import Page, expect


# 加载活动申请测试数据
def load_activity_test_data():
    """从 JSON 文件加载活动申请测试数据"""
    with open("test_activity_data.json", "r", encoding="utf-8") as f:
        return json.load(f)


# 加载测试数据
ACTIVITY_TEST_DATA = load_activity_test_data()


@pytest.fixture(scope="function")
def logged_in_page(page: Page):
    """
    fixture: 提供已登录的页面（客户账号）
    """
    page.goto("http://localhost:8080/EventPlanning_Management/html/index.html")
    page.get_by_role("button", name="登录").click()
    page.wait_for_selector("#loginModal", state="visible")
    page.locator("#modalUsername").fill("user_lisi")
    page.locator("#modalPassword").fill("lisi@456")
    page.locator("#loginRole").select_option("customer")
    
    # 处理登录弹窗
    page.once("dialog", lambda dialog: dialog.dismiss())
    page.locator("#loginBtn").click()
    page.wait_for_timeout(1000)
    
    yield page


@pytest.mark.parametrize("test_data", ACTIVITY_TEST_DATA, ids=lambda x: f"{x['id']}_{x['description']}")
def test_activity_application(logged_in_page: Page, test_data: dict):
    """
    活动申请功能参数化测试
    
    Args:
        logged_in_page: 已登录的 Playwright page 对象
        test_data: 测试数据字典
    """
    page = logged_in_page
    
    print(f"\n执行测试: {test_data['id']} - {test_data['description']}")
    
    # 导航到方案制定页面
    page.get_by_role("link", name="方案制定").click()
    page.wait_for_timeout(500)
    
    # 清空并填写表单
    # 活动地点
    if test_data.get("activity_place"):
        page.get_by_label("活动地点").select_option(test_data["activity_place"])
    
    # 活动类型
    if test_data.get("activity_type"):
        page.get_by_label("活动类型").select_option(test_data["activity_type"])
    
    # 起始时间
    if test_data.get("start_time"):
        page.get_by_label("起始时间").fill(test_data["start_time"])
    
    # 结束时间
    if test_data.get("end_time"):
        page.get_by_label("结束时间").fill(test_data["end_time"])
    
    # 活动人数
    if test_data.get("activity_people"):
        page.get_by_label("活动人数").select_option(test_data["activity_people"])
    
    # 活动预算
    if test_data.get("activity_budget"):
        page.get_by_label("活动预算").select_option(test_data["activity_budget"])
    
    # 手机号码
    if test_data.get("phone"):
        page.get_by_placeholder("请输入手机号码").fill(test_data["phone"])
    
    # 您的称呼
    if test_data.get("name"):
        page.get_by_label("您的称呼").fill(test_data["name"])
    
    # 需求描述
    if test_data.get("requirement_desc"):
        page.get_by_placeholder("请输入您的活动需求描述（非必填）").fill(test_data["requirement_desc"])
    
    # 处理弹窗提示
    dialog_messages = []
    def handle_dialog(dialog):
        dialog_messages.append(dialog.message)
        dialog.dismiss()
    
    page.on("dialog", handle_dialog)
    
    # 点击活动申请按钮
    page.get_by_role("button", name="活动申请").click()
    
    # 等待响应
    page.wait_for_timeout(1000)
    
    # 判断预期结果
    is_normal_case = "正常" in test_data.get("description", "")
    is_boundary_case = "边界" in test_data.get("description", "")
    
    if is_normal_case or is_boundary_case:
        # 预期成功的情况
        # 验证是否有成功提示
        success_keywords = ["成功", "提交成功", "申请成功", "success"]
        has_success = any(
            any(keyword in msg for keyword in success_keywords)
            for msg in dialog_messages
        )
        
        if has_success:
            assert True, f"活动申请成功: {dialog_messages}"
        else:
            # 如果没有成功提示，检查是否跳转到了成功页面
            try:
                page.wait_for_selector("text=申请成功", timeout=3000)
                assert True, "检测到申请成功页面"
            except:
                pytest.fail(f"预期申请成功，但未检测到成功标志。弹窗消息: {dialog_messages}")
    else:
        # 预期失败的情况（异常测试用例）
        # 验证是否有错误提示弹窗
        assert len(dialog_messages) > 0, "预期申请失败并显示错误提示，但未检测到弹窗"
        
        # 验证错误提示内容
        error_keywords = ["错误", "失败", "不能为空", "格式不正确", "invalid", "error", "请填写", "请检查"]
        has_error = any(
            any(keyword in msg for keyword in error_keywords)
            for msg in dialog_messages
        )
        
        # 对于某些特殊情况，如时间错误，也可能有特定提示
        time_error_keywords = ["时间", "结束时间", "起始时间"]
        has_time_error = any(
            any(keyword in msg for keyword in time_error_keywords)
            for msg in dialog_messages
        )
        
        assert has_error or has_time_error, f"预期显示错误提示，但实际弹窗消息为: {dialog_messages}"
    
    print(f"✅ 测试通过: {test_data['id']}")


def test_activity_form_elements(logged_in_page: Page):
    """
    测试活动申请表单元素完整性
    """
    page = logged_in_page
    
    # 导航到方案制定页面
    page.get_by_role("link", name="方案制定").click()
    page.wait_for_timeout(500)
    
    # 验证表单元素存在
    expect(page.get_by_label("活动地点")).to_be_visible()
    expect(page.get_by_label("活动类型")).to_be_visible()
    expect(page.get_by_label("起始时间")).to_be_visible()
    expect(page.get_by_label("结束时间")).to_be_visible()
    expect(page.get_by_label("活动人数")).to_be_visible()
    expect(page.get_by_label("活动预算")).to_be_visible()
    expect(page.get_by_placeholder("请输入手机号码")).to_be_visible()
    expect(page.get_by_label("您的称呼")).to_be_visible()
    expect(page.get_by_role("button", name="活动申请")).to_be_visible()
    
    print("✅ 活动申请表单元素验证通过")


def test_activity_progress_page(logged_in_page: Page):
    """
    测试方案进度页面功能
    """
    page = logged_in_page
    
    # 导航到首页
    page.get_by_role("link", name="首页").click()
    page.wait_for_timeout(500)
    
    # 点击方案进度按钮
    page.get_by_role("button", name="方案进度").click()
    page.wait_for_timeout(500)
    
    # 点击刷新报表按钮
    page.get_by_role("button", name="刷新报表").click()
    page.wait_for_timeout(1000)
    
    # 验证页面是否正常加载（检查是否有表格或数据展示区域）
    # 这里可以根据实际页面结构调整选择器
    try:
        # 尝试查找表格或数据容器
        page.wait_for_selector("table, .progress-table, .data-container", timeout=5000)
        assert True, "方案进度页面加载成功"
    except:
        # 如果没有找到特定元素，检查页面是否包含相关文本
        page_content = page.content()
        assert "进度" in page_content or "方案" in page_content, "方案进度页面内容验证失败"
    
    print("✅ 方案进度页面测试通过")


def test_time_validation(logged_in_page: Page):
    """
    专门测试时间验证逻辑
    """
    page = logged_in_page
    
    # 导航到方案制定页面
    page.get_by_role("link", name="方案制定").click()
    page.wait_for_timeout(500)
    
    # 测试结束时间早于起始时间
    page.get_by_label("起始时间").fill("2026-05-10")
    page.get_by_label("结束时间").fill("2026-05-05")
    
    # 填写其他必填项
    page.get_by_label("活动地点").select_option("武汉")
    page.get_by_label("活动类型").select_option("公司年会")
    page.get_by_label("活动人数").select_option("10-50")
    page.get_by_label("活动预算").select_option("5-10万")
    page.get_by_placeholder("请输入手机号码").fill("13800138000")
    page.get_by_label("您的称呼").fill("张三")
    
    # 处理弹窗
    dialog_messages = []
    def handle_dialog(dialog):
        dialog_messages.append(dialog.message)
        dialog.dismiss()
    
    page.on("dialog", handle_dialog)
    
    # 提交表单
    page.get_by_role("button", name="活动申请").click()
    page.wait_for_timeout(1000)
    
    # 验证是否有时间错误提示
    assert len(dialog_messages) > 0, "时间验证应显示错误提示"
    
    time_error = any("时间" in msg or "结束" in msg or "起始" in msg for msg in dialog_messages)
    assert time_error, f"预期显示时间错误提示，但实际消息为: {dialog_messages}"
    
    print("✅ 时间验证测试通过")


def test_phone_validation(logged_in_page: Page):
    """
    专门测试手机号验证逻辑
    """
    page = logged_in_page
    
    # 导航到方案制定页面
    page.get_by_role("link", name="方案制定").click()
    page.wait_for_timeout(500)
    
    invalid_phones = [
        ("138001380", "手机号长度不足"),
        ("138001380000", "手机号长度过长"),
        ("", "空手机号"),
        ("abcdefghijk", "非数字手机号")
    ]
    
    for phone, desc in invalid_phones:
        # 刷新页面
        page.reload()
        page.wait_for_timeout(500)
        page.get_by_role("link", name="方案制定").click()
        page.wait_for_timeout(500)
        
        # 填写必填项
        page.get_by_label("活动地点").select_option("武汉")
        page.get_by_label("活动类型").select_option("公司年会")
        page.get_by_label("起始时间").fill("2026-05-01")
        page.get_by_label("结束时间").fill("2026-05-02")
        page.get_by_label("活动人数").select_option("10-50")
        page.get_by_label("活动预算").select_option("5-10万")
        page.get_by_label("您的称呼").fill("张三")
        
        if phone:
            page.get_by_placeholder("请输入手机号码").fill(phone)
        
        # 处理弹窗
        dialog_messages = []
        def handle_dialog(dialog):
            dialog_messages.append(dialog.message)
            dialog.dismiss()
        
        page.on("dialog", handle_dialog)
        
        # 提交表单
        page.get_by_role("button", name="活动申请").click()
        page.wait_for_timeout(1000)
        
        # 验证错误提示
        assert len(dialog_messages) > 0, f"{desc}应显示错误提示"
        
        phone_error = any("手机" in msg or "号码" in msg or "格式" in msg for msg in dialog_messages)
        assert phone_error, f"{desc}: 预期显示手机号错误提示，但实际消息为: {dialog_messages}"
    
    print("✅ 手机号验证测试通过")
