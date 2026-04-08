#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
测试运行脚本
提供多种方式运行测试
"""
import os
import sys
import argparse
import subprocess
from pathlib import Path


def run_tests(test_path="tests", browser="chrome", headless=False, 
              parallel=False, workers=2, generate_report=True, 
              markers=None, html_report=True):
    """
    运行测试
    :param test_path: 测试路径
    :param browser: 浏览器类型
    :param headless: 是否无头模式
    :param parallel: 是否并行执行
    :param workers: 并行工作进程数
    :param generate_report: 是否生成Allure报告
    :param markers: 测试标记
    :param html_report: 是否生成HTML报告
    """
    # 构建pytest命令
    cmd = ["pytest", test_path, "-v"]
    
    # 浏览器配置
    cmd.extend(["--browser", browser])
    if headless:
        cmd.append("--headless")
    
    # 并行执行
    if parallel:
        cmd.extend(["-n", str(workers)])
    
    # 测试标记
    if markers:
        cmd.extend(["-m", markers])
    
    # HTML报告
    if html_report:
        report_dir = Path("reports")
        report_dir.mkdir(exist_ok=True)
        html_report_path = report_dir / "test_report.html"
        cmd.extend(["--html", str(html_report_path), "--self-contained-html"])
    
    # Allure报告
    if generate_report:
        allure_results = Path("reports/allure-results")
        allure_results.mkdir(parents=True, exist_ok=True)
        cmd.extend(["--alluredir", str(allure_results)])
    
    # 打印命令
    print(f"执行命令: {' '.join(cmd)}")
    print("-" * 80)
    
    # 执行测试
    result = subprocess.run(cmd)
    
    # 生成Allure报告
    if generate_report and result.returncode in [0, 1]:  # 0=成功, 1=测试失败
        generate_allure_report()
    
    return result.returncode


def generate_allure_report():
    """
    生成Allure HTML报告
    """
    allure_results = Path("reports/allure-results")
    allure_report = Path("reports/allure-report")
    
    if not allure_results.exists():
        print("Allure结果目录不存在，跳过报告生成")
        return
    
    # 检查allure命令是否可用
    try:
        subprocess.run(["allure", "--version"], capture_output=True, check=True)
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("警告: Allure命令行工具未安装，无法生成HTML报告")
        print("请安装Allure: https://docs.qameta.io/allure/")
        return
    
    # 生成报告
    cmd = [
        "allure", "generate",
        str(allure_results),
        "-o", str(allure_report),
        "--clean"
    ]
    
    print(f"\n生成Allure报告: {' '.join(cmd)}")
    result = subprocess.run(cmd)
    
    if result.returncode == 0:
        print(f"\nAllure报告已生成: {allure_report.absolute()}")
        print(f"查看报告: allure open {allure_report.absolute()}")


def serve_allure_report():
    """
    启动Allure报告服务
    """
    allure_results = Path("reports/allure-results")
    
    if not allure_results.exists():
        print("Allure结果目录不存在")
        return
    
    cmd = ["allure", "serve", str(allure_results)]
    print(f"启动Allure服务: {' '.join(cmd)}")
    subprocess.run(cmd)


def main():
    """
    主函数
    """
    parser = argparse.ArgumentParser(
        description="运行UI自动化测试",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  # 运行所有测试
  python run_tests.py
  
  # 运行登录测试
  python run_tests.py -p tests/test_login.py
  
  # 使用Firefox浏览器运行
  python run_tests.py -b firefox
  
  # 无头模式运行
  python run_tests.py --headless
  
  # 并行执行测试
  python run_tests.py --parallel -w 4
  
  # 运行冒烟测试
  python run_tests.py -m smoke
  
  # 生成并查看Allure报告
  python run_tests.py --serve
        """
    )
    
    parser.add_argument(
        "-p", "--path",
        default="tests",
        help="测试路径 (默认: tests)"
    )
    
    parser.add_argument(
        "-b", "--browser",
        default="chrome",
        choices=["chrome", "firefox", "edge"],
        help="浏览器类型 (默认: chrome)"
    )
    
    parser.add_argument(
        "--headless",
        action="store_true",
        help="启用无头模式"
    )
    
    parser.add_argument(
        "--parallel",
        action="store_true",
        help="并行执行测试"
    )
    
    parser.add_argument(
        "-w", "--workers",
        type=int,
        default=2,
        help="并行工作进程数 (默认: 2)"
    )
    
    parser.add_argument(
        "-m", "--markers",
        default=None,
        help="测试标记 (如: smoke, login, activity)"
    )
    
    parser.add_argument(
        "--no-allure",
        action="store_true",
        help="不生成Allure报告"
    )
    
    parser.add_argument(
        "--no-html",
        action="store_true",
        help="不生成HTML报告"
    )
    
    parser.add_argument(
        "--serve",
        action="store_true",
        help="启动Allure报告服务"
    )
    
    args = parser.parse_args()
    
    # 如果指定了serve，直接启动服务
    if args.serve:
        serve_allure_report()
        return
    
    # 运行测试
    exit_code = run_tests(
        test_path=args.path,
        browser=args.browser,
        headless=args.headless,
        parallel=args.parallel,
        workers=args.workers,
        generate_report=not args.no_allure,
        markers=args.markers,
        html_report=not args.no_html
    )
    
    sys.exit(exit_code)


if __name__ == "__main__":
    main()
