# -*- coding: utf-8 -*-
"""
活动申请页面 - Page Object Layer
封装活动申请页面的元素定位和操作方法
"""
from selenium.webdriver.common.by import By
from base.base_page import BasePage
from config.settings import settings


class ActivityPage(BasePage):
    """
    活动申请页面类
    """
    
    # 页面URL
    URL = settings.get_url("html/user/activity.html")
    
    # ==================== 元素定位器 ====================
    # 活动申请表单
    ACTIVITY_PLACE_INPUT = (By.ID, "activityPlace")
    ACTIVITY_TYPE_INPUT = (By.ID, "activityType")
    START_TIME_INPUT = (By.ID, "startTime")
    END_TIME_INPUT = (By.ID, "endTime")
    ACTIVITY_PEOPLE_INPUT = (By.ID, "activityPeople")
    ACTIVITY_BUDGET_INPUT = (By.ID, "activityBudget")
    REQUIREMENT_DESC_INPUT = (By.ID, "requirementDesc")
    SUBMIT_BUTTON = (By.ID, "submitBtn")
    RESET_BUTTON = (By.ID, "resetBtn")
    
    # 活动列表
    ACTIVITY_TABLE = (By.ID, "activityTable")
    ACTIVITY_ROWS = (By.CSS_SELECTOR, "#activityTable tbody tr")
    
    # 查看进度按钮
    VIEW_PROGRESS_BUTTON = (By.CSS_SELECTOR, ".view-progress-btn")
    
    # 进度弹窗
    PROGRESS_MODAL = (By.ID, "progressModal")
    PROGRESS_CONTENT = (By.ID, "progressContent")
    CLOSE_MODAL_BUTTON = (By.CSS_SELECTOR, "#progressModal .close")
    
    def __init__(self, driver):
        """
        初始化活动申请页面
        :param driver: WebDriver实例
        """
        super().__init__(driver)
    
    def open(self):
        """
        打开活动申请页面
        :return: self
        """
        self.open_url(self.URL)
        self.wait_for_page_load()
        return self
    
    def input_activity_place(self, place: str):
        """
        输入活动地点
        :param place: 地点
        :return: self
        """
        self.input_text(self.ACTIVITY_PLACE_INPUT, place)
        return self
    
    def input_activity_type(self, activity_type: str):
        """
        输入活动类型
        :param activity_type: 活动类型
        :return: self
        """
        self.input_text(self.ACTIVITY_TYPE_INPUT, activity_type)
        return self
    
    def input_start_time(self, start_time: str):
        """
        输入开始时间
        :param start_time: 开始时间 (格式: YYYY-MM-DD)
        :return: self
        """
        self.input_text(self.START_TIME_INPUT, start_time)
        return self
    
    def input_end_time(self, end_time: str):
        """
        输入结束时间
        :param end_time: 结束时间 (格式: YYYY-MM-DD)
        :return: self
        """
        self.input_text(self.END_TIME_INPUT, end_time)
        return self
    
    def input_activity_people(self, people: str):
        """
        输入活动人数
        :param people: 人数
        :return: self
        """
        self.input_text(self.ACTIVITY_PEOPLE_INPUT, people)
        return self
    
    def input_activity_budget(self, budget: str):
        """
        输入活动预算
        :param budget: 预算
        :return: self
        """
        self.input_text(self.ACTIVITY_BUDGET_INPUT, budget)
        return self
    
    def input_requirement_desc(self, desc: str):
        """
        输入需求描述
        :param desc: 需求描述
        :return: self
        """
        self.input_text(self.REQUIREMENT_DESC_INPUT, desc)
        return self
    
    def click_submit_button(self):
        """
        点击提交按钮
        :return: self
        """
        self.click(self.SUBMIT_BUTTON)
        return self
    
    def click_reset_button(self):
        """
        点击重置按钮
        :return: self
        """
        self.click(self.RESET_BUTTON)
        return self
    
    def submit_activity(self, place: str, activity_type: str, start_time: str,
                       end_time: str, people: str, budget: str, desc: str):
        """
        提交活动申请
        :param place: 地点
        :param activity_type: 活动类型
        :param start_time: 开始时间
        :param end_time: 结束时间
        :param people: 人数
        :param budget: 预算
        :param desc: 需求描述
        :return: self
        """
        self.input_activity_place(place)
        self.input_activity_type(activity_type)
        self.input_start_time(start_time)
        self.input_end_time(end_time)
        self.input_activity_people(people)
        self.input_activity_budget(budget)
        self.input_requirement_desc(desc)
        self.click_submit_button()
        return self
    
    def get_activity_count(self) -> int:
        """
        获取活动列表数量
        :return: 活动数量
        """
        rows = self.find_elements(self.ACTIVITY_ROWS)
        return len(rows)
    
    def click_view_progress(self, index: int = 0):
        """
        点击查看进度按钮
        :param index: 活动索引
        :return: self
        """
        buttons = self.find_elements(self.VIEW_PROGRESS_BUTTON)
        if index < len(buttons):
            buttons[index].click()
            self.wait_for_element_visible(self.PROGRESS_MODAL)
        return self
    
    def get_progress_content(self) -> str:
        """
        获取进度弹窗内容
        :return: 进度内容文本
        """
        return self.get_text(self.PROGRESS_CONTENT)
    
    def close_progress_modal(self):
        """
        关闭进度弹窗
        :return: self
        """
        self.click(self.CLOSE_MODAL_BUTTON)
        self.wait_for_element_invisible(self.PROGRESS_MODAL)
        return self
    
    def is_submit_successful(self) -> bool:
        """
        判断提交是否成功
        :return: 是否成功
        """
        alert = self.get_alert_text()
        return "成功" in alert or "success" in alert.lower()
    
    def get_first_activity_status(self) -> str:
        """
        获取第一个活动的状态
        :return: 状态文本
        """
        try:
            status_cell = (By.CSS_SELECTOR, "#activityTable tbody tr:first-child td:last-child")
            return self.get_text(status_cell)
        except:
            return ""
