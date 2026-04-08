# -*- coding: utf-8 -*-
"""
方案制定页面 - Page Object Layer
封装方案制定页面的元素定位和操作方法
"""
from selenium.webdriver.common.by import By
from base.base_page import BasePage
from config.settings import settings


class PlanPage(BasePage):
    """
    方案制定页面类
    """
    
    # 页面 URL
    URL = settings.get_url("html/plan.html")
    
    # ==================== 元素定位器 ====================
    # 方案制定表单
    ACTIVITY_PLACE_SELECT = (By.ID, "activityLocation")    # 活动地点下拉框
    ACTIVITY_TYPE_SELECT = (By.ID, "activityType")         # 活动类型下拉框
    START_TIME_INPUT = (By.ID, "startTime")                # 起始时间
    END_TIME_INPUT = (By.ID, "endTime")                    # 结束时间
    ACTIVITY_PEOPLE_SELECT = (By.ID, "participantCount")   # 活动人数下拉框
    ACTIVITY_BUDGET_SELECT = (By.ID, "budget")             # 活动预算下拉框
    PHONE_INPUT = (By.ID, "phone")                         # 手机号码
    NAME_INPUT = (By.ID, "name")                           # 您的称呼
    GENDER_RADIO_MALE = (By.CSS_SELECTOR, "input[name='gender'][value='male']")    # 性别 - 先生
    GENDER_RADIO_FEMALE = (By.CSS_SELECTOR, "input[name='gender'][value='female']") # 性别 - 女士
    REQUIREMENT_DESC_INPUT = (By.ID, "demandDescription")  # 需求描述
    SUBMIT_BUTTON = (By.CSS_SELECTOR, "button[type='submit']")  # 活动申请按钮
    
    def __init__(self, driver):
        """
        初始化方案制定页面
        :param driver: WebDriver 实例
        """
        super().__init__(driver)
    
    def open(self):
        """
        打开方案制定页面
        :return: self
        """
        self.open_url(self.URL)
        self.wait_for_page_load()
        return self
    
    def select_activity_place(self, place: str):
        """
        选择活动地点 - 带重试机制
        :param place: 地点名称
        :return: self
        """
        try:
            # 先尝试直接选择
            self.select_dropdown_by_visible_text(self.ACTIVITY_PLACE_SELECT, place)
            return self
        except Exception as e:
            self.logger.warning(f"选择活动地点失败：{place}, 尝试重试：{e}")
            # 如果失败，等待一下再重试
            import time
            time.sleep(0.5)
            # 尝试点击下拉框打开选项
            self.click(self.ACTIVITY_PLACE_SELECT)
            time.sleep(0.3)
            # 再次尝试选择
            self.select_dropdown_by_visible_text(self.ACTIVITY_PLACE_SELECT, place)
            return self
    
    def select_activity_type(self, activity_type: str):
        """
        选择活动类型 - 带重试机制
        :param activity_type: 活动类型
        :return: self
        """
        try:
            # 先尝试直接选择
            self.select_dropdown_by_visible_text(self.ACTIVITY_TYPE_SELECT, activity_type)
            return self
        except Exception as e:
            self.logger.warning(f"选择活动类型失败：{activity_type}, 尝试重试：{e}")
            # 如果失败，等待一下再重试
            import time
            time.sleep(0.5)
            # 尝试点击下拉框打开选项
            self.click(self.ACTIVITY_TYPE_SELECT)
            time.sleep(0.3)
            # 再次尝试选择
            self.select_dropdown_by_visible_text(self.ACTIVITY_TYPE_SELECT, activity_type)
            return self
    
    def input_start_time(self, start_time: str):
        """
        输入起始时间 - 使用 JavaScript 设置 value 属性并触发所有必要事件
        :param start_time: 起始时间 (格式：YYYY-MM-DD 或 YYYY/MM/DD)
        :return: self
        """
        element = self.find_visible_element(self.START_TIME_INPUT)
        # 确保日期格式为 YYYY-MM-DD（HTML5 date input 标准格式）
        formatted_time = start_time.replace('/', '-')
        # 使用 JavaScript 直接设置 value 属性并触发多个事件确保表单验证生效
        self.driver.execute_script(
            f"""
            var element = arguments[0];
            var dateValue = '{formatted_time}';
            
            // 设置 value 属性
            element.value = dateValue;
            
            // 触发 input 事件
            element.dispatchEvent(new Event('input', {{ bubbles: true }}));
            
            // 触发 change 事件
            element.dispatchEvent(new Event('change', {{ bubbles: true }}));
            
            // 触发 blur 事件（触发表单验证）
            element.dispatchEvent(new Event('blur', {{ bubbles: true }}));
            
            console.log('Date set to:', dateValue);
            """,
            element
        )
        self.logger.info(f"输入起始时间：{start_time} -> {formatted_time}")
        return self
    
    def input_end_time(self, end_time: str):
        """
        输入结束时间 - 使用 JavaScript 设置 value 属性并触发所有必要事件
        :param end_time: 结束时间 (格式：YYYY-MM-DD 或 YYYY/MM/DD)
        :return: self
        """
        element = self.find_visible_element(self.END_TIME_INPUT)
        # 确保日期格式为 YYYY-MM-DD（HTML5 date input 标准格式）
        formatted_time = end_time.replace('/', '-')
        # 使用 JavaScript 直接设置 value 属性并触发多个事件确保表单验证生效
        self.driver.execute_script(
            f"""
            var element = arguments[0];
            var dateValue = '{formatted_time}';
            
            // 设置 value 属性
            element.value = dateValue;
            
            // 触发 input 事件
            element.dispatchEvent(new Event('input', {{ bubbles: true }}));
            
            // 触发 change 事件
            element.dispatchEvent(new Event('change', {{ bubbles: true }}));
            
            // 触发 blur 事件（触发表单验证）
            element.dispatchEvent(new Event('blur', {{ bubbles: true }}));
            
            console.log('Date set to:', dateValue);
            """,
            element
        )
        self.logger.info(f"输入结束时间：{end_time} -> {formatted_time}")
        return self
    
    def select_activity_people(self, people: str):
        """
        选择活动人数
        :param people: 人数范围
        :return: self
        """
        self.select_dropdown_by_visible_text(self.ACTIVITY_PEOPLE_SELECT, people)
        return self
    
    def select_activity_budget(self, budget: str):
        """
        选择活动预算 - 优先通过可见文本选择，失败时尝试索引
        :param budget: 预算范围 (如：5-10 万)
        :return: self
        """
        element = self.find_visible_element(self.ACTIVITY_BUDGET_SELECT)
        from selenium.webdriver.support.ui import Select
        select = Select(element)
        
        # 优先尝试通过可见文本选择
        try:
            select.select_by_visible_text(budget)
            self.logger.info(f"选择活动预算：{budget}")
            return self
        except Exception as e:
            self.logger.warning(f"通过文本选择预算失败：{budget}, 尝试索引方式：{e}")
        
        # 如果文本选择失败，尝试索引方式（作为备选）
        budget_index_map = {
            "少于 1 万": 0,
            "1-3 万": 1,
            "3-5 万": 2,
            "5-10 万": 3,
            "10-20 万": 4,
            "20-50 万": 5,
            "50 万+": 6
        }
        
        if budget in budget_index_map:
            index = budget_index_map[budget]
            try:
                select.select_by_index(index)
                self.logger.info(f"选择活动预算：{budget} (索引：{index})")
                return self
            except Exception as e:
                self.logger.error(f"通过索引选择预算失败：{budget}, 错误：{e}")
        
        # 如果所有方式都失败，抛出异常
        raise Exception(f"无法选择预算选项：{budget}")
    
    def input_phone(self, phone: str):
        """
        输入手机号码 - 使用 JavaScript 辅助输入
        :param phone: 手机号
        :return: self
        """
        element = self.find_visible_element(self.PHONE_INPUT)
        # 滚动到元素位置
        self.driver.execute_script("arguments[0].scrollIntoView(true);", element)
        # 清空并输入
        element.clear()
        element.send_keys(phone)
        self.logger.info(f"输入手机号：{phone}")
        return self
    
    def input_name(self, name: str):
        """
        输入称呼 - 使用 JavaScript 辅助输入
        :param name: 称呼
        :return: self
        """
        element = self.find_visible_element(self.NAME_INPUT)
        # 滚动到元素位置
        self.driver.execute_script("arguments[0].scrollIntoView(true);", element)
        # 清空并输入
        element.clear()
        element.send_keys(name)
        self.logger.info(f"输入称呼：{name}")
        return self
    
    def select_gender(self, gender: str = "先生"):
        """
        选择性别
        :param gender: 性别 (先生/女士)
        :return: self
        """
        if gender == "先生":
            self.click(self.GENDER_RADIO_MALE)
        else:
            self.click(self.GENDER_RADIO_FEMALE)
        return self
    
    def input_requirement_desc(self, desc: str):
        """
        输入需求描述 - 使用 JavaScript 辅助输入
        :param desc: 需求描述
        :return: self
        """
        element = self.find_visible_element(self.REQUIREMENT_DESC_INPUT)
        # 滚动到元素位置
        self.driver.execute_script("arguments[0].scrollIntoView(true);", element)
        # 清空并输入
        element.clear()
        element.send_keys(desc)
        self.logger.info(f"输入需求描述：{desc}")
        return self
    
    def click_submit_button(self):
        """
        点击提交按钮 - 使用 JavaScript 点击确保触发
        :return: self
        """
        element = self.find_clickable_element(self.SUBMIT_BUTTON)
        # 滚动到元素位置
        self.driver.execute_script("arguments[0].scrollIntoView(true);", element)
        # 使用 JavaScript 点击
        self.driver.execute_script("arguments[0].click();", element)
        self.logger.info("点击提交按钮")
        return self
    
    def submit_plan(self, place: str, activity_type: str, start_time: str,
                    end_time: str, people: str, budget: str, phone: str, 
                    name: str, gender: str, desc: str):
        """
        提交方案制定表单
        :param place: 活动地点
        :param activity_type: 活动类型
        :param start_time: 起始时间
        :param end_time: 结束时间
        :param people: 活动人数
        :param budget: 活动预算
        :param phone: 手机号码
        :param name: 称呼
        :param gender: 性别
        :param desc: 需求描述
        :return: self
        """
        self.select_activity_place(place)
        self.select_activity_type(activity_type)
        self.input_start_time(start_time)
        self.input_end_time(end_time)
        self.select_activity_people(people)
        self.select_activity_budget(budget)
        self.input_phone(phone)
        self.input_name(name)
        self.select_gender(gender)
        self.input_requirement_desc(desc)
        self.click_submit_button()
        return self
