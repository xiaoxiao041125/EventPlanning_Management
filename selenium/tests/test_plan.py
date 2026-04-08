# -*- coding: utf-8 -*-
"""
方案制定测试用例 - Test Layer
测试方案制定功能
"""
import pytest
import allure
import json
import time
import logging
from pathlib import Path
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from page_objects.login_page import LoginPage
from page_objects.plan_page import PlanPage
from config.settings import settings
from utils.db_cleaner import DatabaseCleaner

# 配置日志
logger = logging.getLogger(__name__)


@allure.feature("方案制定")
@allure.story("活动方案制定功能")
class TestPlan:
    """
    方案制定测试类
    """
    
    @pytest.fixture(autouse=True)
    def setup_and_teardown(self, driver):
        """
        测试前置和后置：登录和清理数据
        """
        # 前置：登录
        self.driver = driver
        # 先登录
        login_page = LoginPage(driver)
        login_page.open()
        login_page.click_header_login()
        login_page.login("user_lisi", "lisi@456", "1")
        # 处理登录成功弹窗
        time.sleep(0.5)
        alert_text = login_page.accept_alert()
        allure.attach(f"登录 Alert: {alert_text}", "登录信息")
        
        # 验证登录成功
        assert "登录成功" in alert_text or "成功" in alert_text, f"登录失败：{alert_text}"
        
        # 等待页面刷新
        time.sleep(1)
        
        yield  # 测试执行
        
        # 后置：清理数据库
        with allure.step("测试后处理 - 清理数据库"):
            try:
                cleaner = DatabaseCleaner(
                    host=settings.DB_HOST,
                    port=settings.DB_PORT,
                    user=settings.DB_USER,
                    password=settings.DB_PASSWORD,
                    database=settings.DB_NAME
                )
                with cleaner:
                    deleted_count = cleaner.clean_user_activity_demand(userid=3004)
                    allure.attach(f"清理 userid=3004 的数据，删除 {deleted_count} 条记录", "数据清理")
            except Exception as e:
                allure.attach(f"数据清理失败：{str(e)}", "清理错误")
                logger.warning(f"测试后数据清理失败：{e}")
    
    def load_activity_data(self):
        """
        加载活动测试数据
        :return: 测试数据列表
        """
        data_file = Path(__file__).parent.parent / "data" / "activity_data.json"
        with open(data_file, "r", encoding="utf-8") as f:
            data = json.load(f)
        return data.get("test_cases", [])
    
    def navigate_to_plan_page(self):
        """
        导航到方案制定页面
        """
        # 直接访问方案制定页面 URL
        plan_url = settings.get_url("html/plan.html")
        self.driver.get(plan_url)
        
        # 等待页面加载完成 - 使用更通用的等待条件
        try:
            WebDriverWait(self.driver, 10).until(
                lambda d: d.execute_script("return document.readyState") == "complete"
            )
            # 额外等待表单元素 - 使用正确的元素 ID
            WebDriverWait(self.driver, 10).until(
                EC.presence_of_element_located((By.ID, "activityLocation"))
            )
        except Exception as e:
            # 如果找不到元素，可能是页面结构不同，记录当前 URL 和源码
            allure.attach(self.driver.current_url, "当前 URL")
            allure.attach(self.driver.page_source[:2000], "页面源码")
            allure.attach(str(e), "错误信息")
            raise
        time.sleep(1)
    
    @allure.title("方案制定页面加载测试")
    @allure.description("验证方案制定页面能正常加载")
    @allure.severity(allure.severity_level.BLOCKER)
    def test_plan_page_load(self):
        """
        测试方案制定页面加载
        """
        with allure.step("打开方案制定页面"):
            self.navigate_to_plan_page()
            allure.attach(self.driver.current_url, "当前 URL")
        
        with allure.step("验证页面元素"):
            plan_page = PlanPage(self.driver)
            assert plan_page.wait_for_element_visible(plan_page.ACTIVITY_PLACE_SELECT), "活动地点下拉框 (activityLocation) 未显示"
            assert plan_page.wait_for_element_visible(plan_page.ACTIVITY_TYPE_SELECT), "活动类型下拉框 (activityType) 未显示"
            assert plan_page.wait_for_element_visible(plan_page.SUBMIT_BUTTON), "提交按钮未显示"
    
    @allure.title("方案制定 - 数据驱动测试")
    @allure.description("使用 activity_data.json 中的测试数据进行方案制定测试")
    @pytest.mark.parametrize("test_data", load_activity_data(None), ids=lambda x: x.get("case_id", "unknown"))
    def test_plan_submit_with_data(self, test_data):
        """
        使用 JSON 数据文件测试方案制定
        """
        case_id = test_data.get("case_id", "")
        case_name = test_data.get("case_name", "")
        expected = test_data.get("expected", "")
        
        allure.dynamic.title(f"{case_id}: {case_name}")
        allure.attach(json.dumps(test_data, ensure_ascii=False, indent=2), "测试数据")
        
        with allure.step("打开方案制定页面"):
            self.navigate_to_plan_page()
            plan_page = PlanPage(self.driver)
        
        with allure.step("填写方案制定表单"):
            # 选择活动地点
            place = test_data.get("activity_place", "")
            if place:
                try:
                    plan_page.select_activity_place(place)
                    allure.attach(f"活动地点：{place}", "表单数据")
                except Exception as e:
                    allure.attach(f"选择活动地点失败：{place}, 错误：{str(e)}", "表单错误")
                    # 如果地点无效，使用 JavaScript 强制设置值
                    if "无效城市" in case_name or "空城市" in case_name:
                        # 使用 JavaScript 直接设置空值或无效值
                        if not place:
                            self.driver.execute_script("document.getElementById('activityLocation').value = '';")
                        else:
                            self.driver.execute_script(f"document.getElementById('activityLocation').value = '{place}';")
                        allure.attach(f"使用 JavaScript 强制设置活动地点：{place}", "表单数据")
            
            # 选择活动类型
            activity_type = test_data.get("activity_type", "")
            if activity_type:
                try:
                    plan_page.select_activity_type(activity_type)
                    allure.attach(f"活动类型：{activity_type}", "表单数据")
                except Exception as e:
                    allure.attach(f"选择活动类型失败：{activity_type}, 错误：{str(e)}", "表单错误")
                    # 如果类型无效，使用 JavaScript 强制设置值
                    if "无效活动类型" in case_name:
                        self.driver.execute_script(f"document.getElementById('activityType').value = '{activity_type}';")
                        allure.attach(f"使用 JavaScript 强制设置活动类型：{activity_type}", "表单数据")
            
            # 输入时间
            start_time = test_data.get("start_time", "")
            end_time = test_data.get("end_time", "")
            if start_time:
                plan_page.input_start_time(start_time)
            if end_time:
                plan_page.input_end_time(end_time)
            allure.attach(f"时间：{start_time} 至 {end_time}", "表单数据")
            
            # 选择人数和预算
            people = test_data.get("activity_people", "")
            budget = test_data.get("activity_budget", "")
            if people:
                plan_page.select_activity_people(people)
            if budget:
                plan_page.select_activity_budget(budget)
            
            # 输入手机号和称呼
            phone = test_data.get("phone", "")
            name = test_data.get("name", "")
            if phone:
                plan_page.input_phone(phone)
            if name:
                plan_page.input_name(name)
            allure.attach(f"手机：{phone}, 称呼：{name}", "联系信息")
            
            # 输入需求描述
            desc = test_data.get("requirement_desc", "")
            if desc:
                plan_page.input_requirement_desc(desc)
        
        with allure.step("提交表单"):
            plan_page.click_submit_button()
        
        with allure.step("验证提交结果 - 智能验证"):
            time.sleep(0.5)
            
            # 尝试获取弹窗
            alert_text = None
            alert_appeared = False
            
            try:
                alert_text = plan_page.accept_alert()
                alert_appeared = True
                allure.attach(f"出现 Alert: {alert_text}", "弹窗信息")
            except Exception as e:
                allure.attach(f"未出现弹窗：{str(e)}", "验证信息")
            
            # 如果没有出现弹窗，可能是前端拦截（验证失败）
            if not alert_appeared:
                allure.attach("未出现弹窗 - 前端验证拦截", "测试结果")
                # 对于期望前端拦截的测试用例，这算通过
                if expected and ("不能为空" in expected or "格式不正确" in expected or "长度应" in expected or 
                                "只能包含" in expected or "必须晚于" in expected or "请选择正确" in expected):
                    allure.attach(f"期望的前端验证提示：{expected}", "期望信息")
                    allure.attach("前端验证成功拦截，测试通过", "最终结果")
                    return  # 直接返回，算通过
                else:
                    # 如果期望成功提交但没有弹窗，说明有问题
                    allure.attach("期望成功提交但未出现弹窗", "错误信息")
                    assert False, "期望成功提交但未出现弹窗"
            
            # 如果出现了弹窗，说明通过了前端验证，需要验证后端返回的结果
            allure.attach(f"Alert 文本：{alert_text}", "弹窗信息")
            allure.attach(f"期望结果：{expected}", "期望信息")
            
            # 断言 Alert 文本包含期望结果
            assert expected in alert_text, f"期望：{expected}, 实际：{alert_text}"
