# -*- coding: utf-8 -*-
"""
登录功能测试 - Test Layer
测试登录功能的各种场景
"""
import pytest
import allure
from page_objects.login_page import LoginPage
from config.settings import settings


def handle_login_alert(login_page, expected_success=True):
    """
    处理登录后的Alert弹窗
    :param login_page: 登录页面对象
    :param expected_success: 是否期望登录成功
    :return: Alert文本
    """
    import time
    time.sleep(0.5)
    alert_text = login_page.accept_alert()
    allure.attach(f"Alert文本: {alert_text}", "弹窗信息")
    return alert_text


@allure.feature("登录功能")
@allure.story("用户登录")
class TestLogin:
    """
    登录功能测试类
    """

    @allure.title("测试登录页面加载")
    @allure.description("验证登录页面能够正常加载")
    def test_login_page_load(self, driver):
        """
        测试登录页面加载
        """
        with allure.step("打开登录页面"):
            login_page = LoginPage(driver)
            login_page.open()

        with allure.step("验证页面元素"):
            assert login_page.is_at_login_page(), "登录页面加载失败"
            assert login_page.is_element_present(login_page.USERNAME_INPUT), "用户名输入框未找到"
            assert login_page.is_element_present(login_page.PASSWORD_INPUT), "密码输入框未找到"
            assert login_page.is_element_present(login_page.LOGIN_BUTTON), "登录按钮未找到"

    @allure.title("测试有效用户登录")
    @allure.description("使用有效的用户名和密码登录")
    @pytest.mark.parametrize("test_data", [
        {"username": "user_lisi", "password": "lisi@456", "roleId": "1"},
        {"username": "admin", "password": "admin@123", "roleId": "2"},
    ], ids=["普通用户", "员工"])
    def test_valid_login(self, driver, test_data):
        """
        测试有效用户登录
        """
        with allure.step("打开登录页面"):
            login_page = LoginPage(driver)
            login_page.open()

        with allure.step(f"执行登录: {test_data['username']}"):
            login_page.login(
                test_data["username"],
                test_data["password"],
                test_data["roleId"]
            )

        with allure.step("验证登录成功"):
            alert_text = handle_login_alert(login_page)
            assert "登录成功" in alert_text or "成功" in alert_text, f"登录未成功，Alert: {alert_text}"

    @allure.title("测试无效用户登录-错误密码")
    @allure.description("使用错误的密码登录")
    def test_invalid_login_wrong_password(self, driver):
        """
        测试错误密码登录
        """
        with allure.step("打开登录页面"):
            login_page = LoginPage(driver)
            login_page.open()

        with allure.step("使用错误密码登录"):
            login_page.login("user_lisi", "wrong_password", "1")

        with allure.step("验证登录失败"):
            alert_text = handle_login_alert(login_page)
            assert "密码错误" in alert_text or "错误" in alert_text, f"未提示密码错误，Alert: {alert_text}"

    @allure.title("测试无效用户登录-不存在的用户")
    @allure.description("使用不存在的用户名登录")
    def test_invalid_login_nonexistent_user(self, driver):
        """
        测试不存在的用户登录
        """
        with allure.step("打开登录页面"):
            login_page = LoginPage(driver)
            login_page.open()

        with allure.step("使用不存在的用户登录"):
            login_page.login("nonexistent_user", "123456", "1")

        with allure.step("验证登录失败"):
            alert_text = handle_login_alert(login_page)
            assert "用户名不存在" in alert_text or "不存在" in alert_text, f"未提示用户不存在，Alert: {alert_text}"

    @allure.title("测试空用户名登录")
    @allure.description("使用空用户名登录")
    def test_empty_username(self, driver):
        """
        测试空用户名登录
        """
        with allure.step("打开登录页面"):
            login_page = LoginPage(driver)
            login_page.open()

        with allure.step("使用空用户名登录"):
            login_page.login("", "123456", "1")

        with allure.step("验证登录失败"):
            alert_text = handle_login_alert(login_page)
            assert "请填写" in alert_text or "必填" in alert_text, f"未提示填写必填项，Alert: {alert_text}"

    @allure.title("测试空密码登录")
    @allure.description("使用空密码登录")
    def test_empty_password(self, driver):
        """
        测试空密码登录
        """
        with allure.step("打开登录页面"):
            login_page = LoginPage(driver)
            login_page.open()

        with allure.step("使用空密码登录"):
            login_page.login("user_lisi", "", "1")

        with allure.step("验证登录失败"):
            alert_text = handle_login_alert(login_page)
            assert "请填写" in alert_text or "必填" in alert_text, f"未提示填写必填项，Alert: {alert_text}"


