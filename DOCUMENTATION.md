# API Order Orchestrator - Documentação Completa

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Tecnologias](#tecnologias)
3. [Arquitetura](#arquitetura)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Configuração e Instalação](#configuração-e-instalação)
6. [Guia de Uso](#guia-de-uso)
7. [Endpoints da API](#endpoints-da-api)
8. [Componentes](#componentes)
9. [Fluxo de Orquestração](#fluxo-de-orquestração)
10. [Tratamento de Erros](#tratamento-de-erros)
11. [Docker e Infraestrutura](#docker-e-infraestrutura)

---

## 🎯 Visão Geral

A **API Order Orchestrator** é uma aplicação Java que orquestra o processo de criação de pedidos. Ela integra múltiplos serviços através de:
- **Comunicação Síncrona**: Via Feign Client para validação de produtos
- **Comunicação Assíncrona**: Via Apache Kafka para registro de pedidos

A aplicação valida a disponibilidade de produtos em estoque e publica eventos de criação de pedidos em tópicos Kafka para processamento downstream.

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Java** | 17 | Linguagem de programação |
| **Spring Boot** | 3.1.2 | Framework de aplicação |
| **Spring Cloud** | 2022.0.3 | Ferramentas de microserviços |
| **Apache Kafka** | Latest | Broker de mensagens |
| **OpenFeign** | 2022.0.3 | Client HTTP declarativo |
| **Lombok** | Latest | Gerador de código boilerplate |
| **WireMock** | 3.0.0-beta-10 | Mock de serviços HTTP |
| **Docker** | Latest | Containerização |

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                    Cliente HTTP                              │
└────────────────────────┬────────────────────────────────────┘
                         │
                    POST /api-orchestrator/v1/order
                         │
┌────────────────────────▼────────────────────────────────────┐
│            OrderOrchestratorController                       │
│            Endpoint de criação de pedidos                    │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│        OrchestratorCreateOrderServiceImpl                     │
│        Orquestra o fluxo de criação                          │
├────────────────┬──────────────────────────────┬─────────────┤
│                │                              │             │
│                ▼                              ▼             │
│        EncumberServiceImpl            ProducerMessageServiceImpl
│        (Validação Síncrona)          (Publicação Assíncrona) │
│                │                              │             │
│                ▼                              ▼             │
│        ProductClient (Feign)         Kafka Producer         │
│        ┌──────────────────┐                 │               │
│        │  Products API    │          ┌──────▼──────┐        │
│        │  (Mock 8989)     │          │  Tópico     │        │
│        └──────────────────┘          │ create-order│        │
│                                      └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Estrutura do Projeto

```
src/main/java/com/br/apiorderorchestrator/
├── ApiOrderOrchestratorApplication.java    # Classe principal com WireMock
├── controller/
│   └── OrderOrchestratorController.java    # Endpoint REST
├── service/
│   ├── OrchestratorCreateOrderService.java # Interface de orquestração
│   ├── EncumberService.java                # Interface de encumbrance
│   ├── ProducerMessageService.java         # Interface de produção Kafka
│   └── impl/
│       ├── OrchestratorCreateOrderServiceImpl.java
│       ├── EncumberServiceImpl.java
│       └── ProducerMessageServiceImpl.java
├── client/
│   └── ProductClient.java                  # Feign Client
├── dto/
│   ├── OrderRequestDTO.java                # Dados do pedido
│   ├── ProductOrderDTO.java                # Dados do produto
│   ├── ProductDTO.java                     # DTO adicional
│   └── ResponseErrorDTO.java               # Resposta de erro
├── exceptions/
│   ├── BusinessException.java              # Exceção de negócio
│   └── Handler.java                        # Global Exception Handler
├── configure/
│   └── KafkaConfig.java                    # Configuração Kafka
└── util/                                   # Utilitários

src/main/resources/
└── application.properties                  # Configurações da aplicação
```

---

## ⚙️ Configuração e Instalação

### Pré-requisitos
- Java 17+
- Maven 3.6+
- Docker e Docker Compose
- Git

### 1. Clonar o Repositório

```bash
git clone https://github.com/RafaelVictoriano/api-order-orchestrator.git
cd api-order-orchestrator
```

### 2. Configuração de Variáveis

Editar `src/main/resources/application.properties`:

```properties
# Contexto da API
server.servlet.context-path=/api-orchestrator
server.port=8096

# Configuração Kafka
topic.create-order.name=create-order
spring.kafka.producer.bootstrap-servers=localhost:29092

# URL da API de Produtos (Mock)
products.url=http://localhost:8989
```

### 3. Iniciar Infraestrutura com Docker Compose

```bash
docker-compose up -d
```

Isso inicia:
- **Zookeeper**: Gerenciador do Kafka (porta 2181)
- **Kafka**: Broker de mensagens (porta 9092)
- **Kafdrop**: UI para monitorar Kafka (porta 19000)

### 4. Compilar o Projeto

```bash
./mvnw clean compile
```

### 5. Executar a Aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8096/api-orchestrator`

---

## 🚀 Guia de Uso

### Criar um Pedido

**Requisição HTTP:**
```http
POST http://localhost:8096/api-orchestrator/v1/order
Content-Type: application/json

{
  "clientId": 123,
  "products": [
    {
      "productId": 1,
      "quantity": 5
    },
    {
      "productId": 2,
      "quantity": 3
    }
  ]
}
```

**Resposta de Sucesso (202 Accepted):**
```
Status: 202 Accepted
Body: (vazio - processamento assíncrono)
```

**Resposta de Erro (422 Unprocessable Entity):**
```json
{
  "timestamp": "2026-05-10T10:30:00",
  "status": 422,
  "error": "O Produto não tem a quantidade desejada em estoque",
  "path": "/api-orchestrator/v1/order"
}
```

---

## 🔌 Endpoints da API

### `POST /v1/order`

Cria um novo pedido após validar a disponibilidade dos produtos e publica um evento em Kafka.

**Request Body:**
```json
{
  "clientId": 123,
  "products": [
    {
      "productId": 1,
      "quantity": 5
    }
  ]
}
```

**Parameters:**
- `clientId` (required, Long, min: 1): ID do cliente
- `products` (required, List): Lista de produtos a encomendar
  - `productId` (required, Long): ID do produto
  - `quantity` (required, Integer, min: 1): Quantidade desejada

**Status Codes:**
- `202 Accepted`: Pedido aceito e será processado
- `400 Bad Request`: Dados inválidos
- `404 Not Found`: Produtos não encontrados
- `422 Unprocessable Entity`: Quantidade insuficiente em estoque ou erro de validação
- `500 Internal Server Error`: Erro interno do servidor

---

## 🔧 Componentes

### 1. **OrderOrchestratorController**

```java
@RestController
@RequestMapping("v1/order")
public class OrderOrchestratorController {
    
    @Autowired
    private OrchestratorCreateOrderService orchestratorCreateOrderService;
    
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping
    public void createOrder(@RequestBody OrderRequestDTO body) {
        orchestratorCreateOrderService.start(body);
    }
}
```

- Recebe requisições de criação de pedidos
- Retorna `202 Accepted` indicando processamento assíncrono

### 2. **OrchestratorCreateOrderServiceImpl**

Orquestra o fluxo completo:
1. Valida produtos através do `EncumberService`
2. Publica o evento em Kafka via `ProducerMessageService`

```java
@Override
public void start(OrderRequestDTO orderRequestDTO) {
    log.info("Starting order creation orchestration flow");
    encumberService.start(orderRequestDTO.getProducts());
    producerMessageService.start(orderRequestDTO);
    log.info("Finishing order creation orchestration flow");
}
```

### 3. **EncumberServiceImpl**

Valida a disponibilidade de produtos na API de produtos externa.

**Fluxo:**
- Envia lista de produtos para `/encumber` via Feign
- Trata respostas:
  - `200 OK`: Produtos encumbrados com sucesso
  - `404 Not Found`: Produtos não encontrados
  - `422 Unprocessable Entity`: Quantidade insuficiente
  - Outros: Erro de integração

### 4. **ProducerMessageService**

Publica o pedido no tópico Kafka `create-order`.

```java
public interface ProducerMessageService {
    void start(OrderRequestDTO orderRequestDTO);
}
```

### 5. **ProductClient** (Feign)

Cliente HTTP declarativo para integração com API de produtos.

```java
@FeignClient(url = "${products.url}", name = "products-api")
public interface ProductClient {
    @PostMapping("/encumber")
    ResponseEntity<String> encumber(@RequestBody List<ProductOrderDTO> productOrderDTOS);
}
```

### 6. **Data Transfer Objects (DTOs)**

#### OrderRequestDTO
```java
public class OrderRequestDTO {
    private Long clientId;           // ID do cliente (min: 1)
    private List<ProductOrderDTO> products; // Produtos do pedido
}
```

#### ProductOrderDTO
```java
public class ProductOrderDTO {
    private Long productId;          // ID do produto
    private Integer quantity;        // Quantidade desejada
}
```

#### ResponseErrorDTO
```java
public class ResponseErrorDTO {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String path;
}
```

---

## 🔄 Fluxo de Orquestração

```
1. Cliente envia POST com OrderRequestDTO
                    │
                    ▼
2. OrderOrchestratorController recebe
                    │
                    ▼
3. OrchestratorCreateOrderServiceImpl.start()
                    │
        ┌───────────┴────────────┐
        │                        │
        ▼                        ▼
4.   Encumber        ProducerMessage
     (Síncrono)       (Assíncrono)
        │                        │
        ▼                        ▼
5. ProductClient    Kafka Producer
   (HTTP Feign)     (Publish Event)
        │                        │
        ▼                        ▼
6. External API    Topic: create-order
   (Mock 8989)           │
        │                │
        ├────────┬───────┘
        │        │
        ▼        ▼
7. Validação + Publicação
        │
        ▼
8. Retorna 202 Accepted
```

---

## ⚠️ Tratamento de Erros

### GlobalExceptionHandler

Centraliza o tratamento de exceções através da classe `Handler`.

**Exceções Tratadas:**
- `MethodArgumentNotValidException`: Validação de entrada falhou
- `BusinessException`: Exceção de lógica de negócio
- `Exception`: Exceções genéricas

**Formato de Resposta:**
```json
{
  "timestamp": "2026-05-10T10:30:00",
  "status": 422,
  "error": "Mensagem de erro detalhada",
  "path": "/api-orchestrator/v1/order"
}
```

### BusinessException

Exceção customizada para erros de negócio:

```java
public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;
    
    public BusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
```

**Uso:**
```java
throw new BusinessException(UNPROCESSABLE_ENTITY, "O Produto não tem a quantidade desejada em estoque");
```

**Códigos de Erro Comuns:**

| Código | Mensagem | Causa |
|--------|----------|-------|
| 404 | Produtos não encontrados | Product API retornou 404 |
| 422 | O Produto não tem a quantidade desejada em estoque | Quantidade insuficiente |
| 500 | Problemas ao se integrar com parceiros internos | Erro na integração externa |

---

## 🐳 Docker e Infraestrutura

### docker-compose.yaml

O arquivo `docker-compose.yaml` orquestra 3 serviços:

#### 1. Zookeeper
```yaml
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
```

**Porta:** `2181` (interno)

#### 2. Kafka Broker
```yaml
  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

**Porta Broker:** `29092` (interno), `9092` (externa)

#### 3. Kafdrop (UI Kafka)
```yaml
  kafdrop:
    image: obsidiandynamics/kafdrop:latest
    depends_on:
      - kafka
    ports:
      - "19000:9000"
    environment:
      KAFKA_BROKERCONNECT: kafka:29092
```

**Acesso:** `http://localhost:19000`

### Comandos Docker Úteis

```bash
# Iniciar serviços
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar serviços
docker-compose down

# Remover volumes
docker-compose down -v

# Ver status
docker-compose ps
```

### Acesso aos Serviços

| Serviço | URL | Descrição |
|---------|-----|-----------|
| API | http://localhost:8096 | Aplicação principal |
| Kafka | localhost:9092 | Broker (producer/consumer) |
| Kafdrop | http://localhost:19000 | UI Kafka |

---

## 🧪 Testando a Aplicação

### 1. Verificar Status da API

```bash
curl -v http://localhost:8096/api-orchestrator/v1/order
```

### 2. Enviar Pedido de Teste

```bash
curl -X POST http://localhost:8096/api-orchestrator/v1/order \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "products": [
      {
        "productId": 1,
        "quantity": 5
      }
    ]
  }'
```

### 3. Monitorar Tópico Kafka

Acessar **Kafdrop** em `http://localhost:19000`:
1. Procurar pelo tópico `create-order`
2. Verificar mensagens publicadas

---

## 📝 Anotações Importantes

- **Processamento Assíncrono**: A resposta `202 Accepted` não indica sucesso final, apenas que o pedido foi recebido
- **WireMock**: Mock server é iniciado automaticamente na porta 8989 para simular a API de produtos
- **Validação**: Dados são validados tanto no DTO quanto no handler de exceções
- **Logs**: Todas as operações são registradas em Log4j2

---

## 🤝 Contribuindo

Para contribuir com o projeto:

1. Faça um fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob licença MIT. Veja o arquivo LICENSE para mais detalhes.

---

## 📧 Suporte

Para dúvidas ou issues, abra uma issue no repositório: 
https://github.com/RafaelVictoriano/api-order-orchestrator/issues

---

**Última atualização:** 2026-05-10
**Versão do Projeto:** 0.0.1-SNAPSHOT
**Status:** Em Desenvolvimento
