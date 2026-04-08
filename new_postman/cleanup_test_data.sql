-- 清理测试数据
-- 删除测试用户
DELETE FROM user WHERE username LIKE 'test_user_%' OR username LIKE 'test_employee_%';

-- 删除测试员工
DELETE FROM employee WHERE username LIKE 'test_employee_%';

-- 删除测试员工注册申请
DELETE FROM employee_register WHERE username LIKE 'test_employee_%';

-- 提交事务
COMMIT;