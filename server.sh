#!/bin/bash

# RPS Generator - Server Manager
# For running already-built applications
# Run with: ./server.sh [command]

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_DIR="$PROJECT_DIR/frontend"
SHARED_ENV_FILE="/root/otomasi/rps-web/.env.local"

load_shared_env() {
    if [ -f "$SHARED_ENV_FILE" ]; then
        set -a
        source "$SHARED_ENV_FILE"
        set +a
        echo -e "   ${GREEN}✓${NC} Loaded: $SHARED_ENV_FILE"
    else
        echo -e "   ${YELLOW}⚠${NC} Env file not found: $SHARED_ENV_FILE"
    fi
}

print_header() {
    echo ""
    echo -e "${BLUE}╔════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║    RPS Generator - Server Manager              ║${NC}"
    echo -e "${BLUE}║    Spring Boot + Vue.js + OpenAI               ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════╝${NC}"
    echo ""
}

check_requirements() {
    echo -e "${YELLOW}🔍 Checking Requirements...${NC}"
    local missing=0
    
    if ! command -v java &> /dev/null; then
        echo -e "   ${RED}✗${NC} Java: Not installed"
        missing=1
    else
        java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
        echo -e "   ${GREEN}✓${NC} Java: $java_version"
    fi
    
    if ! command -v mvn &> /dev/null; then
        echo -e "   ${RED}✗${NC} Maven: Not installed"
        missing=1
    else
        echo -e "   ${GREEN}✓${NC} Maven: $(mvn -v | head -1)"
    fi
    
    if ! command -v node &> /dev/null; then
        echo -e "   ${RED}✗${NC} Node.js: Not installed"
        missing=1
    else
        echo -e "   ${GREEN}✓${NC} Node.js: $(node -v)"
    fi
    
    if ! command -v npm &> /dev/null; then
        echo -e "   ${RED}✗${NC} npm: Not installed"
        missing=1
    else
        echo -e "   ${GREEN}✓${NC} npm: $(npm -v)"
    fi
    
    if [ $missing -eq 1 ]; then
        echo ""
        echo -e "${RED}❌ Please install missing dependencies${NC}"
        exit 1
    fi
    echo ""
}

start_backend() {
    echo -e "${YELLOW}🚀 Starting Backend (Spring Boot)...${NC}"
    
    if [ -z "$OPENAI_API_KEY" ]; then
        echo -e "   ${YELLOW}⚠${NC} OPENAI_API_KEY not set. AI features disabled."
    fi
    
    cd "$BACKEND_DIR"
    echo -e "   ${GREEN}✓${NC} Backend running at: http://localhost:8080"
    mvn spring-boot:run 2>&1 | grep -v "^\[" | head -5 &
    BACKEND_PID=$!
    echo -e "   ${CYAN}PID: $BACKEND_PID${NC}"
}

start_frontend() {
    echo -e "${YELLOW}🚀 Starting Frontend (Vue.js)...${NC}"
    
    cd "$FRONTEND_DIR"
    if [ ! -d "node_modules" ]; then
        echo -e "   ${YELLOW}📦 Installing dependencies...${NC}"
        npm install -q
    fi
    
    echo -e "   ${GREEN}✓${NC} Frontend running at: http://localhost:5173"
    npm run dev 2>&1 &
    FRONTEND_PID=$!
    echo -e "   ${CYAN}PID: $FRONTEND_PID${NC}"
}

start_all() {
    echo -e "${YELLOW}🚀 Starting Backend and Frontend...${NC}"
    echo ""
    
    start_backend
    sleep 3
    start_frontend
    sleep 2
    
    echo ""
    echo -e "${GREEN}╔════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║ ✅ Services Started Successfully!              ║${NC}"
    echo -e "${GREEN}║                                                ║${NC}"
    echo -e "${GREEN}║ 📱 Frontend: http://localhost:5173             ║${NC}"
    echo -e "${GREEN}║ 🔧 Backend:  http://localhost:8080             ║${NC}"
    echo -e "${GREEN}║                                                ║${NC}"
    echo -e "${GREEN}║ Press Ctrl+C to stop all services              ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════╝${NC}"
    echo ""
    
    trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; echo ''; echo -e '${YELLOW}Services stopped${NC}'; exit 0" SIGINT SIGTERM
    wait
}

stop_all() {
    echo -e "${YELLOW}🛑 Stopping Services...${NC}"
    pkill -f "mvn spring-boot:run" 2>/dev/null && echo -e "   ${GREEN}✓${NC} Backend stopped"
    pkill -f "npm run dev" 2>/dev/null && echo -e "   ${GREEN}✓${NC} Frontend stopped"
    echo ""
}

check_status() {
    echo -e "${YELLOW}📊 Service Status:${NC}"
    echo ""
    
    if ss -tlnp 2>/dev/null | grep -q ":8080 "; then
        echo -e "   ${GREEN}✓${NC} Backend (8080):  RUNNING"
    else
        echo -e "   ${RED}✗${NC} Backend (8080):  STOPPED"
    fi
    
    if ss -tlnp 2>/dev/null | grep -q ":5173 "; then
        echo -e "   ${GREEN}✓${NC} Frontend (5173): RUNNING"
    else
        echo -e "   ${RED}✗${NC} Frontend (5173): STOPPED"
    fi
    
    echo ""
    echo -e "${CYAN}🌐 Access URLs:${NC}"
    echo -e "   Frontend: http://localhost:5173"
    echo -e "   Backend:  http://localhost:8080"
    echo -e "   API:      http://localhost:8080/api"
    echo ""
}

show_help() {
    cat << EOF

Usage: $0 [command]

Commands:
  start       - Start backend and frontend
  stop        - Stop all services
  backend     - Start backend only
  frontend    - Start frontend only
  status      - Check service status
  help        - Show this help message

Examples:
  $0 start       # Start both services
  $0 backend     # Start backend only
  $0 status      # Check what's running

Environment:
  OPENAI_API_KEY must be set in: $SHARED_ENV_FILE

EOF
}

# Main
print_header
load_shared_env
check_requirements

case "${1:-}" in
    start|"")
        start_all
        ;;
    stop)
        stop_all
        ;;
    status|ps)
        check_status
        ;;
    backend)
        start_backend
        wait
        ;;
    frontend)
        start_frontend
        wait
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        if [ -z "$1" ]; then
            echo "Select an option:"
            echo "  1) Start all services"
            echo "  2) Start backend only"
            echo "  3) Start frontend only"
            echo "  4) Stop services"
            echo "  5) Check status"
            echo "  6) Help"
            echo ""
            read -p "Enter choice [1-6]: " choice
            case $choice in
                1) start_all ;;
                2) start_backend; wait ;;
                3) start_frontend; wait ;;
                4) stop_all ;;
                5) check_status ;;
                6) show_help ;;
                *) echo "Invalid choice" ;;
            esac
        else
            echo -e "${RED}Unknown command: $1${NC}"
            show_help
        fi
        ;;
esac
