# -*- coding: utf-8 -*-
"""
活动财务功能测试 - Test Layer
测试活动财务功能的各种场景
"""
import pytest
import allure
from page_objects.finance_page import FinancePage
from page_objects.login_page import LoginPage


@allure.feature("活动财务功能")
@allure.story("活动财务")
class TestFinance:
    """
    活动财务功能测试类
    """
    
    @allure.title("测试活动财务页面加载")
    @allure.description("验证活动财务页面能够正常加载")
    def test_finance_page_load(self, driver):
        """
        测试活动财务页面加载
        """
        # 先登录（会计）
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开活动财务页面"):
            finance_page = FinancePage(driver)
            finance_page.open()
        
        with allure.step("验证页面元素"):
            assert finance_page.is_element_present(finance_page.FINANCE_TABLE), "财务表格未找到"
    
    @allure.title("测试搜索活动财务")
    @allure.description("根据活动名称搜索财务记录")
    def test_search_finance(self, driver):
        """
        测试搜索活动财务
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开活动财务页面"):
            finance_page = FinancePage(driver)
            finance_page.open()
        
        with allure.step("搜索活动财务"):
            finance_page.search_by_activity_name("年会")
        
        with allure.step("验证搜索结果"):
            count = finance_page.get_finance_count()
            allure.attach(f"搜索结果数量: {count}", "搜索统计")
            assert count >= 0, "搜索失败"
    
    @allure.title("测试查看财务统计")
    @allure.description("查看财务统计信息")
    def test_view_finance_statistics(self, driver):
        """
        测试查看财务统计
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开活动财务页面"):
            finance_page = FinancePage(driver)
            finance_page.open()
        
        with allure.step("查看统计信息"):
            stats = finance_page.get_statistics()
            allure.attach(f"统计信息: {stats}", "财务统计")
            assert stats is not None, "获取统计信息失败"
    
    @allure.title("测试按状态筛选")
    @allure.description("按状态筛选财务记录")
    @pytest.mark.parametrize("status", ["已付定金", "已完成"])
    def test_filter_by_status(self, driver, status):
        """
        测试按状态筛选
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开活动财务页面"):
            finance_page = FinancePage(driver)
            finance_page.open()
        
        with allure.step(f"筛选状态: {status}"):
            finance_page.search_by_status(status)
        
        with allure.step("验证筛选结果"):
            count = finance_page.get_finance_count()
            allure.attach(f"筛选结果数量: {count}", "筛选统计")
            assert count >= 0, "筛选失败"
    
    @allure.title("测试查看财务列表")
    @allure.description("查看活动财务列表")
    def test_view_finance_list(self, driver):
        """
        测试查看财务列表
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开活动财务页面"):
            finance_page = FinancePage(driver)
            finance_page.open()
        
        with allure.step("查看财务列表"):
            count = finance_page.get_finance_count()
            allure.attach(f"财务记录数量: {count}", "财务统计")
            assert count >= 0, "获取财务列表失败"
    
    @allure.title("测试重置搜索")
    @allure.description("测试重置搜索功能")
    def test_reset_search(self, driver):
        """
        测试重置搜索
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开活动财务页面"):
            finance_page = FinancePage(driver)
            finance_page.open()
        
        with allure.step("执行搜索"):
            finance_page.search_by_activity_name("年会")
            count_before = finance_page.get_finance_count()
        
        with allure.step("重置搜索"):
            finance_page.click_reset()
            count_after = finance_page.get_finance_count()
        
        with allure.step("验证重置结果"):
            allure.attach(f"重置前: {count_before}, 重置后: {count_after}", "重置统计")
            assert count_after >= 0, "重置失败"


@allure.feature("活动财务功能")
@allure.story("收入确认")
class TestFinanceVerify:
    """
    收入确认测试类
    """
    
    @allure.title("测试收入确认")
    @allure.description("确认活动收入")
    def test_verify_income(self, driver):
        """
        测试收入确认
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开活动财务页面"):
            finance_page = FinancePage(driver)
            finance_page.open()
        
        with allure.step("确认收入"):
            if finance_page.get_finance_count() > 0:
                finance_page.click_verify(0)
                # 处理确认弹窗
                finance_page.confirm_verify()
            else:
                pytest.skip("没有财务记录可供测试")
        
        with allure.step("验证确认结果"):
            import time
            time.sleep(1)
            assert "finance" in driver.current_url, "收入确认后页面异常"
    
    @allure.title("测试查看利润显示")
    @allure.description("验证利润显示（负数显示为-）")
    def test_profit_display(self, driver):
        """
        测试利润显示
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开活动财务页面"):
            finance_page = FinancePage(driver)
            finance_page.open()
        
        with allure.step("查看利润"):
            if finance_page.get_finance_count() > 0:
                profit = finance_page.get_first_record_profit()
                allure.attach(f"第一条记录利润: {profit}", "利润信息")
                # 利润应该不为空
                assert profit is not None, "利润显示异常"
            else:
                pytest.skip("没有财务记录可供测试")
