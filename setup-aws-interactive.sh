#!/bin/bash

# 🖥️ Setup Interativo para AWS Console
# Execute este script no seu servidor AWS

set -e

echo "🚀 SETUP STOCKFLOW API - AWS CONSOLE"
echo "====================================="

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para perguntas
ask_yes_no() {
    while true; do
        read -p "$1 (y/n): " yn
        case $yn in
            [Yy]* ) return 0;;
            [Nn]* ) return 1;;
            * ) echo "Por favor responda y ou n.";;
        esac
    done
}

# Função para aguardar
wait_continue() {
    read -p "Pressione Enter para continuar..."
}

echo -e "${BLUE}📋 VERIFICANDO SISTEMA...${NC}"

# Verificar se é Ubuntu/Debian
if ! command -v apt &> /dev/null; then
    echo -e "${RED}❌ Este script é para sistemas Ubuntu/Debian${NC}"
    exit 1
fi

# 1. Atualizar sistema
if ask_yes_no "Atualizar sistema Ubuntu/Debian?"; then
    echo -e "${YELLOW}📦 Atualizando sistema...${NC}"
    sudo apt update && sudo apt upgrade -y
fi

# 2. Instalar Docker
if ! command -v docker &> /dev/null; then
    echo -e "${YELLOW}🐳 Instalando Docker...${NC}"
    sudo apt install docker.io -y
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo usermod -aG docker $USER
    echo -e "${GREEN}✅ Docker instalado!${NC}"
else
    echo -e "${GREEN}✅ Docker já está instalado${NC}"
fi

# 3. Verificar Java
if ! command -v java &> /dev/null; then
    if ask_yes_no "Instalar Java 21?"; then
        echo -e "${YELLOW}☕ Instalando Java 21...${NC}"
        sudo apt install openjdk-21-jdk -y
    fi
else
    echo -e "${GREEN}✅ Java já está instalado: $(java -version 2>&1 | head -1)${NC}"
fi

# 4. Verificar Maven
if ! command -v mvn &> /dev/null; then
    if ask_yes_no "Instalar Maven?"; then
        echo -e "${YELLOW}📦 Instalando Maven...${NC}"
        sudo apt install maven -y
    fi
else
    echo -e "${GREEN}✅ Maven já está instalado: $(mvn -v | head -1)${NC}"
fi

# 5. Baixar código
echo -e "\n${BLUE}📂 BAIXANDO CÓDIGO...${NC}"
if [ ! -d "stockflow-api" ]; then
    if ask_yes_no "Clonar repositório do GitHub?"; then
        git clone https://github.com/Gustavodoliveira/Astralis.stockflow.git stockflow-api
        cd stockflow-api
    else
        echo -e "${YELLOW}⚠️  Certifique-se de ter os arquivos da aplicação no diretório atual${NC}"
        if [ ! -f "pom.xml" ]; then
            echo -e "${RED}❌ Arquivo pom.xml não encontrado!${NC}"
            exit 1
        fi
    fi
else
    cd stockflow-api
    if ask_yes_no "Atualizar código do repositório?"; then
        git pull
    fi
fi

echo -e "${GREEN}✅ Código disponível!${NC}"

# 6. Configurar variáveis
echo -e "\n${BLUE}⚙️  CONFIGURANDO VARIÁVEIS...${NC}"

read -p "URL do banco de dados [jdbc:postgresql://localhost:5432/stockflow_prod]: " db_url
db_url=${db_url:-"jdbc:postgresql://localhost:5432/stockflow_prod"}

read -p "Usuário do banco [stockflow_user]: " db_user
db_user=${db_user:-"stockflow_user"}

read -s -p "Senha do banco: " db_password
echo

read -p "Porta da aplicação [8080]: " app_port
app_port=${app_port:-"8080"}

# Criar arquivo .env
cat > .env << EOF
DATABASE_URL=$db_url
DB_USERNAME=$db_user
DB_PASSWORD=$db_password
EXTERNAL_API_BASE_URL=https://api.iniciativaaplicativos.com.br/api
EXTERNAL_API_TOKEN=usQjA5McLAyQLc16S9wnESl9fAk4kDYbVasJtyRzMKPPufpx7uqnH6r1128HCw6Q
EXTERNAL_API_SECRET=lxs7KnxBIX24w88t4J169KPxc9UTgdFyfPloZdRGRMfKKJL2YxtawOnbe4zy1VlG
PORT=$app_port
SPRING_PROFILES_ACTIVE=prod
EOF

