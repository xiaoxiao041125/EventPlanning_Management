@echo off
chcp 65001 >nul
echo ==========================================
echo Event Planning Management - API Tests
echo ==========================================
echo.
echo Starting tests...
echo.

set COLLECTION=event_planning_collection.json
set DELAY=100
set REPORT=report.html

echo Running all tests and generating HTML report...
echo.

REM 运行整个集合并生成 HTML 报告
newman run %COLLECTION% --delay-request %DELAY% --reporters cli,html --reporter-html-export %REPORT%

echo.
echo ==========================================
echo Tests completed!
echo ==========================================
echo.
echo HTML Report: %REPORT%
echo.

REM 检查报告是否生成
if exist %REPORT% (
    echo Opening report...
    start %REPORT%
) else (
    echo Warning: Report file not generated.
)

echo.
echo Press any key to exit...
pause >nul
