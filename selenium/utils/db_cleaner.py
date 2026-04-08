# -*- coding: utf-8 -*-
"""
数据库清理工具类
用于清理测试产生的数据
"""

import pymysql
from typing import Optional
import logging

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class DatabaseCleaner:
    """数据库清理器"""
    
    def __init__(self, host: str = 'localhost', port: int = 3306, 
                 user: str = 'root', password: str = '', 
                 database: str = 'eventplanning_management'):
        """
        初始化数据库连接配置
        :param host: 数据库主机
        :param port: 数据库端口
        :param user: 数据库用户名
        :param password: 数据库密码
        :param database: 数据库名
        """
        self.host = host
        self.port = port
        self.user = user
        self.password = password
        self.database = database
        self.connection: Optional[pymysql.Connection] = None
    
    def connect(self):
        """建立数据库连接"""
        try:
            # 如果密码为空，不传递 password 参数
            connect_kwargs = {
                'host': self.host,
                'port': self.port,
                'user': self.user,
                'database': self.database,
                'charset': 'utf8mb4',
                'cursorclass': pymysql.cursors.DictCursor
            }
            
            # 只有在密码不为空时才添加 password 参数
            if self.password:
                connect_kwargs['password'] = self.password
            
            self.connection = pymysql.connect(**connect_kwargs)
            logger.info(f"数据库连接成功：{self.host}:{self.port}/{self.database}")
        except Exception as e:
            logger.error(f"数据库连接失败：{e}")
            raise
    
    def disconnect(self):
        """关闭数据库连接"""
        if self.connection:
            self.connection.close()
            logger.info("数据库连接已关闭")
    
    def clean_user_activity_demand(self, userid: int) -> int:
        """
        清理指定用户的活动申请数据
        :param userid: 用户 ID
        :return: 删除的记录数
        """
        if not self.connection:
            self.connect()
        
        try:
            with self.connection.cursor() as cursor:
                # 先查询有多少条记录
                select_sql = "SELECT COUNT(*) as count FROM activity_demand WHERE userid = %s"
                cursor.execute(select_sql, (userid,))
                result = cursor.fetchone()
                count = result['count'] if result else 0
                logger.info(f"找到 userid={userid} 的活动申请记录：{count} 条")
                
                # 执行删除操作
                delete_sql = "DELETE FROM activity_demand WHERE userid = %s"
                cursor.execute(delete_sql, (userid,))
                self.connection.commit()
                
                deleted_count = cursor.rowcount
                logger.info(f"成功删除 userid={userid} 的活动申请记录：{deleted_count} 条")
                
                return deleted_count
        except Exception as e:
            logger.error(f"清理数据失败：{e}")
            if self.connection:
                self.connection.rollback()
            raise
    
    def clean_all_test_data(self, userid: int):
        """
        清理所有测试数据（如果有多个表需要清理，可以在这里扩展）
        :param userid: 用户 ID
        """
        if not self.connection:
            self.connect()
        
        try:
            # 清理 activity_demand 表
            self.clean_user_activity_demand(userid)
            
            # 如果还有其他表需要清理，可以在这里添加
            # 例如：self.clean_user_activity_material(userid)
            
            logger.info(f"用户 {userid} 的所有测试数据清理完成")
        except Exception as e:
            logger.error(f"清理所有测试数据失败：{e}")
            raise
    
    def __enter__(self):
        """上下文管理器入口"""
        self.connect()
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        """上下文管理器出口"""
        self.disconnect()
