# 🏭 Stockflow API

API para gestão de fluxo de estoque e integração com sistemas externos.

## 🚀 Início Rápido

```bash
# Executar imediatamente (não requer configuração)
./mvnw spring-boot:run
```

🎯 **A aplicação agora inicia sem problemas!** Os erros de placeholder foram corrigidos.

## 📚 Documentação

Toda a documentação técnica está organizada na pasta [`docs/`](docs/).

- 📖 **[Índice completo](docs/README.md)** - Lista de toda documentação
- ⚡ **[Configuração rápida](docs/ENV_SETUP_FIXED.md)** - Como configurar variáveis de ambiente
- 🐳 **[Docker](docs/DOCKER_README.md)** - Execução com Docker
- 🔌 **[API Externa](docs/EXTERNAL_API_CLIENT.md)** - Configuração de integrações

## 🏗️ Tecnologias

- **Spring Boot 3.x** - Framework principal
- **PostgreSQL** - Banco de dados
- **Docker** - Containerização
- **Flyway** - Migrations
- **Swagger/OpenAPI** - Documentação da API

## 🔧 Configuração

### Desenvolvimento Local

```bash
# 1. Executar (funciona sem configuração adicional)
./mvnw spring-boot:run

# 2. Para usar API externa real, edite o .env:
EXTERNAL_API_TOKEN=seu_token_aqui
EXTERNAL_API_SECRET=seu_secret_aqui
```

### Docker

```bash
docker-compose up
```

## ⚠️ Problemas Comuns

- **❌ Could not resolve placeholder**: ✅ **Resolvido!** - App agora inicia com valores padrão
- **❌ PostgreSQL dialect warning**: ✅ **Resolvido!** - Configuração removida
- **❌ DotenvPropertySource error**: ✅ **Resolvido!** - Dependência removida

## 📍 URLs

- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Health**: http://localhost:8080/actuator/health

## 📞 Suporte

Para configurações detalhadas e troubleshooting, consulte a [documentação completa](docs/).
