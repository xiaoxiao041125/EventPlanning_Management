# -*- coding: utf-8 -*-
"""
注册页面 - Page Object Layer
封装注册页面的元素定位和操作方法
"""
from selenium.webdriver.common.by import By
from base.base_page import BasePage
from config.settings import settings


class RegisterPage(BasePage):
    """
    注册页面类
    """
    
    # 页面URL
    URL = settings.get_url("html/index.html")
    
    # ==================== 元素定位器 ====================
    # 注册表单元素
    USERNAME_INPUT = (By.ID, "regUsername")
    PASSWORD_INPUT = (By.ID, "regPassword")
    CONFIRM_PASSWORD_INPUT = (By.ID, "regConfirmPassword")
    ROLE_SELECT = (By.ID, "regRoleId")
    REGISTER_BUTTON = (By.ID, "registerBtn")
    
    # 员工注册额外字段
    EMPLOYEE_NAME_INPUT = (By.ID, "employeeName")
    PHONE_INPUT = (By.ID, "phone")
    SEX_SELECT = (By.ID, "sex")
    POST_SELECT = (By.ID, "post")
    CITY_INPUT = (By.ID, "city")
    
    # 登录链接
    LOGIN_LINK = (By.LINK_TEXT, "登录")
    
    # 提示信息
    ALERT_MESSAGE = (By.ID, "regAlertMsg")
    ERROR_MESSAGE = (By.CLASS_NAME, "error-message")
    
    def __init__(self, driver):
        """
        初始化注册页面
        :param driver: WebDriver实例
        """
        super().__init__(driver)
    
    def open(self):
        """
        打开注册页面
        :return: self
        """
        self.open_url(self.URL)
        self.wait_for_page_load()
        # 切换到注册标签页
        self.click((By.ID, "registerTab"))
        return self
    
    def input_username(self, username: str):
        """
        输入用户名
        :param username: 用户名
        :return: self
        """
        self.input_text(self.USERNAME_INPUT, username)
        return self
    
    def input_password(self, password: str):
        """
        输入密码
        :param password: 密码
        :return: self
        """
        self.input_text(self.PASSWORD_INPUT, password)
        return self
    
    def input_confirm_password(self, confirm_password: str):
        """
        输入确认密码
        :param confirm_password: 确认密码
        :return: self
        """
        self.input_text(self.CONFIRM_PASSWORD_INPUT, confirm_password)
        return self
    
    def select_role(self, role_id: str):
        """
        选择角色
        :param role_id: 角色ID
        :return: self
        """
        role_text_map = {
            "1": "用户",
            "2": "业务管理员",
            "3": "仓库管理员",
            "4": "会计",
            "5": "用户管理员"
        }
        role_text = role_text_map.get(role_id, "用户")
        self.select_dropdown_by_visible_text(self.ROLE_SELECT, role_text)
        return self
    
    def input_employee_name(self, name: str):
        """
        输入员工姓名
        :param name: 姓名
        :return: self
        """
        self.input_text(self.EMPLOYEE_NAME_INPUT, name)
        return self
    
    def input_phone(self, phone: str):
        """
        输入电话
        :param phone: 电话
        :return: self
        """
        self.input_text(self.PHONE_INPUT, phone)
        return self
    
    def select_sex(self, sex: str):
        """
        选择性别
        :param sex: 性别 (男/女)
        :return: self
        """
        self.select_dropdown_by_visible_text(self.SEX_SELECT, sex)
        return self
    
    def select_post(self, post: str):
        """
        选择职位
        :param post: 职位
        :return: self
        """
        self.select_dropdown_by_visible_text(self.POST_SELECT, post)
        return self
    
    def input_city(self, city: str):
        """
        输入城市
        :param city: 城市
        :return: self
        """
        self.input_text(self.CITY_INPUT, city)
        return self
    
    def click_register_button(self):
        """
        点击注册按钮
        :return: self
        """
        self.click(self.REGISTER_BUTTON)
        return self
    
    def click_login_link(self):
        """
        点击登录链接
        :return: LoginPage实例
        """
        self.click(self.LOGIN_LINK)
        from page_objects.login_page import LoginPage
        return LoginPage(self.driver)
    
    def register_user(self, username: str, password: str, role_id: str):
        """
        执行普通用户注册
        :param username: 用户名
        :param password: 密码
        :param role_id: 角色ID
        :return: self
        """
        self.input_username(username)
        self.input_password(password)
        self.input_confirm_password(password)
        self.select_role(role_id)
        self.click_register_button()
        return self
    
    def register_employee(self, username: str, password: str, employee_name: str,
                         phone: str, sex: str, post: str, city: str):
        """
        执行员工注册
        :param username: 用户名
        :param password: 密码
        :param employee_name: 员工姓名
        :param phone: 电话
        :param sex: 性别
        :param post: 职位
        :param city: 城市
        :return: self
        """
        self.input_username(username)
        self.input_password(password)
        self.input_confirm_password(password)
        self.select_role("2")  # 员工角色
        self.input_employee_name(employee_name)
        self.input_phone(phone)
        self.select_sex(sex)
        self.select_post(post)
        self.input_city(city)
        self.click_register_button()
        return self
    
    def get_alert_message(self) -> str:
        """
        获取弹窗提示信息
        :return: 提示信息文本
        """
        try:
            return self.get_text(self.ALERT_MESSAGE)
        except:
            return ""
    
    def get_error_message(self) -> str:
        """
        获取错误提示信息
        :return: 错误信息文本
        """
        try:
            return self.get_text(self.ERROR_MESSAGE)
        except:
            return ""
    
    def is_register_successful(self) -> bool:
        """
        判断注册是否成功
        :return: 是否成功
        """
        alert_msg = self.get_alert_message()
        return "成功" in alert_msg or "success" in alert_msg.lower()
    
    def is_at_register_page(self) -> bool:
        """
        判断是否仍在注册页面
        :return: 是否在注册页面
        """
        return self.is_element_present(self.REGISTER_BUTTON)
