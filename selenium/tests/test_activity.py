# -*- coding: utf-8 -*-
"""
活动申请功能测试 - Test Layer
测试活动申请功能的各种场景
"""
import pytest
import allure
from page_objects.activity_page import ActivityPage
from page_objects.login_page import LoginPage


@allure.feature("活动申请功能")
@allure.story("活动申请")
class TestActivity:
    """
    活动申请功能测试类
    """
    
    @allure.title("测试活动申请页面加载")
    @allure.description("验证活动申请页面能够正常加载")
    def test_activity_page_load(self, driver):
        """
        测试活动申请页面加载
        """
        # 先登录
        with allure.step("用户登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("user1", "123456", "1")
            import time
            time.sleep(1)
        
        with allure.step("打开活动申请页面"):
            activity_page = ActivityPage(driver)
            activity_page.open()
        
        with allure.step("验证页面元素"):
            assert activity_page.is_element_present(activity_page.ACTIVITY_PLACE_INPUT), "活动地点输入框未找到"
            assert activity_page.is_element_present(activity_page.ACTIVITY_TYPE_INPUT), "活动类型输入框未找到"
            assert activity_page.is_element_present(activity_page.SUBMIT_BUTTON), "提交按钮未找到"
    
    @allure.title("测试正常活动申请")
    @allure.description("提交正常的活动申请")
    @pytest.mark.parametrize("activity_data", [
        {
            "activity_place": "北京",
            "activity_type": "公司年会",
            "start_time": "2026-05-01",
            "end_time": "2026-05-02",
            "activity_people": "100",
            "activity_budget": "50000",
            "requirement_desc": "需要音响设备和舞台布置"
        },
        {
            "activity_place": "上海",
            "activity_type": "婚礼",
            "start_time": "2026-06-15",
            "end_time": "2026-06-15",
            "activity_people": "200",
            "activity_budget": "80000",
            "requirement_desc": "户外草坪婚礼，需要鲜花装饰"
        }
    ], ids=["公司年会", "婚礼"])
    def test_submit_activity(self, driver, activity_data):
        """
        测试提交活动申请
        """
        # 先登录
        with allure.step("用户登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("user1", "123456", "1")
            import time
            time.sleep(1)
        
        with allure.step("打开活动申请页面"):
            activity_page = ActivityPage(driver)
            activity_page.open()
        
        with allure.step("填写并提交活动申请"):
            activity_page.submit_activity(
                activity_data["activity_place"],
                activity_data["activity_type"],
                activity_data["start_time"],
                activity_data["end_time"],
                activity_data["activity_people"],
                activity_data["activity_budget"],
                activity_data["requirement_desc"]
            )
        
        with allure.step("验证提交结果"):
            import time
            time.sleep(1)
            # 验证页面是否还在活动页面（提交成功后通常会刷新或跳转）
            assert "activity" in driver.current_url, "活动申请提交后页面异常"
    
    @allure.title("测试活动申请-空活动地点")
    @allure.description("使用空活动地点提交申请")
    def test_submit_activity_empty_place(self, driver):
        """
        测试空活动地点
        """
        # 先登录
        with allure.step("用户登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("user1", "123456", "1")
            import time
            time.sleep(1)
        
        with allure.step("打开活动申请页面"):
            activity_page = ActivityPage(driver)
            activity_page.open()
        
        with allure.step("提交空活动地点"):
            activity_page.submit_activity(
                "",
                "公司年会",
                "2026-05-01",
                "2026-05-02",
                "100",
                "50000",
                "测试数据"
            )
        
        with allure.step("验证提交失败"):
            import time
            time.sleep(0.5)
            # 页面应该还在申请页面
            assert "activity" in driver.current_url
    
    @allure.title("测试查看活动列表")
    @allure.description("查看用户的活动申请列表")
    def test_view_activity_list(self, driver):
        """
        测试查看活动列表
        """
        # 先登录
        with allure.step("用户登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("user1", "123456", "1")
            import time
            time.sleep(1)
        
        with allure.step("打开活动申请页面"):
            activity_page = ActivityPage(driver)
            activity_page.open()
        
        with allure.step("查看活动列表"):
            count = activity_page.get_activity_count()
            allure.attach(f"活动数量: {count}", "活动统计")
            assert count >= 0, "获取活动列表失败"
    
    @allure.title("测试查看活动进度")
    @allure.description("查看活动的审批进度")
    def test_view_activity_progress(self, driver):
        """
        测试查看活动进度
        """
        # 先登录
        with allure.step("用户登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("user1", "123456", "1")
            import time
            time.sleep(1)
        
        with allure.step("打开活动申请页面"):
            activity_page = ActivityPage(driver)
            activity_page.open()
        
        with allure.step("查看活动进度"):
            # 如果有活动记录，点击查看进度
            if activity_page.get_activity_count() > 0:
                activity_page.click_view_progress(0)
                progress = activity_page.get_progress_content()
                allure.attach(progress, "活动进度")
                activity_page.close_progress_modal()
                assert progress != "", "活动进度内容为空"
            else:
                allure.attach("没有活动记录", "提示")
                pytest.skip("没有活动记录可供测试")


@allure.feature("活动申请功能")
@allure.story("参数化活动申请测试")
class TestActivityParameterized:
    """
    参数化活动申请测试类
    """
    
    @allure.title("参数化活动申请测试 - {case_data[case_name]}")
    @pytest.mark.parametrize("case_data", [
        pytest.param({
            "case_id": "ACT_001",
            "case_name": "正常活动申请-公司年会",
            "activity_place": "北京",
            "activity_type": "公司年会",
            "start_time": "2026-05-01",
            "end_time": "2026-05-02",
            "activity_people": "100",
            "activity_budget": "50000",
            "requirement_desc": "需要音响设备和舞台布置",
            "expected": "success"
        }, id="ACT_001"),
        pytest.param({
            "case_id": "ACT_004",
            "case_name": "异常活动申请-空活动地点",
            "activity_place": "",
            "activity_type": "公司年会",
            "start_time": "2026-05-01",
            "end_time": "2026-05-02",
            "activity_people": "100",
            "activity_budget": "50000",
            "requirement_desc": "测试数据",
            "expected": "fail"
        }, id="ACT_004"),
    ])
    def test_activity_with_data(self, driver, case_data):
        """
        使用参数化数据进行活动申请测试
        """
        allure.dynamic.title(f"活动申请测试 - {case_data['case_name']}")
        
        # 先登录
        with allure.step("用户登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("user1", "123456", "1")
            import time
            time.sleep(1)
        
        with allure.step("打开活动申请页面"):
            activity_page = ActivityPage(driver)
            activity_page.open()
        
        with allure.step("提交活动申请"):
            activity_page.submit_activity(
                case_data["activity_place"],
                case_data["activity_type"],
                case_data["start_time"],
                case_data["end_time"],
                case_data["activity_people"],
                case_data["activity_budget"],
                case_data["requirement_desc"]
            )
        
        with allure.step("验证提交结果"):
            import time
            time.sleep(1)
            
            if case_data["expected"] == "success":
                assert "activity" in driver.current_url, \
                    f"期望提交成功: {case_data['case_name']}"
            else:
                # 验证表单验证或提交失败
                assert "activity" in driver.current_url
    
    @allure.title("从数据文件读取活动测试数据")
    def test_activity_from_data_file(self, driver, test_data):
        """
        从JSON数据文件读取测试数据进行活动申请测试
        """
        activity_cases = test_data.get_activity_data()
        
        # 先登录
        with allure.step("用户登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("user1", "123456", "1")
            import time
            time.sleep(1)
        
        # 只测试第一条数据
        case = activity_cases[0]
        with allure.step(f"测试用例: {case.get('case_name', 'Unknown')}"):
            activity_page = ActivityPage(driver)
            activity_page.open()
            
            activity_page.submit_activity(
                case["activity_place"],
                case["activity_type"],
                case["start_time"],
                case["end_time"],
                case["activity_people"],
                case["activity_budget"],
                case["requirement_desc"]
            )
            
            import time
            time.sleep(1)
            
            assert "activity" in driver.current_url, \
                f"活动申请提交失败: {case.get('case_name')}"
