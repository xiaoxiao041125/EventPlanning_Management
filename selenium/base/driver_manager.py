# -*- coding: utf-8 -*-
"""
浏览器驱动管理类 - Base Layer
负责WebDriver的创建、配置和管理
"""
import os
import logging
from selenium import webdriver
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.edge.service import Service as EdgeService
from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.firefox.options import Options as FirefoxOptions
from selenium.webdriver.edge.options import Options as EdgeOptions
from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager
from webdriver_manager.microsoft import EdgeChromiumDriverManager

from config.settings import Settings


class DriverManager:
    """
    浏览器驱动管理类
    支持Chrome、Firefox、Edge浏览器
    """
    
    def __init__(self):
        self.logger = logging.getLogger(__name__)
        self.settings = Settings()
        self.driver = None
    
    def create_driver(self, browser_type: str = None, headless: bool = None) -> webdriver:
        """
        创建WebDriver实例
        :param browser_type: 浏览器类型 (chrome/firefox/edge)
        :param headless: 是否无头模式
        :return: WebDriver实例
        """
        browser_type = browser_type or self.settings.BROWSER
        headless = headless if headless is not None else self.settings.HEADLESS
        
        self.logger.info(f"创建{browser_type}浏览器驱动，无头模式: {headless}")
        
        if browser_type.lower() == "chrome":
            self.driver = self._create_chrome_driver(headless)
        elif browser_type.lower() == "firefox":
            self.driver = self._create_firefox_driver(headless)
        elif browser_type.lower() == "edge":
            self.driver = self._create_edge_driver(headless)
        else:
            raise ValueError(f"不支持的浏览器类型: {browser_type}")
        
        # 配置浏览器
        self._configure_driver()
        
        return self.driver
    
    def _create_chrome_driver(self, headless: bool) -> webdriver.Chrome:
        """
        创建Chrome浏览器驱动
        :param headless: 是否无头模式
        :return: Chrome WebDriver
        """
        options = ChromeOptions()
        
        # 基础配置
        options.add_argument("--start-maximized")
        options.add_argument("--disable-extensions")
        options.add_argument("--disable-popup-blocking")
        options.add_argument("--disable-infobars")
        options.add_argument("--disable-gpu")
        options.add_argument("--no-sandbox")
        options.add_argument("--disable-dev-shm-usage")
        
        # 无头模式
        if headless:
            options.add_argument("--headless")
        
        # 忽略证书错误
        options.add_argument("--ignore-certificate-errors")
        options.add_argument("--allow-running-insecure-content")
        
        # 设置用户代理
        options.add_argument("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        
        # 禁用自动化检测
        options.add_experimental_option("excludeSwitches", ["enable-automation"])
        options.add_experimental_option("useAutomationExtension", False)
        
        # 设置下载路径
        prefs = {
            "download.default_directory": self.settings.DOWNLOAD_DIR,
            "download.prompt_for_download": False,
            "download.directory_upgrade": True,
            "safebrowsing.enabled": True
        }
        options.add_experimental_option("prefs", prefs)
        
        # 优先使用本地驱动
        driver_path = os.path.join(self.settings.DRIVER_DIR, "chromedriver.exe")
        if os.path.exists(driver_path):
            self.logger.info(f"使用本地Chrome驱动: {driver_path}")
            service = ChromeService(driver_path)
            driver = webdriver.Chrome(service=service, options=options)
        else:
            self.logger.warning(f"本地驱动不存在: {driver_path}，尝试使用webdriver-manager下载")
            try:
                # 尝试使用webdriver-manager自动管理驱动
                service = ChromeService(ChromeDriverManager().install())
                driver = webdriver.Chrome(service=service, options=options)
            except Exception as e:
                raise FileNotFoundError(f"找不到Chrome驱动，且无法自动下载: {e}")
        
        # 执行CDP命令，进一步隐藏自动化特征
        driver.execute_cdp_cmd("Page.addScriptToEvaluateOnNewDocument", {
            "source": """
                Object.defineProperty(navigator, 'webdriver', {
                    get: () => undefined
                })
            """
        })
        
        return driver
    
    def _create_firefox_driver(self, headless: bool) -> webdriver.Firefox:
        """
        创建Firefox浏览器驱动
        :param headless: 是否无头模式
        :return: Firefox WebDriver
        """
        options = FirefoxOptions()
        
        # 基础配置
        options.add_argument("--start-maximized")
        
        # 无头模式
        if headless:
            options.add_argument("--headless")
        
        # 设置首选项
        profile = webdriver.FirefoxProfile()
        profile.set_preference("browser.download.folderList", 2)
        profile.set_preference("browser.download.manager.showWhenStarting", False)
        profile.set_preference("browser.download.dir", self.settings.DOWNLOAD_DIR)
        profile.set_preference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream")
        
        # 优先使用本地驱动
        driver_path = os.path.join(self.settings.DRIVER_DIR, "geckodriver.exe")
        if os.path.exists(driver_path):
            self.logger.info(f"使用本地Firefox驱动: {driver_path}")
            service = FirefoxService(driver_path)
            driver = webdriver.Firefox(service=service, options=options)
        else:
            self.logger.warning(f"本地驱动不存在: {driver_path}，尝试使用webdriver-manager下载")
            try:
                service = FirefoxService(GeckoDriverManager().install())
                driver = webdriver.Firefox(service=service, options=options)
            except Exception as e:
                raise FileNotFoundError(f"找不到Firefox驱动，且无法自动下载: {e}")
        
        return driver
    
    def _create_edge_driver(self, headless: bool) -> webdriver.Edge:
        """
        创建Edge浏览器驱动
        :param headless: 是否无头模式
        :return: Edge WebDriver
        """
        options = EdgeOptions()
        
        # 基础配置
        options.add_argument("--start-maximized")
        options.add_argument("--disable-extensions")
        options.add_argument("--disable-popup-blocking")
        
        # 无头模式
        if headless:
            options.add_argument("--headless")
        
        # 优先使用本地驱动
        driver_path = os.path.join(self.settings.DRIVER_DIR, "msedgedriver.exe")
        if os.path.exists(driver_path):
            self.logger.info(f"使用本地Edge驱动: {driver_path}")
            service = EdgeService(driver_path)
            driver = webdriver.Edge(service=service, options=options)
        else:
            self.logger.warning(f"本地驱动不存在: {driver_path}，尝试使用webdriver-manager下载")
            try:
                service = EdgeService(EdgeChromiumDriverManager().install())
                driver = webdriver.Edge(service=service, options=options)
            except Exception as e:
                raise FileNotFoundError(f"找不到Edge驱动，且无法自动下载: {e}")
        
        return driver
    
    def _configure_driver(self) -> None:
        """
        配置WebDriver
        """
        if self.driver:
            # 设置隐式等待
            self.driver.implicitly_wait(self.settings.IMPLICIT_WAIT)
            
            # 设置页面加载超时
            self.driver.set_page_load_timeout(self.settings.PAGE_LOAD_TIMEOUT)
            
            # 设置脚本执行超时
            self.driver.set_script_timeout(self.settings.SCRIPT_TIMEOUT)
            
            self.logger.info("WebDriver配置完成")
    
    def quit_driver(self) -> None:
        """
        关闭WebDriver
        """
        if self.driver:
            try:
                self.driver.quit()
                self.logger.info("WebDriver已关闭")
            except Exception as e:
                self.logger.error(f"关闭WebDriver失败: {e}")
            finally:
                self.driver = None
    
    def get_driver(self) -> webdriver:
        """
        获取当前WebDriver实例
        :return: WebDriver实例
        """
        if not self.driver:
            raise RuntimeError("WebDriver未创建，请先调用create_driver()")
        return self.driver
