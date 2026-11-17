#!/usr/bin/env bash
# ==============================================================================
# CookCircle - One-Click Launcher for Linux / macOS
# ==============================================================================

set -e

echo "=========================================================="
echo "   CookCircle - Society Cooking, Simplified Launcher      "
echo "=========================================================="

if command -v docker &> /dev/null && docker info &> /dev/null; then
    echo "Docker detected. Launching complete multi-container stack..."
    docker-compose up --build -d
    echo ""
    echo "CookCircle platform started successfully!"
    echo "  - Frontend: http://localhost:5173 (or http://localhost)"
    echo "  - Backend:  http://localhost:8080/api/v1/societies"
    echo "  - MySQL:    localhost:3306 (cookcircle_db)"
    exit 0
fi

echo "Docker not running. Launching in local development mode..."
echo "1. Building frontend..."
cd frontend && npm install && npm run build && cd ..

echo "2. Building backend..."
cd backend && mvn clean test && cd ..

echo "Launch commands:"
echo "  Frontend: cd frontend && npm run dev"
echo "  Backend:  cd backend && mvn spring-boot:run"

