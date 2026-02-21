#!/bin/bash

# Script para gerenciar a aplicação Stockflow API com Docker

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Função de ajuda
show_help() {
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  build         Build da imagem Docker"
    echo "  up            Sobe os containers (development)"
    echo "  down          Para os containers"
    echo "  logs          Mostra os logs dos containers"
    echo "  clean         Remove containers, images e volumes"
    echo "  prod-build    Build para produção"
    echo "  prod-up       Sobe em modo produção"
    echo "  prod-logs     Logs do ambiente de produção"
    echo "  test          Executa os testes"
    echo "  help          Mostra esta ajuda"
    echo ""
}

# Build da aplicação
build() {
    print_info "Fazendo build da imagem Docker..."
    docker compose build --no-cache
    print_success "Build concluído!"
}

# Subir containers de desenvolvimento
up() {
    print_info "Subindo containers de desenvolvimento..."
    docker compose up -d
    
    print_info "Aguardando containers ficarem prontos..."
    sleep 30
    
    print_info "Verificando status dos containers..."
    docker compose ps
    
    print_success "Aplicação disponível em: http://localhost:8080/v1/stockflow-api"
}

# Parar containers
down() {
    print_info "Parando containers..."
    docker compose down
    print_success "Containers parados!"
}

# Mostrar logs
logs() {
    docker compose logs -f
}

# Limpeza completa
clean() {
    print_warning "Esta operação irá remover todos os containers, imagens e volumes!"
    read -p "Tem certeza? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_info "Removendo containers..."
        docker compose down -v --remove-orphans
        
        print_info "Removendo imagens..."
        docker rmi $(docker images "stockflow-api*" -q) 2>/dev/null || true
        
        print_info "Removendo volumes órfãos..."
        docker volume prune -f
        
        print_success "Limpeza concluída!"
    else
        print_info "Operação cancelada."
    fi
}

# Build para produção
prod_build() {
    print_info "Fazendo build para produção..."
    docker compose -f docker-compose.prod.yml build --no-cache
    print_success "Build de produção concluído!"
}

# Subir em produção
prod_up() {
    print_info "Subindo em modo produção..."
    docker compose -f docker-compose.prod.yml up -d
    
    print_info "Aguardando aplicação ficar pronta..."
    sleep 60
    
    print_info "Verificando status..."
    docker compose -f docker-compose.prod.yml ps
    
    print_success "Aplicação de produção iniciada!"
}

# Logs de produção
prod_logs() {
    docker compose -f docker-compose.prod.yml logs -f
}

# Executar testes
test() {
    print_info "Executando testes..."
    ./mvnw test
    print_success "Testes concluídos!"
}

# Main
case "${1:-help}" in
    build)
        build
        ;;
    up)
        up
        ;;
    down)
        down
        ;;
    logs)
        logs
        ;;
    clean)
        clean
        ;;
    prod-build)
        prod_build
        ;;
    prod-up)
        prod_up
        ;;
    prod-logs)
        prod_logs
        ;;
    test)
        test
        ;;
    help|*)
        show_help
        ;;
esac