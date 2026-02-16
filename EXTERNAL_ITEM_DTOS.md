# DTOs para API Externa - Itens/Produtos

Criei DTOs de resposta para trabalhar com dados de itens da API externa com a estrutura JSON fornecida.

## 📦 **Estrutura Criada**

### 1. **ExternalItemResponse.java**

DTO básico que mapeia exatamente a estrutura JSON da API:

```java
public record ExternalItemResponse(
    Long id,
    String identificacao,
    String descricao,
    @JsonProperty("data_criacao") String dataCriacao,
    Integer qtde,
    Integer saldo,
    @JsonProperty("data_validade") String dataValidade,
    String localizacao
)
```

### 2. **ExternalItemTypedResponse.java**

Versão com datas tipadas como `LocalDateTime`:

```java
public record ExternalItemTypedResponse(
    Long id,
    String identificacao,
    String descricao,
    @JsonProperty("data_criacao")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dataCriacao,
    Integer qtde,
    Integer saldo,
    @JsonProperty("data_validade")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dataValidade,
    String localizacao
)
```

### 3. **ExternalItemService.java**

Service com métodos para trabalhar com os itens:

- `getItem(Long itemId)` - Buscar item específico
- `getAllItems()` - Listar todos os itens
- `searchItems(...)` - Buscar com filtros
- `createItem(...)` - Criar novo item
- `updateQuantity(...)` - Atualizar quantidade
- `hasStock(...)` - Verificar se tem estoque
- `isExpired(...)` - Verificar se vencido
- `convertToTypedResponse(...)` - Converter entre tipos

### 4. **ExternalItemController.java**

Controller REST com endpoints para testar:

## 🚀 **Como Usar**

### **Exemplo Básico:**

```java
@Autowired
private ExternalItemService itemService;

// Buscar item
ExternalItemResponse item = itemService.getItem(1L);

// Verificar estoque
boolean temEstoque = itemService.hasStock(item);

// Buscar com datas tipadas
ExternalItemTypedResponse itemTipado = itemService.getTypedItem(1L);

// Verificar se vencido
boolean vencido = itemService.isExpired(itemTipado);
```

### **Buscar Todos os Itens:**

```java
List<ExternalItemResponse> todosItens = itemService.getAllItems();
```

### **Buscar com Filtros:**

```java
List<ExternalItemResponse> itens = itemService.searchItems("2019/09-3.6", 5);
```

### **Criar Novo Item:**

```java
ExternalItemResponse novoItem = itemService.createItem(
    "2024/01-1.0",
    "Produto Novo",
    100,
    "Estoque A"
);
```

## 🌐 **Endpoints REST Disponíveis**

```bash
# Buscar item por ID
GET /v1/stockflow-api/api/external-items/1

# Buscar item com datas tipadas
GET /v1/stockflow-api/api/external-items/1/typed

# Listar todos
GET /v1/stockflow-api/api/external-items

# Buscar com filtros
GET /v1/stockflow-api/api/external-items/search?identificacao=2019/09-3.6&qtdeMinima=10

# Criar novo item
POST /v1/stockflow-api/api/external-items
?identificacao=2024/01&descricao=Teste&qtde=50&localizacao=EstoqueB

# Atualizar quantidade
PUT /v1/stockflow-api/api/external-items/1/quantity?quantidade=200

# Verificar estoque
GET /v1/stockflow-api/api/external-items/1/has-stock

# Verificar se vencido
GET /v1/stockflow-api/api/external-items/1/is-expired
```

## 📋 **Mapeamento de Campos**

| JSON Field      | DTO Field       | Tipo                   | Descrição            |
| --------------- | --------------- | ---------------------- | -------------------- |
| `id`            | `id`            | `Long`                 | Identificador único  |
| `identificacao` | `identificacao` | `String`               | Código identificador |
| `descricao`     | `descricao`     | `String`               | Descrição do item    |
| `data_criacao`  | `dataCriacao`   | `String/LocalDateTime` | Data de criação      |
| `qtde`          | `qtde`          | `Integer`              | Quantidade           |
| `saldo`         | `saldo`         | `Integer`              | Saldo atual          |
| `data_validade` | `dataValidade`  | `String/LocalDateTime` | Data de validade     |
| `localizacao`   | `localizacao`   | `String`               | Localização física   |

## 🔄 **Conversão de Tipos**

O service inclui método para converter entre as versões:

```java
// String -> LocalDateTime
ExternalItemTypedResponse tipado = itemService.convertToTypedResponse(item);

// Direto com conversão automática
ExternalItemTypedResponse item = itemService.getTypedItem(1L);
```

## ⚙️ **Configuração**

Os DTOs usam anotações Jackson para mapeamento automático:

- `@JsonProperty` - Mapear nomes diferentes
- `@JsonFormat` - Formatar datas
- Pattern usado: `"yyyy-MM-dd HH:mm:ss"`

**✅ Compilação bem-sucedida! Os DTOs estão prontos para usar.**
