# Cliente para API Externa

Este módulo contém um cliente HTTP configurado para fazer requisições à API externa `https://api.iniciativaaplicativos.com.br/api` com autenticação via token e secret.

## Estrutura

- `ExternalApiConfig.java` - Configuração do cliente WebClient com headers de autenticação
- `ExternalApiClient.java` - Cliente HTTP com métodos para GET, POST, PUT, DELETE
- `ExternalApiService.java` - Service genérico com métodos utilitários
- `ExternalApiController.java` - Controller proxy para testar o cliente
- `ExampleIntegrationService.java` - Exemplo prático de uso do cliente

## Configuração

As credenciais estão configuradas no `application.yaml` e podem ser sobrescritas via variáveis de ambiente:

```yaml
external:
  api:
    base-url: ${EXTERNAL_API_BASE_URL:https://api.iniciativaaplicativos.com.br/api}
    token: ${EXTERNAL_API_TOKEN:usQjA5McLAyQLc16S9wnESl9fAk4kDYbVasJtyRzMKPPufpx7uqnH6r1128HCw6Q}
    secret: ${EXTERNAL_API_SECRET:lxs7KnxBIX24w88t4J169KPxc9UTgdFyfPloZdRGRMfKKJL2YxtawOnbe4zy1VlG}
```

### Headers Enviados Automaticamente

- `Authorization: Bearer {token}`
- `X-API-Secret: {secret}`
- `Content-Type: application/json`
- `Accept: application/json`

## Como Usar

### 1. Uso Básico com ExternalApiClient

```java
@Autowired
private ExternalApiClient apiClient;

// GET request
String response = apiClient.get("/products");

// POST request
Map<String, Object> data = new HashMap<>();
data.put("name", "Produto Teste");
String response = apiClient.post("/products", data);

// GET com parâmetros
Map<String, String> params = new HashMap<>();
params.put("category", "electronics");
String response = apiClient.getWithParams("/products", params);
```

### 2. Uso com ExternalApiService

```java
@Autowired
private ExternalApiService externalApiService;

// Buscar dados
String response = externalApiService.fetchData("/products");

// Enviar dados
String response = externalApiService.sendData("/products", productData);

// Converter resposta para objeto
Product product = externalApiService.parseJsonToObject(response, Product.class);
```

### 3. Exemplo Prático com ExampleIntegrationService

```java
@Autowired
private ExampleIntegrationService integrationService;

// Verificar saúde da API
boolean isHealthy = integrationService.checkApiHealth();

// Buscar produto específico
String productInfo = integrationService.getProductInfo("12345");

// Criar novo produto
Map<String, Object> newProduct = integrationService.createProductPayload(
    "Produto Novo", "Descrição do produto", 99.99
);
String result = integrationService.createProduct(newProduct);
```

## Endpoints de Teste

O sistema inclui endpoints para testar o cliente:

### GET /api/external/test

Testa a conectividade com a API externa.

**Resposta de sucesso:**

```json
{
  "status": "success",
  "message": "Conexão com API externa funcionando",
  "response": "..."
}
```

### GET /api/external/proxy

Proxy para fazer requisições GET para a API externa.

**Parâmetros:**

- `endpoint` (obrigatório) - O endpoint da API externa
- Parâmetros adicionais serão passados como query parameters

**Exemplo:**

```
GET /api/external/proxy?endpoint=/products&category=electronics&limit=10
```

### POST /api/external/proxy

Proxy para fazer requisições POST.

**Parâmetros:**

- `endpoint` (query) - O endpoint da API externa
- Body da requisição será enviado para a API externa

### PUT /api/external/proxy

Similar ao POST, mas para requisições PUT.

### DELETE /api/external/proxy

Proxy para requisições DELETE.

## Tratamento de Erros

O cliente possui tratamento de erros integrado:

- Erros HTTP são capturados e convertidos em `RuntimeException`
- Logs detalhados são gerados para debugging
- Mensagens de erro incluem o endpoint e detalhes do problema

## Customização

Para criar um service específico para sua integração:

1. Injete o `ExternalApiClient`
2. Implemente métodos específicos para seus endpoints
3. Adicione tratamento de erro personalizado
4. Use logging para monitoramento

**Exemplo:**

```java
@Service
public class MySpecificService {

    private final ExternalApiClient apiClient;

    @Autowired
    public MySpecificService(ExternalApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public MyData getMyData(String id) {
        String response = apiClient.get("/my-endpoint/" + id);
        // Processe a resposta...
        return processResponse(response);
    }
}
```

## Monitoramento

- Logs são gerados em nível `INFO` para requisições bem-sucedidas
- Logs de `ERROR` para falhas
- Use os endpoints de teste para verificar conectividade
- Configure alertas baseados nos logs de erro
