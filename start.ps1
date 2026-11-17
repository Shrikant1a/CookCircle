# ==============================================================================
# CookCircle - One-Click Launcher for Windows PowerShell
# ==============================================================================

param (
    [switch]$Local,
    [switch]$Docker
)

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "   CookCircle - Society Cooking, Simplified Launcher      " -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Cyan

if ($Docker -or (-not $Local)) {
    Write-Host "`n[1/3] Checking Docker daemon..." -ForegroundColor Yellow
    $dockerRunning = docker info 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Docker is running. Starting full stack via docker-compose..." -ForegroundColor Green
        docker-compose up --build -d
        Write-Host "`nAll services started successfully in Docker!" -ForegroundColor Green
        Write-Host "  - Frontend: http://localhost:5173 (or http://localhost)" -ForegroundColor Cyan
        Write-Host "  - Backend:  http://localhost:8080/api/v1/societies" -ForegroundColor Cyan
        Write-Host "  - MySQL:    localhost:3306 (Database: cookcircle_db)" -ForegroundColor Cyan
        exit 0
    } else {
        Write-Host "Docker daemon not available. Falling back to Local Development mode..." -ForegroundColor Yellow
    }
}

Write-Host "`nStarting CookCircle in Local Development Mode..." -ForegroundColor Yellow

# Step 1: Frontend Build check
Write-Host "`n[1/2] Verifying Frontend Assets..." -ForegroundColor Yellow
npm run build --prefix frontend
if ($LASTEXITCODE -ne 0) {
    Write-Host "Frontend build failed. Please check errors above." -ForegroundColor Red
    exit 1
}

# Step 2: Backend Test & Packaging
Write-Host "`n[2/2] Running Backend Verification Suite..." -ForegroundColor Yellow
$env:JAVA_HOME = "C:\Program Files\Java\jdk-18.0.1.1"
& "s:\CookCircle\tools\apache-maven-3.9.6\bin\mvn.cmd" test -f backend/pom.xml -q

Write-Host "`nCookCircle Ready for Local Launch!" -ForegroundColor Green
Write-Host "To start the frontend dev server:" -ForegroundColor White
Write-Host "  npm run dev --prefix frontend" -ForegroundColor Cyan
Write-Host "To run the backend Spring Boot app:" -ForegroundColor White
Write-Host "  s:\CookCircle\tools\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run -f backend/pom.xml" -ForegroundColor Cyan

