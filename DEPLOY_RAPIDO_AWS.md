# 🚀 Deploy Rápido na AWS - StockFlow API

## ✅ Imagem Docker Pronta!

A imagem `stockflow-api:latest` (526MB) foi criada com sucesso e está pronta para deploy!

## 📦 Opção 1: Docker Hub (MAIS RÁPIDO)

### 1. Fazer Login no Docker Hub

```bash
docker login
```

### 2. Taguear e Fazer Push

```bash
# Substitua SEU_USUARIO pelo seu usuário do Docker Hub
export DOCKER_USER="SEU_USUARIO"

# Taguear a imagem
docker tag stockflow-api:latest $DOCKER_USER/stockflow-api:latest

# Fazer push
docker push $DOCKER_USER/stockflow-api:latest
```

### 3. Baixar na AWS (EC2)

```bash
# No servidor EC2
docker pull SEU_USUARIO/stockflow-api:latest

# Rodar na produção
docker run -d \
  --name stockflow-prod \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e EXTERNAL_API_TOKEN="usQjA5McLAyQLc16S9wnESl9fAk4kDYbVasJtyRzMKPPufpx7uqnH6r1128HCw6Q" \
  -e EXTERNAL_API_SECRET="lxs7KnxBIX24w88t4J169KPxc9UTgdFyfPloZdRGRMfKKJL2YxtawOnbe4zy1VlG" \
  -e DATABASE_URL="jdbc:postgresql://SEU_RDS_ENDPOINT:5432/stockflow_prod" \
  -e DB_USERNAME="stockflow_admin" \
  -e DB_PASSWORD="SUA_SENHA" \
  --restart unless-stopped \
  SEU_USUARIO/stockflow-api:latest
```

## 🏭 Opção 2: AWS ECR (RECOMENDADO PARA PRODUÇÃO)

### 1. Configurar AWS CLI

```bash
aws configure
# Inserir: Access Key, Secret Key, Region (us-east-1)
```

### 2. Criar Repositório ECR

```bash
aws ecr create-repository --repository-name stockflow-api --region us-east-1
```

### 3. Login no ECR

```bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com
```

### 4. Push para ECR

```bash
# Substitua ACCOUNT_ID pelo seu ID da conta AWS
export AWS_ACCOUNT_ID="123456789012"
export ECR_REPO="$AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/stockflow-api"

# Taguear para ECR
docker tag stockflow-api:latest $ECR_REPO:latest

# Push para ECR
docker push $ECR_REPO:latest
```

## 🎯 Deploy com Docker Compose (SIMPLES)

Crie um arquivo `docker-compose.prod.yml` na AWS:

```yaml
version: "3.8"
services:
  stockflow-api:
    image: SEU_USUARIO/stockflow-api:latest # ou ECR URL
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      EXTERNAL_API_TOKEN: "usQjA5McLAyQLc16S9wnESl9fAk4kDYbVasJtyRzMKPPufpx7uqnH6r1128HCw6Q"
      EXTERNAL_API_SECRET: "lxs7KnxBIX24w88t4J169KPxc9UTgdFyfPloZdRGRMfKKJL2YxtawOnbe4zy1VlG"
      DATABASE_URL: "jdbc:postgresql://SEU_RDS_ENDPOINT:5432/stockflow_prod"
      DB_USERNAME: "stockflow_admin"
      DB_PASSWORD: "SUA_SENHA"
    restart: unless-stopped
```

### Rodar na AWS:

```bash
docker-compose -f docker-compose.prod.yml up -d
```

## ⚡ Setup Rápido EC2

### 1. Criar instância EC2

```bash
# Amazon Linux 2 t3.small (ou maior)
# Security Group: HTTP (80), HTTPS (443), SSH (22), Custom TCP (8080)
```

### 2. Instalar Docker no EC2

```bash
sudo yum update -y
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -a -G docker ec2-user

# Instalar Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 3. Deploy

```bash
# Fazer logout e login novamente para docker group
exit
# Conectar novamente via SSH

# Baixar e rodar aplicação
docker pull SEU_USUARIO/stockflow-api:latest
# Usar os comandos docker run acima
```

## 🔍 Health Check

Após o deploy, verifique:

```bash
# Container Status
docker ps

# Logs da aplicação
docker logs stockflow-prod

# Health endpoint
curl http://SEU_EC2_IP:8080/api/health
```

## 📊 Comandos Úteis

```bash
# Ver logs em tempo real
docker logs -f stockflow-prod

# Restart do container
docker restart stockflow-prod

# Atualizar aplicação
docker pull SEU_USUARIO/stockflow-api:latest
docker stop stockflow-prod
docker rm stockflow-prod
# Depois rodar docker run novamente

# Backup da aplicação (se necessário)
docker commit stockflow-prod stockflow-backup:$(date +%Y%m%d)
```

## 🚨 Configurações de Produção

### Variáveis Obrigatórias:

- `SPRING_PROFILES_ACTIVE=prod`
- `EXTERNAL_API_TOKEN` - Token da API externa
- `EXTERNAL_API_SECRET` - Secret da API externa
- `DATABASE_URL` - URL do banco PostgreSQL
- `DB_USERNAME` - Usuário do banco
- `DB_PASSWORD` - Senha do banco

### Portas:

- **Aplicação**: 8080
- **Health Check**: GET /api/health

---

## 📞 Suporte

Se houver problemas:

1. Verificar logs: `docker logs stockflow-prod`
2. Testar health: `curl http://localhost:8080/api/health`
3. Verificar conectividade com banco de dados
4. Validar variáveis de ambiente

**🎉 Sua aplicação está pronta para produção!**