echo -e "${GREEN}✅ Arquivo .env criado!${NC}"

# 7. Build da aplicação
echo -e "\n${BLUE}🏗️  FAZENDO BUILD...${NC}"

if ask_yes_no "Fazer build do JAR?"; then
    chmod +x mvnw
    ./mvnw clean package -DskipTests
    
    if [ -f "target/stockflow-api-0.0.1-SNAPSHOT.jar" ]; then
        echo -e "${GREEN}✅ JAR criado com sucesso!${NC}"
    else
        echo -e "${RED}❌ Erro ao criar JAR${NC}"
        exit 1
    fi
fi

# 8. Build da imagem Docker
echo -e "\n${BLUE}🐳 CRIANDO IMAGEM DOCKER...${NC}"

if ask_yes_no "Fazer build da imagem Docker?"; then
    docker build -t stockflow-api:latest .
    
    if docker images | grep -q stockflow-api; then
        echo -e "${GREEN}✅ Imagem Docker criada!${NC}"
        docker images | grep stockflow-api
    else
        echo -e "${RED}❌ Erro ao criar imagem Docker${NC}"
        exit 1
    fi
fi

# 9. Configurar banco (opcional)
echo -e "\n${BLUE}🗄️  BANCO DE DADOS...${NC}"

if ask_yes_no "Configurar PostgreSQL local?"; then
    if ! command -v psql &> /dev/null; then
        sudo apt install postgresql postgresql-contrib -y
        sudo systemctl start postgresql
        sudo systemctl enable postgresql
    fi
    
    echo -e "${YELLOW}Configurando banco...${NC}"
    sudo -u postgres psql << EOF
CREATE USER $db_user WITH PASSWORD '$db_password';
CREATE DATABASE stockflow_prod OWNER $db_user;
GRANT ALL PRIVILEGES ON DATABASE stockflow_prod TO $db_user;
\q
EOF
    
    echo -e "${GREEN}✅ Banco configurado!${NC}"
fi

# 10. Rodar aplicação
echo -e "\n${BLUE}🚀 INICIANDO APLICAÇÃO...${NC}"

if ask_yes_no "Rodar aplicação agora?"; then
    # Parar container existente se houver
    docker stop stockflow-api 2>/dev/null || true
    docker rm stockflow-api 2>/dev/null || true
    
    # Rodar novo container
    docker run -d \
        --name stockflow-api \
        --restart unless-stopped \
        -p $app_port:8080 \
        --env-file .env \
        stockflow-api:latest
    
    echo -e "${GREEN}✅ Aplicação iniciada!${NC}"
    
    # Aguardar inicializar
    echo -e "${YELLOW}⏳ Aguardando aplicação inicializar...${NC}"
    sleep 10
    
    # Testar
    if curl -s http://localhost:$app_port/api/health/live > /dev/null; then
        echo -e "${GREEN}✅ Aplicação funcionando!${NC}"
        echo -e "${BLUE}🌐 Acesse: http://$(curl -s ifconfig.me):$app_port/api/health/custom${NC}"
    else
        echo -e "${YELLOW}⚠️  Verificando logs...${NC}"
        docker logs stockflow-api
    fi
fi

# 11. Configurar firewall
echo -e "\n${BLUE}🔥 FIREWALL...${NC}"

if ask_yes_no "Configurar firewall UFW?"; then
    sudo ufw allow 22
    sudo ufw allow 80
    sudo ufw allow 443
    sudo ufw allow $app_port
    sudo ufw --force enable
    echo -e "${GREEN}✅ Firewall configurado!${NC}"
fi

# Resumo final
echo -e "\n${GREEN}🎉 DEPLOY CONCLUÍDO!${NC}"
echo "========================="
echo -e "🌐 Aplicação: http://localhost:$app_port"
echo -e "🩺 Health Check: http://localhost:$app_port/api/health/custom"
echo -e "📊 Logs: docker logs -f stockflow-api"
echo -e "🔄 Restart: docker restart stockflow-api"
echo -e "⏹️  Parar: docker stop stockflow-api"
echo ""
echo -e "${BLUE}📋 Para mais comandos, veja: DEPLOY_MANUAL_AWS.md${NC}"