# -*- coding: utf-8 -*-
"""
薪资管理功能测试 - Test Layer
测试薪资管理功能的各种场景
"""
import pytest
import allure
from page_objects.salary_page import SalaryPage
from page_objects.login_page import LoginPage


@allure.feature("薪资管理功能")
@allure.story("薪资管理")
class TestSalary:
    """
    薪资管理功能测试类
    """
    
    @allure.title("测试薪资管理页面加载")
    @allure.description("验证薪资管理页面能够正常加载")
    def test_salary_page_load(self, driver):
        """
        测试薪资管理页面加载
        """
        # 先登录（会计）
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开薪资管理页面"):
            salary_page = SalaryPage(driver)
            salary_page.open()
        
        with allure.step("验证页面元素"):
            assert salary_page.is_element_present(salary_page.SALARY_TABLE), "薪资表格未找到"
            assert salary_page.is_element_present(salary_page.CALCULATE_BUTTON), "计算按钮未找到"
    
    @allure.title("测试搜索薪资记录")
    @allure.description("根据员工姓名搜索薪资记录")
    def test_search_salary(self, driver):
        """
        测试搜索薪资记录
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开薪资管理页面"):
            salary_page = SalaryPage(driver)
            salary_page.open()
        
        with allure.step("搜索薪资记录"):
            salary_page.search_by_employee_name("员工")
        
        with allure.step("验证搜索结果"):
            count = salary_page.get_salary_count()
            allure.attach(f"搜索结果数量: {count}", "搜索统计")
            assert count >= 0, "搜索失败"
    
    @allure.title("测试查看薪资统计")
    @allure.description("查看薪资统计信息")
    def test_view_salary_statistics(self, driver):
        """
        测试查看薪资统计
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开薪资管理页面"):
            salary_page = SalaryPage(driver)
            salary_page.open()
        
        with allure.step("查看统计信息"):
            stats = salary_page.get_statistics()
            allure.attach(f"统计信息: {stats}", "薪资统计")
            assert stats is not None, "获取统计信息失败"
    
    @allure.title("测试计算本月薪资")
    @allure.description("计算本月员工薪资")
    def test_calculate_salary(self, driver):
        """
        测试计算本月薪资
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开薪资管理页面"):
            salary_page = SalaryPage(driver)
            salary_page.open()
        
        with allure.step("点击计算本月薪资"):
            salary_page.click_calculate()
        
        with allure.step("验证计算结果"):
            import time
            time.sleep(1)
            # 验证页面是否还在薪资页面
            assert "salary" in driver.current_url, "计算后页面异常"
    
    @allure.title("测试按状态筛选")
    @allure.description("按发放状态筛选薪资记录")
    @pytest.mark.parametrize("status", ["0", "1"], ids=["待发放", "已发放"])
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
        
        with allure.step("打开薪资管理页面"):
            salary_page = SalaryPage(driver)
            salary_page.open()
        
        with allure.step(f"筛选状态: {'已发放' if status == '1' else '待发放'}"):
            salary_page.search_by_status(status)
        
        with allure.step("验证筛选结果"):
            count = salary_page.get_salary_count()
            allure.attach(f"筛选结果数量: {count}", "筛选统计")
            assert count >= 0, "筛选失败"


@allure.feature("薪资管理功能")
@allure.story("薪资发放")
class TestSalaryPayment:
    """
    薪资发放测试类
    """
    
    @allure.title("测试发放单个员工薪资")
    @allure.description("发放单个员工的薪资")
    def test_pay_single_salary(self, driver):
        """
        测试发放单个薪资
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开薪资管理页面"):
            salary_page = SalaryPage(driver)
            salary_page.open()
        
        with allure.step("筛选待发放记录"):
            salary_page.search_by_status("0")
        
        with allure.step("发放薪资"):
            if salary_page.get_salary_count() > 0:
                salary_page.click_pay(0)
                # 处理确认弹窗
                salary_page.confirm_pay()
            else:
                pytest.skip("没有待发放的薪资记录")
        
        with allure.step("验证发放结果"):
            import time
            time.sleep(1)
            assert "salary" in driver.current_url, "发放后页面异常"
    
    @allure.title("测试批量发放薪资")
    @allure.description("批量发放员工薪资")
    def test_batch_pay_salary(self, driver):
        """
        测试批量发放薪资
        """
        # 先登录
        with allure.step("会计登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("accountant", "123456", "4")
            import time
            time.sleep(1)
        
        with allure.step("打开薪资管理页面"):
            salary_page = SalaryPage(driver)
            salary_page.open()
        
        with allure.step("点击批量发放"):
            salary_page.click_batch_pay()
        
        with allure.step("验证批量发放结果"):
            import time
            time.sleep(1)
            assert "salary" in driver.current_url, "批量发放后页面异常"
