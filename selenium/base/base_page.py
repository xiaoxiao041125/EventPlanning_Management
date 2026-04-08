# -*- coding: utf-8 -*-
"""
基础页面类 - Base Layer
封装所有页面通用的Selenium操作方法
"""
import os
import time
import logging
from typing import Tuple, List, Optional, Union
from selenium.webdriver.remote.webdriver import WebDriver
from selenium.webdriver.remote.webelement import WebElement
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import (
    TimeoutException,
    NoSuchElementException,
    ElementNotVisibleException,
    ElementNotInteractableException,
    StaleElementReferenceException
)
from selenium.webdriver.common.action_chains import ActionChains

from config.settings import Settings


class BasePage:
    """
    基础页面类
    所有页面对象类的父类，封装常用操作
    """
    
    def __init__(self, driver: WebDriver):
        """
        初始化基础页面
        :param driver: WebDriver实例
        """
        self.driver = driver
        self.logger = logging.getLogger(__name__)
        self.settings = Settings()
        self.timeout = self.settings.IMPLICIT_WAIT
        self.poll_frequency = 0.5
        
    def open_url(self, url: str) -> None:
        """
        打开指定URL
        :param url: 目标URL
        """
        try:
            self.driver.get(url)
            self.logger.info(f"打开URL: {url}")
        except Exception as e:
            self.logger.error(f"打开URL失败: {url}, 错误: {e}")
            raise
    
    def find_element(self, locator: Tuple[By, str], timeout: int = None) -> WebElement:
        """
        查找单个元素（显式等待）
        :param locator: 元素定位器 (By.ID, "id_value")
        :param timeout: 超时时间（秒）
        :return: WebElement元素
        """
        timeout = timeout or self.timeout
        try:
            element = WebDriverWait(self.driver, timeout, self.poll_frequency).until(
                EC.presence_of_element_located(locator)
            )
            self.logger.debug(f"找到元素: {locator}")
            return element
        except TimeoutException:
            self.logger.error(f"查找元素超时: {locator}")
            self.take_screenshot(f"element_not_found_{int(time.time())}")
            raise NoSuchElementException(f"元素未找到: {locator}")
    
    def find_elements(self, locator: Tuple[By, str], timeout: int = None) -> List[WebElement]:
        """
        查找多个元素
        :param locator: 元素定位器
        :param timeout: 超时时间（秒）
        :return: WebElement元素列表
        """
        timeout = timeout or self.timeout
        try:
            elements = WebDriverWait(self.driver, timeout, self.poll_frequency).until(
                EC.presence_of_all_elements_located(locator)
            )
            self.logger.debug(f"找到 {len(elements)} 个元素: {locator}")
            return elements
        except TimeoutException:
            self.logger.warning(f"查找元素超时，返回空列表: {locator}")
            return []
    
    def find_visible_element(self, locator: Tuple[By, str], timeout: int = None) -> WebElement:
        """
        查找可见元素
        :param locator: 元素定位器
        :param timeout: 超时时间（秒）
        :return: WebElement元素
        """
        timeout = timeout or self.timeout
        try:
            element = WebDriverWait(self.driver, timeout, self.poll_frequency).until(
                EC.visibility_of_element_located(locator)
            )
            return element
        except TimeoutException:
            self.logger.error(f"查找可见元素超时: {locator}")
            self.take_screenshot(f"element_not_visible_{int(time.time())}")
            raise ElementNotVisibleException(f"元素不可见: {locator}")
    
    def find_clickable_element(self, locator: Tuple[By, str], timeout: int = None) -> WebElement:
        """
        查找可点击元素
        :param locator: 元素定位器
        :param timeout: 超时时间（秒）
        :return: WebElement元素
        """
        timeout = timeout or self.timeout
        try:
            element = WebDriverWait(self.driver, timeout, self.poll_frequency).until(
                EC.element_to_be_clickable(locator)
            )
            return element
        except TimeoutException:
            self.logger.error(f"查找可点击元素超时: {locator}")
            self.take_screenshot(f"element_not_clickable_{int(time.time())}")
            raise ElementNotInteractableException(f"元素不可点击: {locator}")
    
    def wait_for_element_visible(self, locator: Tuple[By, str], timeout: int = None) -> WebElement:
        """
        等待元素可见
        :param locator: 元素定位器
        :param timeout: 超时时间（秒）
        :return: WebElement元素
        """
        return self.find_visible_element(locator, timeout)
    
    def wait_for_element_clickable(self, locator: Tuple[By, str], timeout: int = None) -> WebElement:
        """
        等待元素可点击
        :param locator: 元素定位器
        :param timeout: 超时时间（秒）
        :return: WebElement元素
        """
        return self.find_clickable_element(locator, timeout)
    
    def click(self, locator: Tuple[By, str], timeout: int = None) -> None:
        """
        点击元素
        :param locator: 元素定位器
        :param timeout: 超时时间（秒）
        """
        try:
            element = self.find_clickable_element(locator, timeout)
            element.click()
            self.logger.info(f"点击元素: {locator}")
        except Exception as e:
            self.logger.error(f"点击元素失败: {locator}, 错误: {e}")
            raise
    
    def input_text(self, locator: Tuple[By, str], text: str, clear_first: bool = True, timeout: int = None) -> None:
        """
        输入文本
        :param locator: 元素定位器
        :param text: 输入的文本
        :param clear_first: 是否先清空
        :param timeout: 超时时间（秒）
        """
        try:
            element = self.find_visible_element(locator, timeout)
            if clear_first:
                element.clear()
            element.send_keys(text)
            self.logger.info(f"输入文本到 {locator}: {text}")
        except Exception as e:
            self.logger.error(f"输入文本失败: {locator}, 错误: {e}")
            raise
    
    def get_text(self, locator: Tuple[By, str], timeout: int = None) -> str:
        """
        获取元素文本
        :param locator: 元素定位器
        :param timeout: 超时时间（秒）
        :return: 元素文本
        """
        try:
            element = self.find_visible_element(locator, timeout)
            text = element.text
            self.logger.debug(f"获取文本 {locator}: {text}")
            return text
        except Exception as e:
            self.logger.error(f"获取文本失败: {locator}, 错误: {e}")
            raise
    
    def get_attribute(self, locator: Tuple[By, str], attribute: str, timeout: int = None) -> str:
        """
        获取元素属性值
        :param locator: 元素定位器
        :param attribute: 属性名
        :param timeout: 超时时间（秒）
        :return: 属性值
        """
        try:
            element = self.find_element(locator, timeout)
            value = element.get_attribute(attribute)
            self.logger.debug(f"获取属性 {locator}.{attribute}: {value}")
            return value
        except Exception as e:
            self.logger.error(f"获取属性失败: {locator}, 错误: {e}")
            raise
    
    def is_element_present(self, locator: Tuple[By, str], timeout: int = 5) -> bool:
        """
        判断元素是否存在
        :param locator: 元素定位器
        :param timeout: 超时时间（秒）
        :return: 是否存在
        """
        try:
            self.find_element(locator, timeout)
            return True
        except NoSuchElementException:
            return False
    
    def is_element_visible(self, locator: Tuple[By, str], timeout: int = 5) -> bool:
        """
        判断元素是否可见
        :param locator: 元素定位器
        :param timeout: 超时时间（秒）
        :return: 是否可见
        """
        try:
            self.find_visible_element(locator, timeout)
            return True
        except (TimeoutException, ElementNotVisibleException):
            return False
    
    def wait_for_element_invisible(self, locator: Tuple[By, str], timeout: int = None) -> bool:
        """
        等待元素不可见
        :param locator: 元素定位器
        :param timeout: 超时时间（秒）
        :return: 是否成功
        """
        timeout = timeout or self.timeout
        try:
            result = WebDriverWait(self.driver, timeout, self.poll_frequency).until(
                EC.invisibility_of_element_located(locator)
            )
            return result
        except TimeoutException:
            return False
    
    def select_dropdown_by_visible_text(self, locator: Tuple[By, str], text: str, timeout: int = None) -> None:
        """
        通过可见文本选择下拉框选项
        :param locator: 下拉框定位器
        :param text: 可见文本
        :param timeout: 超时时间（秒）
        """
        from selenium.webdriver.support.ui import Select
        try:
            element = self.find_visible_element(locator, timeout)
            select = Select(element)
            select.select_by_visible_text(text)
            self.logger.info(f"选择下拉框 {locator} 选项: {text}")
        except Exception as e:
            self.logger.error(f"选择下拉框失败: {locator}, 错误: {e}")
            raise
    
    def select_dropdown_by_value(self, locator: Tuple[By, str], value: str, timeout: int = None) -> None:
        """
        通过value属性选择下拉框选项
        :param locator: 下拉框定位器
        :param value: value属性值
        :param timeout: 超时时间（秒）
        """
        from selenium.webdriver.support.ui import Select
        try:
            element = self.find_visible_element(locator, timeout)
            select = Select(element)
            select.select_by_value(value)
            self.logger.info(f"选择下拉框 {locator} value: {value}")
        except Exception as e:
            self.logger.error(f"选择下拉框失败: {locator}, 错误: {e}")
            raise
    
    def select_dropdown_by_index(self, locator: Tuple[By, str], index: int, timeout: int = None) -> None:
        """
        通过索引选择下拉框选项
        :param locator: 下拉框定位器
        :param index: 索引
        :param timeout: 超时时间（秒）
        """
        from selenium.webdriver.support.ui import Select
        try:
            element = self.find_visible_element(locator, timeout)
            select = Select(element)
            select.select_by_index(index)
            self.logger.info(f"选择下拉框 {locator} 索引: {index}")
        except Exception as e:
            self.logger.error(f"选择下拉框失败: {locator}, 错误: {e}")
            raise
    
    def switch_to_frame(self, locator: Union[Tuple[By, str], str, int]) -> None:
        """
        切换到iframe
        :param locator: iframe定位器或name/id或索引
        """
        try:
            if isinstance(locator, tuple):
                WebDriverWait(self.driver, self.timeout).until(
                    EC.frame_to_be_available_and_switch_to_it(locator)
                )
            else:
                self.driver.switch_to.frame(locator)
            self.logger.info(f"切换到iframe: {locator}")
        except Exception as e:
            self.logger.error(f"切换iframe失败: {locator}, 错误: {e}")
            raise
    
    def switch_to_default_content(self) -> None:
        """
        切换回主文档
        """
        self.driver.switch_to.default_content()
        self.logger.info("切换回主文档")
    
    def switch_to_window(self, window_handle: str) -> None:
        """
        切换到指定窗口
        :param window_handle: 窗口句柄
        """
        self.driver.switch_to.window(window_handle)
        self.logger.info(f"切换到窗口: {window_handle}")
    
    def switch_to_new_window(self) -> None:
        """
        切换到新打开的窗口
        """
        current_handle = self.driver.current_window_handle
        handles = self.driver.window_handles
        for handle in handles:
            if handle != current_handle:
                self.switch_to_window(handle)
                break
    
    def accept_alert(self, timeout: int = 5) -> str:
        """
        接受弹窗
        :param timeout: 超时时间（秒）
        :return: 弹窗文本
        """
        try:
            alert = WebDriverWait(self.driver, timeout).until(EC.alert_is_present())
            text = alert.text
            alert.accept()
            self.logger.info(f"接受弹窗: {text}")
            return text
        except TimeoutException:
            self.logger.warning("等待弹窗超时")
            return ""
    
    def dismiss_alert(self, timeout: int = 5) -> str:
        """
        取消弹窗
        :param timeout: 超时时间（秒）
        :return: 弹窗文本
        """
        try:
            alert = WebDriverWait(self.driver, timeout).until(EC.alert_is_present())
            text = alert.text
            alert.dismiss()
            self.logger.info(f"取消弹窗: {text}")
            return text
        except TimeoutException:
            self.logger.warning("等待弹窗超时")
            return ""
    
    def execute_script(self, script: str, *args) -> any:
        """
        执行JavaScript脚本
        :param script: JavaScript代码
        :param args: 参数
        :return: 执行结果
        """
        try:
            result = self.driver.execute_script(script, *args)
            self.logger.debug(f"执行脚本: {script[:50]}...")
            return result
        except Exception as e:
            self.logger.error(f"执行脚本失败: {e}")
            raise
    
    def scroll_to_element(self, locator: Tuple[By, str]) -> None:
        """
        滚动到元素位置
        :param locator: 元素定位器
        """
        element = self.find_element(locator)
        self.execute_script("arguments[0].scrollIntoView(true);", element)
        self.logger.info(f"滚动到元素: {locator}")
    
    def scroll_to_bottom(self) -> None:
        """
        滚动到页面底部
        """
        self.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        self.logger.info("滚动到页面底部")
    
    def scroll_to_top(self) -> None:
        """
        滚动到页面顶部
        """
        self.execute_script("window.scrollTo(0, 0);")
        self.logger.info("滚动到页面顶部")
    
    def hover_over_element(self, locator: Tuple[By, str]) -> None:
        """
        鼠标悬停在元素上
        :param locator: 元素定位器
        """
        element = self.find_element(locator)
        ActionChains(self.driver).move_to_element(element).perform()
        self.logger.info(f"悬停在元素: {locator}")
    
    def double_click(self, locator: Tuple[By, str]) -> None:
        """
        双击元素
        :param locator: 元素定位器
        """
        element = self.find_element(locator)
        ActionChains(self.driver).double_click(element).perform()
        self.logger.info(f"双击元素: {locator}")
    
    def right_click(self, locator: Tuple[By, str]) -> None:
        """
        右键点击元素
        :param locator: 元素定位器
        """
        element = self.find_element(locator)
        ActionChains(self.driver).context_click(element).perform()
        self.logger.info(f"右键点击元素: {locator}")
    
    def drag_and_drop(self, source_locator: Tuple[By, str], target_locator: Tuple[By, str]) -> None:
        """
        拖拽元素
        :param source_locator: 源元素定位器
        :param target_locator: 目标元素定位器
        """
        source = self.find_element(source_locator)
        target = self.find_element(target_locator)
        ActionChains(self.driver).drag_and_drop(source, target).perform()
        self.logger.info(f"拖拽元素从 {source_locator} 到 {target_locator}")
    
    def get_page_title(self) -> str:
        """
        获取页面标题
        :return: 页面标题
        """
        return self.driver.title
    
    def get_page_url(self) -> str:
        """
        获取当前URL
        :return: 当前URL
        """
        return self.driver.current_url
    
    def refresh_page(self) -> None:
        """
        刷新页面
        """
        self.driver.refresh()
        self.logger.info("刷新页面")
    
    def go_back(self) -> None:
        """
        浏览器后退
        """
        self.driver.back()
        self.logger.info("浏览器后退")
    
    def go_forward(self) -> None:
        """
        浏览器前进
        """
        self.driver.forward()
        self.logger.info("浏览器前进")
    
    def take_screenshot(self, filename: str = None) -> str:
        """
        截图
        :param filename: 文件名（不含路径）
        :return: 截图文件路径
        """
        if filename is None:
            filename = f"screenshot_{int(time.time())}.png"
        
        filepath = os.path.join(self.settings.SCREENSHOT_DIR, filename)
        os.makedirs(os.path.dirname(filepath), exist_ok=True)
        
        try:
            self.driver.save_screenshot(filepath)
            self.logger.info(f"截图保存: {filepath}")
            return filepath
        except Exception as e:
            self.logger.error(f"截图失败: {e}")
            return ""
    
    def wait_for_page_load(self, timeout: int = None) -> None:
        """
        等待页面加载完成
        :param timeout: 超时时间（秒）
        """
        timeout = timeout or self.timeout
        try:
            WebDriverWait(self.driver, timeout).until(
                lambda d: d.execute_script("return document.readyState") == "complete"
            )
            self.logger.info("页面加载完成")
        except TimeoutException:
            self.logger.warning("等待页面加载超时")
    
    def wait_for_ajax_complete(self, timeout: int = None) -> None:
        """
        等待AJAX请求完成（jQuery）
        :param timeout: 超时时间（秒）
        """
        timeout = timeout or self.timeout
        try:
            WebDriverWait(self.driver, timeout).until(
                lambda d: d.execute_script("return jQuery.active == 0")
            )
            self.logger.info("AJAX请求完成")
        except TimeoutException:
            self.logger.warning("等待AJAX完成超时")
    
    def clear_and_input(self, locator: Tuple[By, str], text: str, timeout: int = None) -> None:
        """
        清空并输入文本
        :param locator: 元素定位器
        :param text: 输入文本
        :param timeout: 超时时间（秒）
        """
        self.input_text(locator, text, clear_first=True, timeout=timeout)
    
    def get_element_count(self, locator: Tuple[By, str]) -> int:
        """
        获取元素数量
        :param locator: 元素定位器
        :return: 元素数量
        """
        elements = self.find_elements(locator)
        return len(elements)
    
    def wait_for_text_present(self, locator: Tuple[By, str], text: str, timeout: int = None) -> bool:
        """
        等待元素包含指定文本
        :param locator: 元素定位器
        :param text: 期望文本
        :param timeout: 超时时间（秒）
        :return: 是否成功
        """
        timeout = timeout or self.timeout
        try:
            result = WebDriverWait(self.driver, timeout, self.poll_frequency).until(
                EC.text_to_be_present_in_element(locator, text)
            )
            return result
        except TimeoutException:
            return False
    
    def wait_for_value_present(self, locator: Tuple[By, str], value: str, timeout: int = None) -> bool:
        """
        等待元素value属性包含指定值
        :param locator: 元素定位器
        :param value: 期望value
        :param timeout: 超时时间（秒）
        :return: 是否成功
        """
        timeout = timeout or self.timeout
        try:
            result = WebDriverWait(self.driver, timeout, self.poll_frequency).until(
                EC.text_to_be_present_in_element_value(locator, value)
            )
            return result
        except TimeoutException:
            return False
