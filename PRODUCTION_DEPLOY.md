# 🚀 Deploy em Produção - AWS

Este guia contém instruções para fazer deploy da aplicação StockFlow API em produção na AWS.

## 📋 Pré-requisitos

### AWS Services Necessários:

- **ECS (Elastic Container Service)** - Para rodar containers
- **ECR (Elastic Container Registry)** - Para armazenar imagens Docker
- **RDS PostgreSQL** - Banco de dados
- **Application Load Balancer (ALB)** - Load balancer
- **CloudWatch** - Monitoramento e logs
- **Systems Manager (SSM)** - Gerenciamento de secrets

### Ferramentas locais:

- Docker
- AWS CLI v2
- Java 21
- Maven 3.8+

## 🛠️ Configuração Inicial

### 1. Criar Repositório ECR

```bash
aws ecr create-repository --repository-name stockflow-api --region us-east-1
```

### 2. Configurar RDS PostgreSQL

```bash
# Criar subnet group
aws rds create-db-subnet-group \
    --db-subnet-group-name stockflow-subnet-group \
    --db-subnet-group-description "Subnet group for StockFlow" \
    --subnet-ids subnet-xxx subnet-yyy

# Criar banco de dados
aws rds create-db-instance \
    --db-instance-identifier stockflow-db \
    --db-instance-class db.t3.micro \
    --engine postgres \
    --engine-version 15.4 \
    --master-username stockflow_admin \
    --master-user-password SEU_PASSWORD_AQUI \
    --allocated-storage 20 \
    --db-name stockflow_prod \
    --db-subnet-group-name stockflow-subnet-group
```

### 3. Configurar Parameters Store (SSM)

```bash
# Database
aws ssm put-parameter --name "/stockflow-api/database-url" \
    --value "jdbc:postgresql://stockflow-db.xxxxx.us-east-1.rds.amazonaws.com:5432/stockflow_prod" \
    --type "String"

aws ssm put-parameter --name "/stockflow-api/db-username" \
    --value "stockflow_admin" \
    --type "String"

aws ssm put-parameter --name "/stockflow-api/db-password" \
    --value "SEU_PASSWORD_AQUI" \
    --type "SecureString"

# API Externa
aws ssm put-parameter --name "/stockflow-api/external-api-token" \
    --value "usQjA5McLAyQLc16S9wnESl9fAk4kDYbVasJtyRzMKPPufpx7uqnH6r1128HCw6Q" \
    --type "SecureString"

aws ssm put-parameter --name "/stockflow-api/external-api-secret" \
    --value "lxs7KnxBIX24w88t4J169KPxc9UTgdFyfPloZdRGRMfKKJL2YxtawOnbe4zy1VlG" \
    --type "SecureString"
```

### 4. Criar CloudWatch Log Group

```bash
aws logs create-log-group --log-group-name /ecs/stockflow-api
```

## 🚀 Deploy

### Opção 1: Script Automático

```bash
# Editar deploy-aws.sh com seu ACCOUNT_ID
chmod +x deploy-aws.sh
./deploy-aws.sh
```

### Opção 2: Manual

#### 1. Build local da aplicação

```bash
./mvnw clean package -DskipTests
```

#### 2. Build da imagem Docker

```bash
docker build -t stockflow-api:latest .
```

#### 3. Login no ECR

```bash
aws ecr get-login-password --region us-east-1 | \
docker login --username AWS --password-stdin YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com
```

#### 4. Tag e Push

```bash
# Tag da imagem
docker tag stockflow-api:latest YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/stockflow-api:latest

# Push para ECR
docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/stockflow-api:latest
```

#### 5. Criar/Atualizar Task Definition

```bash
# Editar task-definition.json com seu ACCOUNT_ID
aws ecs register-task-definition --cli-input-json file://task-definition.json
```

#### 6. Criar ECS Cluster (primeira vez)

```bash
aws ecs create-cluster --cluster-name stockflow-cluster
```

#### 7. Criar/Atualizar Serviço ECS

```bash
aws ecs create-service \
    --cluster stockflow-cluster \
    --service-name stockflow-api-service \
    --task-definition stockflow-api \
    --desired-count 2 \
    --launch-type FARGATE \
    --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx,subnet-yyy],securityGroups=[sg-xxx]}"
```

## 🔍 Monitoramento e Health Checks

### Endpoints de Health Check:

- **Liveness**: `/api/health/live` - Verifica se a aplicação está viva
- **Readiness**: `/api/health/ready` - Verifica se está pronta para receber tráfego
- **Custom Health**: `/api/health/custom` - Health check customizado com detalhes
- **Actuator Health**: `/api/actuator/health` - Health check do Spring Boot

### Métricas no CloudWatch:

- Acesse CloudWatch > Métricas > StockflowAPI

## 🔧 Configurações de Produção

### Variáveis de Ambiente Obrigatórias:

- `DATABASE_URL` - URL do banco PostgreSQL
- `DB_USERNAME` - Usuário do banco
- `DB_PASSWORD` - Senha do banco
- `EXTERNAL_API_TOKEN` - Token da API externa
- `EXTERNAL_API_SECRET` - Secret da API externa
- `SPRING_PROFILES_ACTIVE=prod` - Perfil de produção

### Configurações Opcionais:

- `PORT=8080` - Porta da aplicação (padrão: 8080)
- `JAVA_OPTS` - Opções da JVM

## 🚨 Troubleshooting

### Verificar logs do container:

```bash
aws logs tail /ecs/stockflow-api --follow
```

### Verificar status do serviço:

```bash
aws ecs describe-services --cluster stockflow-cluster --services stockflow-api-service
```

### Verificar tasks rodando:

```bash
aws ecs list-tasks --cluster stockflow-cluster --service-name stockflow-api-service
```

### Verificar health da aplicação:

```bash
curl https://stockflow-api-alb.us-east-1.elb.amazonaws.com/api/health/custom
```

## 🔄 Atualizações

Para fazer nova release:

1. Fazer commit das mudanças
2. Executar script de deploy: `./deploy-aws.sh`
3. Monitorar o deploy no console da AWS

## 🔒 Segurança

- ✅ Aplicação roda como usuário não-root
- ✅ Secrets gerenciados via SSM Parameter Store
- ✅ Logs centralizados no CloudWatch
- ✅ Health checks configurados
- ✅ Configurações específicas por ambiente

## 📞 Suporte

Em caso de problemas:

1. Verificar logs no CloudWatch
2. Verificar health checks
3. Verificar configurações do Load Balancer
4. Verificar security groups e networking
