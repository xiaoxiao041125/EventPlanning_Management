-- 薪资记录表
CREATE TABLE IF NOT EXISTS `salary_record` (
  `record_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `year` int(4) NOT NULL COMMENT '年份',
  `month` int(2) NOT NULL COMMENT '月份',
  `base_salary` decimal(10,2) NOT NULL COMMENT '基本工资',
  `performance_salary` decimal(10,2) DEFAULT 0.00 COMMENT '绩效/提成/加班费',
  `total_salary` decimal(10,2) NOT NULL COMMENT '应发工资',
  `salary_status` tinyint(1) DEFAULT 0 COMMENT '0待发放 1已发放',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_year_month` (`year`,`month`),
  UNIQUE KEY `uk_user_year_month` (`user_id`,`year`,`month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资记录表';

-- 员工工作记录表（用于工人计算工作天数）
CREATE TABLE IF NOT EXISTS `employee_work` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '员工唯一标识，关联user表的userId',
  `work_start_time` datetime NOT NULL COMMENT '工作起始时间',
  `work_end_time` datetime NOT NULL COMMENT '工作结束时间',
  `demand_id` int(11) NOT NULL COMMENT '活动id',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_work_date` (`work_start_time`),
  KEY `idx_demand_id` (`demand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工工作记录表';