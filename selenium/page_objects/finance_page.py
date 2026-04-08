# -*- coding: utf-8 -*-
"""
活动财务页面 - Page Object Layer
封装活动财务页面的元素定位和操作方法
"""
from selenium.webdriver.common.by import By
from base.base_page import BasePage
from config.settings import settings


class FinancePage(BasePage):
    """
    活动财务页面类
    """
    
    # 页面URL
    URL = settings.get_url("html/accountant/finance.html")
    
    # ==================== 元素定位器 ====================
    # 搜索区域
    ACTIVITY_NAME_SEARCH = (By.ID, "activityName")
    STATUS_SEARCH = (By.ID, "status")
    SEARCH_BUTTON = (By.ID, "searchBtn")
    RESET_BUTTON = (By.ID, "resetBtn")
    
    # 统计信息
    TOTAL_INCOME = (By.ID, "totalIncome")
    TOTAL_EXPENSE = (By.ID, "totalExpense")
    TOTAL_PROFIT = (By.ID, "totalProfit")
    
    # 财务列表
    FINANCE_TABLE = (By.ID, "financeTable")
    FINANCE_ROWS = (By.CSS_SELECTOR, "#financeTable tbody tr")
    
    # 操作按钮
    VERIFY_BUTTON = (By.CSS_SELECTOR, ".verify-btn")
    VIEW_DETAILS_BUTTON = (By.CSS_SELECTOR, ".view-details-btn")
    
    # 分页
    PAGINATION = (By.ID, "pagination")
    
    def __init__(self, driver):
        """
        初始化活动财务页面
        :param driver: WebDriver实例
        """
        super().__init__(driver)
    
    def open(self):
        """
        打开活动财务页面
        :return: self
        """
        self.open_url(self.URL)
        self.wait_for_page_load()
        return self
    
    def search_by_activity_name(self, name: str):
        """
        按活动名称搜索
        :param name: 活动名称
        :return: self
        """
        self.input_text(self.ACTIVITY_NAME_SEARCH, name)
        self.click(self.SEARCH_BUTTON)
        self.wait_for_page_load()
        return self
    
    def search_by_status(self, status: str):
        """
        按状态搜索
        :param status: 状态
        :return: self
        """
        self.select_dropdown_by_visible_text(self.STATUS_SEARCH, status)
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
    
    def click_verify(self, index: int = 0):
        """
        点击收入确认按钮
        :param index: 记录索引
        :return: self
        """
        buttons = self.find_elements(self.VERIFY_BUTTON)
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
    
    def get_finance_count(self) -> int:
        """
        获取财务记录数量
        :return: 记录数量
        """
        rows = self.find_elements(self.FINANCE_ROWS)
        return len(rows)
    
    def get_statistics(self) -> dict:
        """
        获取统计信息
        :return: 统计信息字典
        """
        stats = {}
        try:
            stats['total_income'] = self.get_text(self.TOTAL_INCOME)
            stats['total_expense'] = self.get_text(self.TOTAL_EXPENSE)
            stats['total_profit'] = self.get_text(self.TOTAL_PROFIT)
        except:
            pass
        return stats
    
    def get_first_record_status(self) -> str:
        """
        获取第一条记录的状态
        :return: 状态文本
        """
        try:
            status_cell = (By.CSS_SELECTOR, "#financeTable tbody tr:first-child td:nth-child(5)")
            return self.get_text(status_cell)
        except:
            return ""
    
    def get_first_record_profit(self) -> str:
        """
        获取第一条记录的利润
        :return: 利润文本
        """
        try:
            profit_cell = (By.CSS_SELECTOR, "#financeTable tbody tr:first-child td:nth-child(6)")
            return self.get_text(profit_cell)
        except:
            return ""
    
    def confirm_verify(self):
        """
        确认收入（处理确认弹窗）
        :return: self
        """
        self.accept_alert()
        return self
    
    def is_verify_successful(self) -> bool:
        """
        判断收入确认是否成功
        :return: 是否成功
        """
        alert = self.get_alert_text()
        return "成功" in alert or "success" in alert.lower()
