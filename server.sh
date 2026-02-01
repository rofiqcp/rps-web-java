#!/bin/bash

# RPS Generator - Startup Script
# Run with: ./start.sh [backend|frontend|all]

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_DIR="$PROJECT_DIR/frontend"
SHARED_ENV_FILE="/root/otomasi/rps-web/.env.local"

load_shared_env() {
    if [ -f "$SHARED_ENV_FILE" ]; then
        set -a
        # shellcheck disable=SC1090
        source "$SHARED_ENV_FILE"
        set +a
        echo -e "${GREEN}✓ Loaded shared env: $SHARED_ENV_FILE${NC}"
    else
        echo -e "${YELLOW}⚠ Shared env not found: $SHARED_ENV_FILE${NC}"
    fi
}

print_header() {
    echo -e "${BLUE}╔══════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${NC}       ${GREEN}RPS Generator - Java + Vue.js${NC}             ${BLUE}║${NC}"
    echo -e "${BLUE}║${NC}       ${YELLOW}Universitas Diponegoro${NC}                    ${BLUE}║${NC}"
    echo -e "${BLUE}╚══════════════════════════════════════════════════╝${NC}"
    echo ""
}

check_requirements() {
    echo -e "${YELLOW}Checking requirements...${NC}"
    
    # Check Java
    if ! command -v java &> /dev/null; then
        echo -e "${RED}✗ Java not found. Please install Java 17+${NC}"
        exit 1
    else
        java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
        echo -e "${GREEN}✓ Java: $java_version${NC}"
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}✗ Maven not found. Please install Maven 3.8+${NC}"
        exit 1
    else
        mvn_version=$(mvn -v | head -1)
        echo -e "${GREEN}✓ $mvn_version${NC}"
    fi
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        echo -e "${RED}✗ Node.js not found. Please install Node.js 18+${NC}"
        exit 1
    else
        node_version=$(node -v)
        echo -e "${GREEN}✓ Node.js: $node_version${NC}"
    fi
    
    # Check npm
    if ! command -v npm &> /dev/null; then
        echo -e "${RED}✗ npm not found. Please install npm 9+${NC}"
        exit 1
    else
        npm_version=$(npm -v)
        echo -e "${GREEN}✓ npm: $npm_version${NC}"
    fi
    
    echo ""
}

start_backend() {
    echo -e "${YELLOW}Starting Backend (Spring Boot)...${NC}"
    cd "$BACKEND_DIR"
    
    # Check if OPENAI_API_KEY is set
    if [ -z "$OPENAI_API_KEY" ]; then
        echo -e "${YELLOW}⚠ OPENAI_API_KEY not set. AI features will not work.${NC}"
        echo -e "${YELLOW}  Set with: export OPENAI_API_KEY=your_key_here${NC}"
    fi
    
    # Build if needed
    if [ ! -f "target/rps-generator-*.jar" ]; then
        echo -e "${YELLOW}Building backend...${NC}"
        mvn clean package -DskipTests -q
    fi
    
    echo -e "${GREEN}Backend running at: http://localhost:8080${NC}"
    mvn spring-boot:run
}

start_frontend() {
    echo -e "${YELLOW}Starting Frontend (Vue.js)...${NC}"
    cd "$FRONTEND_DIR"
    
    # Install dependencies if needed
    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}Installing dependencies...${NC}"
        npm install
    fi
    
    echo -e "${GREEN}Frontend running at: http://localhost:5173${NC}"
    npm run dev
}

start_all() {
    echo -e "${YELLOW}Starting both Backend and Frontend...${NC}"
    
    # Start backend in background
    cd "$BACKEND_DIR"
    if [ ! -f "target/rps-generator-*.jar" ]; then
        echo -e "${YELLOW}Building backend...${NC}"
        mvn clean package -DskipTests -q
    fi
    mvn spring-boot:run &
    BACKEND_PID=$!
    
    # Wait for backend to start
    echo -e "${YELLOW}Waiting for backend to start...${NC}"
    sleep 10
    
    # Start frontend in background
    cd "$FRONTEND_DIR"
    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}Installing frontend dependencies...${NC}"
        npm install
    fi
    npm run dev &
    FRONTEND_PID=$!
    
    echo ""
    echo -e "${GREEN}╔══════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║ Services started successfully!                   ║${NC}"
    echo -e "${GREEN}║                                                  ║${NC}"
    echo -e "${GREEN}║ Backend:  http://localhost:8080                  ║${NC}"
    echo -e "${GREEN}║ Frontend: http://localhost:5173                  ║${NC}"
    echo -e "${GREEN}║                                                  ║${NC}"
    echo -e "${GREEN}║ Press Ctrl+C to stop all services                ║${NC}"
    echo -e "${GREEN}╚══════════════════════════════════════════════════╝${NC}"
    
    # Handle shutdown
    trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit 0" SIGINT SIGTERM
    
    wait
}

# Main
print_header
load_shared_env
check_requirements

case "${1:-all}" in
    backend)
        start_backend
        ;;
    frontend)
        start_frontend
        ;;
    all)
        start_all
        ;;
    *)
        echo "Usage: $0 [backend|frontend|all]"
        exit 1
        ;;
esac
