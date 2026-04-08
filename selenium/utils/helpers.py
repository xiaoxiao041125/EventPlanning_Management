# -*- coding: utf-8 -*-
"""
辅助工具类 - Utils Layer
提供各种辅助功能
"""
import os
import re
import random
import string
from datetime import datetime, timedelta
from pathlib import Path
from typing import Optional


class StringHelper:
    """
    字符串辅助类
    """
    
    @staticmethod
    def generate_random_string(length: int = 8) -> str:
        """
        生成随机字符串
        :param length: 长度
        :return: 随机字符串
        """
        return ''.join(random.choices(string.ascii_letters + string.digits, k=length))
    
    @staticmethod
    def generate_random_phone() -> str:
        """
        生成随机手机号
        :return: 手机号
        """
        prefixes = ["138", "139", "137", "136", "135", "134", "150", "151", "152", "157", "158", "159"]
        prefix = random.choice(prefixes)
        suffix = ''.join(random.choices(string.digits, k=8))
        return prefix + suffix
    
    @staticmethod
    def generate_random_email() -> str:
        """
        生成随机邮箱
        :return: 邮箱
        """
        domains = ["gmail.com", "qq.com", "163.com", "outlook.com", "hotmail.com"]
        username = StringHelper.generate_random_string(8)
        domain = random.choice(domains)
        return f"{username}@{domain}"
    
    @staticmethod
    def extract_number(text: str) -> Optional[int]:
        """
        从文本中提取数字
        :param text: 文本
        :return: 数字或None
        """
        numbers = re.findall(r'\d+', text)
        if numbers:
            return int(numbers[0])
        return None


class DateTimeHelper:
    """
    日期时间辅助类
    """
    
    @staticmethod
    def get_current_date(format: str = "%Y-%m-%d") -> str:
        """
        获取当前日期
        :param format: 日期格式
        :return: 日期字符串
        """
        return datetime.now().strftime(format)
    
    @staticmethod
    def get_current_datetime(format: str = "%Y-%m-%d %H:%M:%S") -> str:
        """
        获取当前日期时间
        :param format: 日期时间格式
        :return: 日期时间字符串
        """
        return datetime.now().strftime(format)
    
    @staticmethod
    def get_future_date(days: int = 1, format: str = "%Y-%m-%d") -> str:
        """
        获取未来日期
        :param days: 天数
        :param format: 日期格式
        :return: 日期字符串
        """
        future = datetime.now() + timedelta(days=days)
        return future.strftime(format)
    
    @staticmethod
    def get_past_date(days: int = 1, format: str = "%Y-%m-%d") -> str:
        """
        获取过去日期
        :param days: 天数
        :param format: 日期格式
        :return: 日期字符串
        """
        past = datetime.now() - timedelta(days=days)
        return past.strftime(format)
    
    @staticmethod
    def get_current_month() -> str:
        """
        获取当前月份
        :return: 月份字符串 (YYYY-MM)
        """
        return datetime.now().strftime("%Y-%m")


class FileHelper:
    """
    文件辅助类
    """
    
    @staticmethod
    def ensure_dir(path: Path) -> Path:
        """
        确保目录存在
        :param path: 目录路径
        :return: 目录路径
        """
        path.mkdir(parents=True, exist_ok=True)
        return path
    
    @staticmethod
    def get_latest_file(directory: Path, pattern: str = "*") -> Optional[Path]:
        """
        获取目录中最新的文件
        :param directory: 目录路径
        :param pattern: 文件匹配模式
        :return: 最新文件路径或None
        """
        if not directory.exists():
            return None
        
        files = list(directory.glob(pattern))
        if not files:
            return None
        
        return max(files, key=lambda f: f.stat().st_mtime)
    
    @staticmethod
    def clean_old_files(directory: Path, days: int = 7, pattern: str = "*"):
        """
        清理旧文件
        :param directory: 目录路径
        :param days: 保留天数
        :param pattern: 文件匹配模式
        """
        if not directory.exists():
            return
        
        cutoff = datetime.now() - timedelta(days=days)
        
        for file_path in directory.glob(pattern):
            if file_path.is_file():
                mtime = datetime.fromtimestamp(file_path.stat().st_mtime)
                if mtime < cutoff:
                    file_path.unlink()


class ValidationHelper:
    """
    验证辅助类
    """
    
    @staticmethod
    def is_valid_phone(phone: str) -> bool:
        """
        验证手机号格式
        :param phone: 手机号
        :return: 是否有效
        """
        pattern = r'^1[3-9]\d{9}$'
        return bool(re.match(pattern, phone))
    
    @staticmethod
    def is_valid_email(email: str) -> bool:
        """
        验证邮箱格式
        :param email: 邮箱
        :return: 是否有效
        """
        pattern = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
        return bool(re.match(pattern, email))
    
    @staticmethod
    def is_valid_date(date_str: str, format: str = "%Y-%m-%d") -> bool:
        """
        验证日期格式
        :param date_str: 日期字符串
        :param format: 日期格式
        :return: 是否有效
        """
        try:
            datetime.strptime(date_str, format)
            return True
        except ValueError:
            return False


class RetryHelper:
    """
    重试辅助类
    """
    
    @staticmethod
    def retry(func, max_attempts: int = 3, delay: float = 1.0, exceptions=(Exception,)):
        """
        重试执行函数
        :param func: 要执行的函数
        :param max_attempts: 最大尝试次数
        :param delay: 延迟时间（秒）
        :param exceptions: 捕获的异常类型
        :return: 函数返回值
        """
        import time
        
        last_exception = None
        for attempt in range(max_attempts):
            try:
                return func()
            except exceptions as e:
                last_exception = e
                if attempt < max_attempts - 1:
                    time.sleep(delay)
        
        raise last_exception


# 便捷函数
def generate_test_username() -> str:
    """
    生成测试用户名
    :return: 用户名
    """
    return f"test_{StringHelper.generate_random_string(6)}"


def generate_test_password() -> str:
    """
    生成测试密码
    :return: 密码
    """
    return StringHelper.generate_random_string(8)


def get_project_root() -> Path:
    """
    获取项目根目录
    :return: 项目根目录路径
    """
    return Path(__file__).parent.parent
