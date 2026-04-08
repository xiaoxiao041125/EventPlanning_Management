# -*- coding: utf-8 -*-
"""
物料管理页面 - Page Object Layer
封装物料管理页面的元素定位和操作方法
"""
from selenium.webdriver.common.by import By
from base.base_page import BasePage
from config.settings import settings


class MaterialPage(BasePage):
    """
    物料管理页面类
    """
    
    # 页面URL
    URL = settings.get_url("html/warehouse/material.html")
    
    # ==================== 元素定位器 ====================
    # 搜索区域
    MATERIAL_TYPE_SEARCH = (By.ID, "materialType")
    SEARCH_BUTTON = (By.ID, "searchBtn")
    RESET_BUTTON = (By.ID, "resetBtn")
    
    # 新增物料按钮
    ADD_MATERIAL_BUTTON = (By.ID, "addMaterialBtn")
    
    # 物料表单弹窗
    MATERIAL_MODAL = (By.ID, "materialModal")
    MATERIAL_TYPE_INPUT = (By.ID, "materialType")
    QUANTITY_INPUT = (By.ID, "quantity")
    PRICE_INPUT = (By.ID, "price")
    CATEGORY_SELECT = (By.ID, "category")
    REMARKS_INPUT = (By.ID, "remarks")
    SAVE_BUTTON = (By.ID, "saveBtn")
    CANCEL_BUTTON = (By.ID, "cancelBtn")
    
    # 物料列表
    MATERIAL_TABLE = (By.ID, "materialTable")
    MATERIAL_ROWS = (By.CSS_SELECTOR, "#materialTable tbody tr")
    
    # 操作按钮
    EDIT_BUTTON = (By.CSS_SELECTOR, ".edit-btn")
    DELETE_BUTTON = (By.CSS_SELECTOR, ".delete-btn")
    
    # 分页
    PAGINATION = (By.ID, "pagination")
    PAGE_BUTTONS = (By.CSS_SELECTOR, "#pagination .page-btn")
    
    def __init__(self, driver):
        """
        初始化物料管理页面
        :param driver: WebDriver实例
        """
        super().__init__(driver)
    
    def open(self):
        """
        打开物料管理页面
        :return: self
        """
        self.open_url(self.URL)
        self.wait_for_page_load()
        return self
    
    def search_material(self, material_type: str):
        """
        搜索物料
        :param material_type: 物料类型
        :return: self
        """
        self.input_text(self.MATERIAL_TYPE_SEARCH, material_type)
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
    
    def click_add_material(self):
        """
        点击新增物料按钮
        :return: self
        """
        self.click(self.ADD_MATERIAL_BUTTON)
        self.wait_for_element_visible(self.MATERIAL_MODAL)
        return self
    
    def input_material_type(self, material_type: str):
        """
        输入物料类型
        :param material_type: 物料类型
        :return: self
        """
        self.input_text(self.MATERIAL_TYPE_INPUT, material_type)
        return self
    
    def input_quantity(self, quantity: str):
        """
        输入数量
        :param quantity: 数量
        :return: self
        """
        self.input_text(self.QUANTITY_INPUT, quantity)
        return self
    
    def input_price(self, price: str):
        """
        输入单价
        :param price: 单价
        :return: self
        """
        self.input_text(self.PRICE_INPUT, price)
        return self
    
    def select_category(self, category: str):
        """
        选择分类
        :param category: 分类
        :return: self
        """
        self.select_dropdown_by_visible_text(self.CATEGORY_SELECT, category)
        return self
    
    def input_remarks(self, remarks: str):
        """
        输入备注
        :param remarks: 备注
        :return: self
        """
        self.input_text(self.REMARKS_INPUT, remarks)
        return self
    
    def click_save(self):
        """
        点击保存按钮
        :return: self
        """
        self.click(self.SAVE_BUTTON)
        return self
    
    def click_cancel(self):
        """
        点击取消按钮
        :return: self
        """
        self.click(self.CANCEL_BUTTON)
        self.wait_for_element_invisible(self.MATERIAL_MODAL)
        return self
    
    def add_material(self, material_type: str, quantity: str, price: str,
                    category: str, remarks: str = ""):
        """
        添加新物料
        :param material_type: 物料类型
        :param quantity: 数量
        :param price: 单价
        :param category: 分类
        :param remarks: 备注
        :return: self
        """
        self.click_add_material()
        self.input_material_type(material_type)
        self.input_quantity(quantity)
        self.input_price(price)
        self.select_category(category)
        if remarks:
            self.input_remarks(remarks)
        self.click_save()
        return self
    
    def get_material_count(self) -> int:
        """
        获取物料列表数量
        :return: 物料数量
        """
        rows = self.find_elements(self.MATERIAL_ROWS)
        return len(rows)
    
    def click_edit(self, index: int = 0):
        """
        点击编辑按钮
        :param index: 物料索引
        :return: self
        """
        buttons = self.find_elements(self.EDIT_BUTTON)
        if index < len(buttons):
            buttons[index].click()
            self.wait_for_element_visible(self.MATERIAL_MODAL)
        return self
    
    def click_delete(self, index: int = 0):
        """
        点击删除按钮
        :param index: 物料索引
        :return: self
        """
        buttons = self.find_elements(self.DELETE_BUTTON)
        if index < len(buttons):
            buttons[index].click()
        return self
    
    def confirm_delete(self):
        """
        确认删除（处理确认弹窗）
        :return: self
        """
        self.accept_alert()
        return self
    
    def get_first_material_type(self) -> str:
        """
        获取第一个物料的类型
        :return: 物料类型
        """
        try:
            type_cell = (By.CSS_SELECTOR, "#materialTable tbody tr:first-child td:nth-child(2)")
            return self.get_text(type_cell)
        except:
            return ""
    
    def go_to_page(self, page_number: int):
        """
        跳转到指定页
        :param page_number: 页码
        :return: self
        """
        page_button = (By.XPATH, f"//div[@id='pagination']//button[text()='{page_number}']")
        self.click(page_button)
        self.wait_for_page_load()
        return self
    
    def is_save_successful(self) -> bool:
        """
        判断保存是否成功
        :return: 是否成功
        """
        alert = self.get_alert_text()
        return "成功" in alert or "success" in alert.lower()
