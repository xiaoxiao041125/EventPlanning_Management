# -*- coding: utf-8 -*-
"""
登录页面 - Page Object Layer
封装登录页面的元素定位和操作方法
"""
from selenium.webdriver.common.by import By
from base.base_page import BasePage
from config.settings import settings


class LoginPage(BasePage):
    """
    登录页面类
    """
    
    # 页面URL
    URL = settings.LOGIN_URL
    
    # ==================== 元素定位器 ====================
    # 顶部导航栏登录按钮
    HEADER_LOGIN_BUTTON = (By.ID, "headerLoginBtn")
    
    # 登录模态框
    LOGIN_MODAL = (By.ID, "loginModal")
    
    # 登录表单元素
    USERNAME_INPUT = (By.ID, "modalUsername")
    PASSWORD_INPUT = (By.ID, "modalPassword")
    ROLE_SELECT = (By.ID, "loginRole")
    LOGIN_BUTTON = (By.ID, "loginBtn")
    
    # 注册链接
    REGISTER_LINK = (By.LINK_TEXT, "注册")
    
    # 提示信息
    ALERT_MESSAGE = (By.ID, "alertMsg")
    ERROR_MESSAGE = (By.CLASS_NAME, "error-message")
    
    # 页面标题和头部
    PAGE_TITLE = (By.TAG_NAME, "h2")
    
    def __init__(self, driver):
        """
        初始化登录页面
        :param driver: WebDriver实例
        """
        super().__init__(driver)
    
    def open(self):
        """
        打开登录页面
        :return: self
        """
        self.open_url(self.URL)
        self.wait_for_page_load()
        # 点击顶部登录按钮打开登录弹窗
        self.click_header_login()
        return self
    
    def click_header_login(self):
        """
        点击顶部导航栏的登录按钮
        :return: self
        """
        try:
            # 等待并点击顶部登录按钮
            self.wait_for_element_visible(self.HEADER_LOGIN_BUTTON, timeout=5)
            self.click(self.HEADER_LOGIN_BUTTON)
            # 等待登录表单出现
            self.wait_for_element_visible(self.USERNAME_INPUT, timeout=5)
            self.logger.info("已点击顶部登录按钮，登录表单已显示")
        except Exception as e:
            self.logger.warning(f"点击顶部登录按钮失败或登录表单已显示: {e}")
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
    
    def select_role(self, role_id: str):
        """
        选择角色
        :param role_id: 角色ID (1-普通用户, 2-员工)
        :return: self
        """
        role_text_map = {
            "1": "普通用户",
            "2": "员工",
            "user": "普通用户",
            "employee": "员工"
        }
        role_text = role_text_map.get(role_id, "普通用户")
        self.select_dropdown_by_visible_text(self.ROLE_SELECT, role_text)
        return self
    
    def click_login_button(self):
        """
        点击登录按钮
        :return: self
        """
        self.click(self.LOGIN_BUTTON)
        return self
    
    def click_register_link(self):
        """
        点击注册链接
        :return: RegisterPage实例
        """
        self.click(self.REGISTER_LINK)
        from page_objects.register_page import RegisterPage
        return RegisterPage(self.driver)
    
    def login(self, username: str, password: str, role_id: str):
        """
        执行完整登录操作
        :param username: 用户名
        :param password: 密码
        :param role_id: 角色ID
        :return: self
        """
        self.input_username(username)
        self.input_password(password)
        self.select_role(role_id)
        self.click_login_button()
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
    
    def is_login_successful(self) -> bool:
        """
        判断登录是否成功
        :return: 是否成功
        """
        # 登录成功后页面会跳转，检查当前URL是否改变
        current_url = self.get_page_url()
        return "login" not in current_url and current_url != self.URL
    
    def is_at_login_page(self) -> bool:
        """
        判断是否仍在登录页面
        :return: 是否在登录页面
        """
        return self.is_element_present(self.LOGIN_BUTTON)
    
    def get_page_title_text(self) -> str:
        """
        获取页面标题文本
        :return: 标题文本
        """
        return self.get_text(self.PAGE_TITLE)
    
    def clear_form(self):
        """
        清空登录表单
        :return: self
        """
        self.find_element(self.USERNAME_INPUT).clear()
        self.find_element(self.PASSWORD_INPUT).clear()
        return self
