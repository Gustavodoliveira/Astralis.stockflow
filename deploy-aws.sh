#!/bin/bash

# Script para deploy na AWS ECS
# Configurações de deploy para produção

set -e  # Exit on any error

# Configurações
REGION="us-east-1"
CLUSTER_NAME="stockflow-cluster"
SERVICE_NAME="stockflow-api-service" 
FAMILY_NAME="stockflow-api"
ECR_REPOSITORY="stockflow-api"
IMAGE_TAG="latest"

echo "🚀 Iniciando deploy para AWS ECS..."

# 1. Build da aplicação
echo "📦 Fazendo build da aplicação..."
./mvnw clean package -DskipTests

# 2. Build da imagem Docker
echo "🐳 Fazendo build da imagem Docker..."
docker build -t $ECR_REPOSITORY:$IMAGE_TAG .

# 3. Login no ECR (se necessário)
echo "🔐 Fazendo login no Amazon ECR..."
aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com

# 4. Tag da imagem para o ECR
echo "🏷️  Taggeando imagem para ECR..."
docker tag $ECR_REPOSITORY:$IMAGE_TAG $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG

# 5. Push da imagem para ECR
echo "📤 Enviando imagem para ECR..."
docker push $ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG

# 6. Atualizar task definition (se necessário)
echo "📝 Atualizando task definition..."
# Aqui você pode usar aws ecs register-task-definition ou AWS CDK/Terraform

# 7. Atualizar serviço ECS
echo "🔄 Atualizando serviço ECS..."
aws ecs update-service --cluster $CLUSTER_NAME --service $SERVICE_NAME --force-new-deployment --region $REGION

echo "✅ Deploy concluído com sucesso!"
echo "🌐 Monitorize o deploy em: https://console.aws.amazon.com/ecs/home?region=$REGION#/clusters/$CLUSTER_NAME/services/$SERVICE_NAME/tasks"