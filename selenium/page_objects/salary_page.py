# -*- coding: utf-8 -*-
"""
薪资管理页面 - Page Object Layer
封装薪资管理页面的元素定位和操作方法
"""
from selenium.webdriver.common.by import By
from base.base_page import BasePage
from config.settings import settings


class SalaryPage(BasePage):
    """
    薪资管理页面类
    """
    
    # 页面URL
    URL = settings.get_url("html/accountant/salary.html")
    
    # ==================== 元素定位器 ====================
    # 搜索区域
    EMPLOYEE_NAME_SEARCH = (By.ID, "employeeName")
    MONTH_SEARCH = (By.ID, "month")
    STATUS_SEARCH = (By.ID, "status")
    SEARCH_BUTTON = (By.ID, "searchBtn")
    RESET_BUTTON = (By.ID, "resetBtn")
    
    # 操作按钮
    CALCULATE_BUTTON = (By.ID, "calculateBtn")
    BATCH_PAY_BUTTON = (By.ID, "batchPayBtn")
    
    # 统计信息
    STATISTICS_PANEL = (By.ID, "statisticsPanel")
    TOTAL_EMPLOYEES = (By.ID, "totalEmployees")
    PAID_COUNT = (By.ID, "paidCount")
    PENDING_COUNT = (By.ID, "pendingCount")
    TOTAL_SALARY = (By.ID, "totalSalary")
    PAID_AMOUNT = (By.ID, "paidAmount")
    PENDING_AMOUNT = (By.ID, "pendingAmount")
    
    # 薪资列表
    SALARY_TABLE = (By.ID, "salaryTable")
    SALARY_ROWS = (By.CSS_SELECTOR, "#salaryTable tbody tr")
    
    # 操作按钮
    PAY_BUTTON = (By.CSS_SELECTOR, ".pay-btn")
    VIEW_DETAILS_BUTTON = (By.CSS_SELECTOR, ".view-details-btn")
    
    # 分页
    PAGINATION = (By.ID, "pagination")
    
    def __init__(self, driver):
        """
        初始化薪资管理页面
        :param driver: WebDriver实例
        """
        super().__init__(driver)
    
    def open(self):
        """
        打开薪资管理页面
        :return: self
        """
        self.open_url(self.URL)
        self.wait_for_page_load()
        return self
    
    def search_by_employee_name(self, name: str):
        """
        按员工姓名搜索
        :param name: 员工姓名
        :return: self
        """
        self.input_text(self.EMPLOYEE_NAME_SEARCH, name)
        self.click(self.SEARCH_BUTTON)
        self.wait_for_page_load()
        return self
    
    def search_by_month(self, month: str):
        """
        按月份搜索
        :param month: 月份 (格式: YYYY-MM)
        :return: self
        """
        self.input_text(self.MONTH_SEARCH, month)
        self.click(self.SEARCH_BUTTON)
        self.wait_for_page_load()
        return self
    
    def search_by_status(self, status: str):
        """
        按状态搜索
        :param status: 状态 (0-待发放, 1-已发放)
        :return: self
        """
        status_text = "已发放" if status == "1" else "待发放"
        self.select_dropdown_by_visible_text(self.STATUS_SEARCH, status_text)
        self.click(self.SEARCH_BUTTON)
        self.wait_for_page_load()
        return self
    
    def click_reset(self):
        """
        点击重置按钮
        :return: self
        """
        self.click(self.RESET_BUTTON)
        return self
    
    def click_calculate(self):
        """
        点击计算本月薪资按钮
        :return: self
        """
        self.click(self.CALCULATE_BUTTON)
        return self
    
    def click_batch_pay(self):
        """
        点击批量发放按钮
        :return: self
        """
        self.click(self.BATCH_PAY_BUTTON)
        return self
    
    def click_pay(self, index: int = 0):
        """
        点击发放薪资按钮
        :param index: 记录索引
        :return: self
        """
        buttons = self.find_elements(self.PAY_BUTTON)
        if index < len(buttons):
            buttons[index].click()
        return self
    
    def click_view_details(self, index: int = 0):
        """
        点击查看详情按钮
        :param index: 记录索引
        :return: self
        """
        buttons = self.find_elements(self.VIEW_DETAILS_BUTTON)
        if index < len(buttons):
            buttons[index].click()
        return self
    
    def get_salary_count(self) -> int:
        """
        获取薪资记录数量
        :return: 记录数量
        """
        rows = self.find_elements(self.SALARY_ROWS)
        return len(rows)
    
    def get_statistics(self) -> dict:
        """
        获取统计信息
        :return: 统计信息字典
        """
        stats = {}
        try:
            stats['total_employees'] = self.get_text(self.TOTAL_EMPLOYEES)
            stats['paid_count'] = self.get_text(self.PAID_COUNT)
            stats['pending_count'] = self.get_text(self.PENDING_COUNT)
            stats['total_salary'] = self.get_text(self.TOTAL_SALARY)
            stats['paid_amount'] = self.get_text(self.PAID_AMOUNT)
            stats['pending_amount'] = self.get_text(self.PENDING_AMOUNT)
        except:
            pass
        return stats
    
    def get_first_record_status(self) -> str:
        """
        获取第一条记录的状态
        :return: 状态文本
        """
        try:
            status_cell = (By.CSS_SELECTOR, "#salaryTable tbody tr:first-child td:last-child")
            return self.get_text(status_cell)
        except:
            return ""
    
    def confirm_pay(self):
        """
        确认发放（处理确认弹窗）
        :return: self
        """
        self.accept_alert()
        return self
    
    def is_calculate_successful(self) -> bool:
        """
        判断计算是否成功
        :return: 是否成功
        """
        alert = self.get_alert_text()
        return "成功" in alert or "success" in alert.lower()
