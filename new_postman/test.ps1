# Event Planning Management - API Test Script
# Execute tests with Newman (simple version)

$ErrorActionPreference = "Continue"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Event Planning Management - API Tests" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

$Collection = "event_planning_collection.json"
$Delay = 100

Write-Host "Running all interface tests..." -ForegroundColor Yellow
Write-Host "Collection: $Collection" -ForegroundColor Gray
Write-Host ""

# Run Newman tests (without HTML reporter)
newman run $Collection --delay-request $Delay

$exitCode = $LASTEXITCODE

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Tests Completed!" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

if ($exitCode -eq 0) {
    Write-Host "All tests passed!" -ForegroundColor Green
} else {
    Write-Host "Some tests failed. Exit code: $exitCode" -ForegroundColor Red
}

Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
