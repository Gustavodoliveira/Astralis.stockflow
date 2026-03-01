# 🖥️ Deploy Manual no Console AWS

Execute estes comandos diretamente no seu servidor AWS:

## 📋 1. Preparar o Ambiente

```bash
# Atualizar sistema
sudo apt update && sudo apt upgrade -y

# Instalar Docker (se não tiver)
sudo apt install docker.io -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER

# Instalar Java 21 (se necessário)
sudo apt install openjdk-21-jdk -y

# Instalar Maven (se necessário)
sudo apt install maven -y
```

## 📦 2. Baixar o Código

```bash
# Clone do repositório
git clone https://github.com/Gustavodoliveira/Astralis.stockflow.git
cd Astralis.stockflow

# OU upload manual dos arquivos via SCP/SFTP
```

## 🏗️ 3. Build da Aplicação

```bash
# Build do JAR
chmod +x mvnw
./mvnw clean package -DskipTests

# Verificar se o JAR foi criado
ls -la target/stockflow-api-0.0.1-SNAPSHOT.jar
```

## 🐳 4. Build da Imagem Docker

```bash
# Build da imagem
docker build -t stockflow-api:latest .

# Verificar imagem criada
docker images | grep stockflow
```

## 🗄️ 5. Configurar Banco de Dados

```bash
# Se usar PostgreSQL local (para teste):
sudo apt install postgresql postgresql-contrib -y
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Criar banco e usuário
sudo -u postgres createuser stockflow_user -P
sudo -u postgres createdb stockflow_prod -O stockflow_user
```

## 🚀 6. Rodar a Aplicação

### Opção A: Direto com Docker

```bash
# Rodar container em produção
docker run -d \
  --name stockflow-api \
  --restart unless-stopped \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL="jdbc:postgresql://localhost:5432/stockflow_prod" \
  -e DB_USERNAME="stockflow_user" \
  -e DB_PASSWORD="sua_senha_aqui" \
  -e EXTERNAL_API_BASE_URL="https://api.iniciativaaplicativos.com.br/api" \
  -e EXTERNAL_API_TOKEN="usQjA5McLAyQLc16S9wnESl9fAk4kDYbVasJtyRzMKPPufpx7uqnH6r1128HCw6Q" \
  -e EXTERNAL_API_SECRET="lxs7KnxBIX24w88t4J169KPxc9UTgdFyfPloZdRGRMfKKJL2YxtawOnbe4zy1VlG" \
  -e PORT=8080 \
  stockflow-api:latest
```

### Opção B: Com Docker Compose

```bash
# Criar arquivo .env para produção
cat > .env << EOF
DATABASE_URL=jdbc:postgresql://localhost:5432/stockflow_prod
DB_USERNAME=stockflow_user
DB_PASSWORD=sua_senha_aqui
EXTERNAL_API_BASE_URL=https://api.iniciativaaplicativos.com.br/api
EXTERNAL_API_TOKEN=usQjA5McLAyQLc16S9wnESl9fAk4kDYbVasJtyRzMKPPufpx7uqnH6r1128HCw6Q
EXTERNAL_API_SECRET=lxs7KnxBIX24w88t4J169KPxc9UTgdFyfPloZdRGRMfKKJL2YxtawOnbe4zy1VlG
PORT=8080
SPRING_PROFILES_ACTIVE=prod
EOF

# Instalar Docker Compose
sudo apt install docker-compose -y

# Subir aplicação
docker-compose -f docker-compose.prod.yml up -d
```

## 🔍 7. Verificar Deploy

```bash
# Verificar container rodando
docker ps | grep stockflow

# Verificar logs
docker logs stockflow-api

# Testar aplicação
curl http://localhost:8080/api/health/live
curl http://localhost:8080/api/health/custom

# Testar API externa
curl "http://localhost:8080/api/external/getItemByDescription/teste"
```

## 🌐 8. Configurar Reverse Proxy (Opcional)

### Com Nginx:

```bash
# Instalar Nginx
sudo apt install nginx -y

# Configurar proxy
sudo tee /etc/nginx/sites-available/stockflow << EOF
server {
    listen 80;
    server_name seu-dominio.com;

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

# Ativar configuração
sudo ln -s /etc/nginx/sites-available/stockflow /etc/nginx/sites-enabled/
sudo systemctl restart nginx
```

## 🔥 9. Configurar Firewall

```bash
# Permitir portas necessárias
sudo ufw allow 22      # SSH
sudo ufw allow 80      # HTTP
sudo ufw allow 443     # HTTPS
sudo ufw allow 8080    # Aplicação (se acesso direto)
sudo ufw --force enable
```

## 📊 10. Monitoramento

```bash
# Ver logs da aplicação
docker logs -f stockflow-api

# Monitorar recursos
docker stats stockflow-api

# Health check contínuo
watch -n 30 'curl -s http://localhost:8080/api/health/custom | jq'
```

## 🔄 11. Comandos de Manutenção

```bash
# Parar aplicação
docker stop stockflow-api

# Reiniciar aplicação
docker restart stockflow-api

# Atualizar aplicação (novo build)
docker stop stockflow-api
docker rm stockflow-api
git pull
./mvnw clean package -DskipTests
docker build -t stockflow-api:latest .
# Executar comando docker run novamente...

# Backup do banco
docker exec stockflow-api pg_dump -U stockflow_user stockflow_prod > backup.sql

# Ver uso de espaço
docker system df
docker system prune    # Limpar recursos não utilizados
```

## 🚨 Troubleshooting

```bash
# Se der erro de permissão Docker
sudo systemctl restart docker
sudo usermod -aG docker $USER
# Fazer logout e login novamente

# Se aplicação não subir
docker logs stockflow-api
docker exec -it stockflow-api bash    # Entrar no container

# Verificar portas em uso
sudo netstat -tulpn | grep :8080

# Verificar espaço em disco
df -h
```

---

## 📋 Checklist Final:

- [ ] Sistema atualizado
- [ ] Docker instalado e funcionando
- [ ] Java 21 instalado
- [ ] Código baixado/clonado
- [ ] JAR buildado com sucesso
- [ ] Imagem Docker criada
- [ ] Banco de dados configurado
- [ ] Variáveis de ambiente definidas
- [ ] Container rodando
- [ ] Health checks passando
- [ ] API respondendo
- [ ] Firewall configurado
- [ ] Logs funcionando
