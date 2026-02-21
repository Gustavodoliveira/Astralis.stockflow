# Use uma imagem base com Java 21
FROM openjdk:21-jdk-slim

# Definir o diretório de trabalho
WORKDIR /app

# Instalar curl para health checks (opcional)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dar permissão de execução ao Maven wrapper
RUN chmod +x ./mvnw

# Baixar dependências (isso será cached se o pom.xml não mudar)
RUN ./mvnw dependency:go-offline -B

# Copiar o código fonte
COPY src src

# Fazer o build da aplicação
RUN ./mvnw clean package -DskipTests

# Expor a porta da aplicação
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "target/stockflow-api-0.0.1-SNAPSHOT.jar"]

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/v1/stockflow-api/actuator/health || exit 1