@allure.feature("登录功能")
@allure.story("参数化登录测试")
class TestLoginParameterized:
    """
    参数化登录测试类
    从数据文件读取测试数据进行登录测试
    """

    @allure.title("参数化登录测试 - {case_data[case_name]}")
    @pytest.mark.parametrize("case_data", [
        pytest.param({
            "case_id": "LOGIN_001",
            "case_name": "有效用户登录-普通用户",
            "username": "user_lisi",
            "password": "lisi@456",
            "roleId": "1",
            "expected": "success"
        }, id="LOGIN_001"),
        pytest.param({
            "case_id": "LOGIN_002",
            "case_name": "有效用户登录-员工",
            "username": "admin",
            "password": "admin@123",
            "roleId": "2",
            "expected": "success"
        }, id="LOGIN_002"),
        pytest.param({
            "case_id": "LOGIN_003",
            "case_name": "无效用户登录-错误密码",
            "username": "user_lisi",
            "password": "wrong_password",
            "roleId": "1",
            "expected": "fail"
        }, id="LOGIN_003"),
        pytest.param({
            "case_id": "LOGIN_004",
            "case_name": "无效用户登录-不存在的用户",
            "username": "nonexistent_user",
            "password": "123456",
            "roleId": "1",
            "expected": "fail"
        }, id="LOGIN_004"),
    ])
    def test_login_with_data(self, driver, case_data):
        """
        使用参数化数据进行登录测试
        """
        allure.dynamic.title(f"登录测试 - {case_data['case_name']}")

        with allure.step("打开登录页面"):
            login_page = LoginPage(driver)
            login_page.open()

        with allure.step(f"执行登录: {case_data['username']}"):
            login_page.login(
                case_data["username"],
                case_data["password"],
                case_data["roleId"]
            )

        with allure.step("验证登录结果"):
            alert_text = handle_login_alert(login_page)

            if case_data["expected"] == "success":
                assert "登录成功" in alert_text or "成功" in alert_text, \
                    f"期望登录成功，但失败: {case_data['case_name']}, Alert: {alert_text}"
            else:
                assert "登录成功" not in alert_text, \
                    f"期望登录失败，但成功了: {case_data['case_name']}, Alert: {alert_text}"

    @allure.title("从数据文件读取登录测试数据")
    def test_login_from_data_file(self, driver, test_data):
        """
        从JSON数据文件读取测试数据进行登录测试
        获取Alert弹窗文本并与expected进行断言
        """
        login_cases = test_data.get_login_data()

        for i, case in enumerate(login_cases):
            with allure.step(f"测试用例 {i+1}: {case.get('case_name', 'Unknown')}"):
                # 每次测试用例都重新打开浏览器，避免登录状态影响
                driver.get(settings.LOGIN_URL)
                login_page = LoginPage(driver)
                login_page.wait_for_page_load()

                # 等待页面完全加载
                import time
                time.sleep(1)

                # 直接用JS显示登录模态框，确保表单可见
                driver.execute_script("""
                    var modal = document.getElementById('loginModal');
                    if(modal) {
                        modal.style.display = 'block';
                        modal.classList.add('show');
                    }
                """)
                time.sleep(0.5)

                login_page.login(
                    case["username"],
                    case["password"],
                    case["roleId"]
                )

                import time
                time.sleep(0.5)

                with allure.step("获取Alert弹窗文本并断言"):
                    alert_text = login_page.accept_alert()
                    expected_text = case.get("expected", "")

                    allure.attach(f"实际Alert: {alert_text}", "实际Alert文本")
                    allure.attach(f"期望Alert: {expected_text}", "期望Alert文本")

                    # 使用包含匹配，因为系统Alert文本可能与预期略有差异（如标点符号）
                    if expected_text:
                        assert expected_text.replace("！", "").replace("!", "") in alert_text.replace("！", "").replace("!", "") or \
                               alert_text.replace("！", "").replace("!", "") in expected_text.replace("！", "").replace("!", ""), \
                            f"Alert文本不匹配! 期望: '{expected_text}', 实际: '{alert_text}'"
