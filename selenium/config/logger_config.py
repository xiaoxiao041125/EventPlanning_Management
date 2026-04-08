# -*- coding: utf-8 -*-
"""
日志配置类 - Config Layer
配置和管理日志记录
"""
import logging
import sys
from pathlib import Path

from config.settings import settings


class LoggerConfig:
    """
    日志配置类
    统一配置日志格式和输出
    """
    
    @staticmethod
    def setup_logger(name: str = None, log_file: Path = None, level: str = None) -> logging.Logger:
        """
        配置并获取日志记录器
        :param name: 日志记录器名称
        :param log_file: 日志文件路径
        :param level: 日志级别
        :return: 配置好的日志记录器
        """
        logger = logging.getLogger(name or __name__)
        
        # 如果已经配置过，直接返回
        if logger.handlers:
            return logger
        
        # 设置日志级别
        log_level = level or settings.LOG_LEVEL
        logger.setLevel(getattr(logging, log_level.upper()))
        
        # 创建格式化器
        formatter = logging.Formatter(settings.LOG_FORMAT)
        
        # 控制台处理器
        console_handler = logging.StreamHandler(sys.stdout)
        console_handler.setLevel(logging.DEBUG)
        console_handler.setFormatter(formatter)
        logger.addHandler(console_handler)
        
        # 文件处理器
        if log_file is None:
            log_file = settings.LOG_FILE
        
        Path(log_file).parent.mkdir(parents=True, exist_ok=True)
        file_handler = logging.FileHandler(log_file, encoding='utf-8')
        file_handler.setLevel(logging.DEBUG)
        file_handler.setFormatter(formatter)
        logger.addHandler(file_handler)
        
        return logger
    
    @staticmethod
    def get_logger(name: str) -> logging.Logger:
        """
        获取指定名称的日志记录器
        :param name: 日志记录器名称
        :return: 日志记录器
        """
        return logging.getLogger(name)


def setup_logging():
    """
    初始化根日志配置
    """
    # 配置根日志记录器
    root_logger = logging.getLogger()
    root_logger.setLevel(logging.DEBUG)
    
    # 清除现有处理器
    root_logger.handlers.clear()
    
    # 创建格式化器
    formatter = logging.Formatter(settings.LOG_FORMAT)
    
    # 控制台处理器
    console_handler = logging.StreamHandler(sys.stdout)
    console_handler.setLevel(getattr(logging, settings.LOG_LEVEL.upper()))
    console_handler.setFormatter(formatter)
    root_logger.addHandler(console_handler)
    
    # 文件处理器
    Path(settings.LOG_FILE).parent.mkdir(parents=True, exist_ok=True)
    file_handler = logging.FileHandler(settings.LOG_FILE, encoding='utf-8')
    file_handler.setLevel(logging.DEBUG)
    file_handler.setFormatter(formatter)
    root_logger.addHandler(file_handler)
    
    # 设置第三方库的日志级别
    logging.getLogger("selenium").setLevel(logging.WARNING)
    logging.getLogger("urllib3").setLevel(logging.WARNING)
    logging.getLogger("webdriver_manager").setLevel(logging.WARNING)
