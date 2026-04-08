# -*- coding: utf-8 -*-
"""
项目配置类 - Config Layer
集中管理所有配置参数
"""
import os
from pathlib import Path


class Settings:
    """
    项目配置类
    使用单例模式确保配置唯一性
    """
    _instance = None
    
    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(Settings, cls).__new__(cls)
            cls._instance._init_settings()
        return cls._instance
    
    def _init_settings(self):
        """初始化配置"""
        # 项目根目录
        self._base_dir = Path(__file__).parent.parent
        
        # 各子目录 (使用字符串路径，避免JSON序列化问题)
        self.BASE_DIR = str(self._base_dir)
        self.DRIVER_DIR = str(self._base_dir / "driver")
        self.DATA_DIR = str(self._base_dir / "data")
        self.LOG_DIR = str(self._base_dir / "logs")
        self.REPORT_DIR = str(self._base_dir / "reports")
        self.SCREENSHOT_DIR = str(self._base_dir / "screenshots")
        self.DOWNLOAD_DIR = str(self._base_dir / "downloads")
        
        # 确保目录存在
        for dir_path in [self.LOG_DIR, self.REPORT_DIR, self.SCREENSHOT_DIR, self.DOWNLOAD_DIR]:
            Path(dir_path).mkdir(parents=True, exist_ok=True)
        
        # ==================== 浏览器配置 ====================
        self.BROWSER = os.getenv("TEST_BROWSER", "chrome")  # chrome/firefox/edge
        self.HEADLESS = os.getenv("TEST_HEADLESS", "false").lower() == "true"
        
        # ==================== 超时配置 ====================
        self.IMPLICIT_WAIT = int(os.getenv("IMPLICIT_WAIT", "10"))  # 隐式等待（秒）
        self.EXPLICIT_WAIT = int(os.getenv("EXPLICIT_WAIT", "15"))  # 显式等待（秒）
        self.PAGE_LOAD_TIMEOUT = int(os.getenv("PAGE_LOAD_TIMEOUT", "30"))  # 页面加载超时（秒）
        self.SCRIPT_TIMEOUT = int(os.getenv("SCRIPT_TIMEOUT", "20"))  # 脚本执行超时（秒）
        
        # ==================== URL配置 ====================
        self.BASE_URL = os.getenv("BASE_URL", "http://localhost:8080/EventPlanning_Management")
        self.LOGIN_URL = f"{self.BASE_URL}/html/index.html"
        self.HOME_URL = f"{self.BASE_URL}/html/index.html"
        
        # ==================== 测试数据配置 ====================
        self.TEST_DATA_FILE = str(Path(self.DATA_DIR) / "test_data.json")
        self.EXCEL_DATA_FILE = str(Path(self.DATA_DIR) / "test_data.xlsx")
        
        # ==================== 日志配置 ====================
        self.LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")  # DEBUG/INFO/WARNING/ERROR/CRITICAL
        self.LOG_FORMAT = "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
        self.LOG_FILE = str(Path(self.LOG_DIR) / f"test_{self._get_timestamp()}.log")
        
        # ==================== 报告配置 ====================
        self.REPORT_TITLE = "活动策划公司管理系统 - UI自动化测试报告"
        self.REPORT_DESCRIPTION = "基于Python+Pytest+Selenium的自动化测试"
        
        # ==================== 重试配置 ====================
        self.MAX_RETRY = int(os.getenv("MAX_RETRY", "2"))  # 失败重试次数
        self.RETRY_DELAY = int(os.getenv("RETRY_DELAY", "1"))  # 重试间隔（秒）
        
        # ==================== 截图配置 ====================
        self.SCREENSHOT_ON_FAILURE = True  # 失败时自动截图
        self.SCREENSHOT_ON_SUCCESS = False  # 成功时截图
        
        # ==================== 数据库配置（可选） ====================
        self.DB_HOST = os.getenv("DB_HOST", "localhost")
        self.DB_PORT = int(os.getenv("DB_PORT", "3306"))
        self.DB_NAME = os.getenv("DB_NAME", "eventplanning_management")
        self.DB_USER = os.getenv("DB_USER", "root")
        self.DB_PASSWORD = os.getenv("DB_PASSWORD", "123456")
    
    def _get_timestamp(self):
        """获取时间戳字符串"""
        from datetime import datetime
        return datetime.now().strftime("%Y%m%d_%H%M%S")
    
    def get_url(self, path: str) -> str:
        """
        获取完整URL
        :param path: 相对路径
        :return: 完整URL
        """
        if path.startswith("http"):
            return path
        if path.startswith("/"):
            path = path[1:]
        return f"{self.BASE_URL}/{path}"


# 全局settings实例
settings = Settings()
