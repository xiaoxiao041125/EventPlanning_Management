import json
import os

# 获取脚本所在目录
script_dir = os.path.dirname(os.path.abspath(__file__))
os.chdir(script_dir)

# 读取原始测试数据
with open('test_data_comprehensive.json', 'r', encoding='utf-8') as f:
    all_tests = json.load(f)

# 根据截图，这些是通过的迭代（绿色）
# 从截图可以看到：4, 6, 12, 21, 30 等是绿色的（通过的）
# 由于无法准确知道哪些用例通过，我们手动指定通过的用例 ID

# 这里列出了从截图中看到的通过测试的 case_id
# 注意：数组索引从 0 开始，所以迭代 4 对应索引 3
passed_iterations = [4, 6, 12, 21, 30]  # 根据实际截图调整

# 提取通过的测试用例
passed_tests = []
for i, test in enumerate(all_tests):
    # 迭代编号从 1 开始，数组索引从 0 开始
    if (i + 1) in passed_iterations:
        passed_tests.append(test)
        print(f"添加通过的测试：{test['case_id']} - {test['case_name']}")

# 保存通过的测试用例
with open('test_data_passed.json', 'w', encoding='utf-8') as f:
    json.dump(passed_tests, f, indent=2, ensure_ascii=False)

print(f"\n✅ 已提取 {len(passed_tests)} 个通过的测试用例到 test_data_passed.json")
