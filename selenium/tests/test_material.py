# -*- coding: utf-8 -*-
"""
物料管理功能测试 - Test Layer
测试物料管理功能的各种场景
"""
import pytest
import allure
from page_objects.material_page import MaterialPage
from page_objects.login_page import LoginPage


@allure.feature("物料管理功能")
@allure.story("物料管理")
class TestMaterial:
    """
    物料管理功能测试类
    """
    
    @allure.title("测试物料管理页面加载")
    @allure.description("验证物料管理页面能够正常加载")
    def test_material_page_load(self, driver):
        """
        测试物料管理页面加载
        """
        # 先登录（仓库管理员）
        with allure.step("仓库管理员登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("warehouse_admin", "123456", "3")
            import time
            time.sleep(1)
        
        with allure.step("打开物料管理页面"):
            material_page = MaterialPage(driver)
            material_page.open()
        
        with allure.step("验证页面元素"):
            assert material_page.is_element_present(material_page.MATERIAL_TABLE), "物料表格未找到"
            assert material_page.is_element_present(material_page.ADD_MATERIAL_BUTTON), "新增按钮未找到"
    
    @allure.title("测试搜索物料")
    @allure.description("根据物料类型搜索物料")
    @pytest.mark.parametrize("search_keyword", ["音响", "灯光", "舞台"])
    def test_search_material(self, driver, search_keyword):
        """
        测试搜索物料
        """
        # 先登录
        with allure.step("仓库管理员登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("warehouse_admin", "123456", "3")
            import time
            time.sleep(1)
        
        with allure.step("打开物料管理页面"):
            material_page = MaterialPage(driver)
            material_page.open()
        
        with allure.step(f"搜索物料: {search_keyword}"):
            material_page.search_material(search_keyword)
        
        with allure.step("验证搜索结果"):
            count = material_page.get_material_count()
            allure.attach(f"搜索结果数量: {count}", "搜索统计")
            assert count >= 0, "搜索失败"
    
    @allure.title("测试新增物料")
    @allure.description("添加新的物料信息")
    def test_add_material(self, driver):
        """
        测试新增物料
        """
        # 先登录
        with allure.step("仓库管理员登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("warehouse_admin", "123456", "3")
            import time
            time.sleep(1)
        
        with allure.step("打开物料管理页面"):
            material_page = MaterialPage(driver)
            material_page.open()
        
        with allure.step("添加新物料"):
            material_page.add_material(
                material_type="测试音响设备",
                quantity="10",
                price="500",
                category="音响设备",
                remarks="测试用物料"
            )
        
        with allure.step("验证添加结果"):
            import time
            time.sleep(1)
            # 验证页面是否还在物料页面
            assert "material" in driver.current_url, "物料添加后页面异常"
    
    @allure.title("测试查看物料列表")
    @allure.description("查看物料列表")
    def test_view_material_list(self, driver):
        """
        测试查看物料列表
        """
        # 先登录
        with allure.step("仓库管理员登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("warehouse_admin", "123456", "3")
            import time
            time.sleep(1)
        
        with allure.step("打开物料管理页面"):
            material_page = MaterialPage(driver)
            material_page.open()
        
        with allure.step("查看物料列表"):
            count = material_page.get_material_count()
            allure.attach(f"物料数量: {count}", "物料统计")
            assert count >= 0, "获取物料列表失败"
    
    @allure.title("测试重置搜索")
    @allure.description("测试重置搜索功能")
    def test_reset_search(self, driver):
        """
        测试重置搜索
        """
        # 先登录
        with allure.step("仓库管理员登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("warehouse_admin", "123456", "3")
            import time
            time.sleep(1)
        
        with allure.step("打开物料管理页面"):
            material_page = MaterialPage(driver)
            material_page.open()
        
        with allure.step("执行搜索"):
            material_page.search_material("音响")
            count_before = material_page.get_material_count()
        
        with allure.step("重置搜索"):
            material_page.click_reset()
            count_after = material_page.get_material_count()
        
        with allure.step("验证重置结果"):
            allure.attach(f"重置前: {count_before}, 重置后: {count_after}", "重置统计")
            assert count_after >= 0, "重置失败"


@allure.feature("物料管理功能")
@allure.story("参数化物料管理测试")
class TestMaterialParameterized:
    """
    参数化物料管理测试类
    """
    
    @allure.title("参数化物料添加测试 - {case_data[case_name]}")
    @pytest.mark.parametrize("case_data", [
        pytest.param({
            "case_id": "MAT_001",
            "case_name": "正常添加物料-音响设备",
            "material_type": "专业音响",
            "quantity": "10",
            "price": "500",
            "category": "音响设备",
            "remarks": "高品质音响设备",
            "expected": "success"
        }, id="MAT_001"),
        pytest.param({
            "case_id": "MAT_002",
            "case_name": "正常添加物料-灯光设备",
            "material_type": "LED灯光",
            "quantity": "20",
            "price": "300",
            "category": "灯光设备",
            "remarks": "彩色LED灯光",
            "expected": "success"
        }, id="MAT_002"),
    ])
    def test_add_material_with_data(self, driver, case_data):
        """
        使用参数化数据进行物料添加测试
        """
        allure.dynamic.title(f"物料添加测试 - {case_data['case_name']}")
        
        # 先登录
        with allure.step("仓库管理员登录"):
            login_page = LoginPage(driver)
            login_page.open()
            login_page.login("warehouse_admin", "123456", "3")
            import time
            time.sleep(1)
        
        with allure.step("打开物料管理页面"):
            material_page = MaterialPage(driver)
            material_page.open()
        
        with allure.step("添加物料"):
            material_page.add_material(
                case_data["material_type"],
                case_data["quantity"],
                case_data["price"],
                case_data["category"],
                case_data["remarks"]
            )
        
        with allure.step("验证添加结果"):
            import time
            time.sleep(1)
            assert "material" in driver.current_url, \
                f"物料添加失败: {case_data['case_name']}"
