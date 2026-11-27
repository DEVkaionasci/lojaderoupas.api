# Loja de Roupas — API REST em Java

Projeto final da disciplina **Padrões de Projeto em Aplicações Web** (IF Baiano — Prof. Éder Pereira dos Santos).

## Problema que a aplicação resolve

Uma loja de roupas online precisa manter o **catálogo** de produtos, cadastrar **clientes**, receber **pedidos** (com cálculo de descontos e frete) e controlar o **estoque** sem vender o que não existe. Esta API cobre esse fluxo completo; não há interface gráfica: toda interação é feita pelo **Postman**.

## Integrantes

- Kaio do Nascimento Sacramento
- Ian Marcel
- Yuri Herold

## API externa consumida: ViaCEP

- Site: <https://viacep.com.br> — `GET https://viacep.com.br/ws/{cep}/json/` (pública, sem chave).
- O cliente informa **só o CEP**; o backend consulta o ViaCEP e preenche logradouro, bairro, cidade e UF.
- A **UF** obtida define a **regra de frete** do pedido (local / regional / nacional): o dado externo participa da regra de negócio.
- Também exposta em `GET /api/cep/{cep}`.

## Requisitos funcionais

| #    | Requisito                                                                                                  |
| ---- | ---------------------------------------------------------------------------------------------------------- |
| RF01 | Cadastrar, listar, buscar, atualizar e remover produtos (com filtro por categoria)                         |
| RF02 | Cadastrar, listar, buscar, atualizar e remover clientes, preenchendo o endereço via ViaCEP                 |
| RF03 | Consultar um CEP na API externa                                                                            |
| RF04 | Criar pedido com vários itens, validando cliente, produtos e estoque                                       |
| RF05 | Aplicar descontos: PIX (10%) e 3+ peças (5%), cumulativos                                                  |
| RF06 | Calcular frete pela UF do cliente (local R$ 12, regional R$ 22, nacional R$ 38, grátis a partir de R$ 300) |
| RF07 | Baixar o estoque ao criar o pedido e devolvê-lo ao cancelar                                                |
| RF08 | Atualizar o status do pedido respeitando o fluxo CRIADO → PAGO → ENVIADO → ENTREGUE (ou CANCELADO)         |
| RF09 | Consultar um pedido, listar todos os pedidos e listar os pedidos de um cliente                             |
| RF10 | Proteger rotas administrativas com chave de API (`X-API-Key`)                                              |

## Onde cada padrão de projeto foi implementado

| Padrão               | Onde                                                                                                                                                                     |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Front Controller** | `controller/FrontControllerServlet.java` (+ `Router`, `Rotas`, `AutenticacaoInterceptor`) — ponto único `/api/*`: roteamento, autenticação, tratamento de exceções e log |
| **DTO**              | pacote `dto/` + `mapper/` — entidades nunca saem da API; `ClienteResponseDTO` não tem senha                                                                              |
| **DAO**              | pacote `dao/` — interfaces (`ProdutoDAO`, `ClienteDAO`, `PedidoDAO`) + implementações JDBC (`*DAOImpl`)                                                                  |
| **Command** (GoF)    | pacote `command/` — um Command por ação, escolhido pelo Front Controller                                                                                                 |
| **Singleton** (GoF)  | `infra/ConnectionSingleton.java`                                                                                                                                         |
| **Strategy** (GoF)   | `strategy/` — `FreteStrategy` e 4 estratégias; escolha em `FreteStrategyFactory`; uso em `PedidoService.criar`                                                           |
| **Decorator** (GoF)  | `decorator/` — `PrecoPedido`, `SubtotalPedido`, `DescontoPixDecorator`, `DescontoQuantidadeDecorator`; montagem em `PrecoPedidoFactory`                                  |

## Arquitetura

`Postman → FrontControllerServlet → Command → Service → DAO → PostgreSQL` (e `Service → ViaCepClient → ViaCEP`).
Diagramas em [`docs/`](docs): [classes](docs/diagrama-classes.md), [componentes](docs/diagrama-componentes.md), [banco de dados](docs/modelo-banco.md).

```
src/main/java/br/com/lojaroupas/
├── Main.java                 # sobe o Tomcat embarcado e monta as peças
├── controller/               # Front Controller, Router, rotas, autenticação
├── command/                  # Padrão Command (16 ações)
├── service/                  # regras de negócio
├── model/                    # entidades (POJOs) e enums
├── dto/  mapper/             # DTOs e conversões
├── dao/                      # DAO (interfaces + JDBC)
├── strategy/  decorator/     # padrões GoF de frete e desconto
├── integration/              # ViaCEP
├── infra/                    # Singleton, Config, JdbcUtil
├── exception/                # exceções com status HTTP
└── util/                     # validação, JSON, senha
```

## Como executar

Requisitos: **Java 17+**, **Maven 3.8+**, **PostgreSQL**.

1. Criar o banco e as tabelas:
   ```bash
   psql -U postgres -c "CREATE DATABASE loja_roupas;"
   psql -U postgres -d loja_roupas -f sql/schema.sql
   ```
2. (Opcional) variáveis de ambiente — valores padrão entre parênteses:
   `DB_URL` (`jdbc:postgresql://localhost:5432/loja_roupas`), `DB_USER` (`postgres`), `DB_PASSWORD` (`postgres`), `PORT` (`8080`), `API_KEY` (`loja-admin-123`), `LOJA_UF` (`BA`).
3. Compilar, testar e rodar:
   ```bash
   mvn clean package
   java -jar target/loja-roupas.jar
   ```
   (ou rode a classe `Main` na IDE). A API fica em `http://localhost:8080/api`.
4. Importar no Postman `postman/loja-roupas.postman_collection.json` e executar a coleção na ordem.

## Rotas

Rotas **admin** exigem o header `X-API-Key`. Como não há login de cliente, tudo que lê ou altera dados de um cliente já existente é admin.

| Método              | Rota                                                                  | Acesso  |
| ------------------- | --------------------------------------------------------------------- | ------- |
| GET                 | `/api/produtos[?categoria=CAMISETA]`                                  | público |
| GET                 | `/api/produtos/{id}`                                                  | público |
| POST / PUT / DELETE | `/api/produtos` · `/api/produtos/{id}`                                | admin   |
| POST                | `/api/clientes`                                                       | público |
| GET                 | `/api/clientes` · `/api/clientes/{id}` · `/api/clientes/{id}/pedidos` | admin   |
| PUT / DELETE        | `/api/clientes/{id}`                                                  | admin   |
| POST                | `/api/pedidos`                                                        | público |
| GET                 | `/api/pedidos` · `/api/pedidos/{id}`                                  | admin   |
| PUT                 | `/api/pedidos/{id}/status`                                            | admin   |
| GET                 | `/api/cep/{cep}`                                                      | público |

Exemplo de criação de pedido:

```json
POST /api/pedidos
{ "clienteId": 1, "formaPagamento": "PIX", "itens": [ { "produtoId": 1, "quantidade": 3 } ] }
```

Códigos usados: 200, 201, 204, 400 (validação), 401 (sem chave), 404, 405, 409 (conflito/estoque/e-mail duplicado), 502 (ViaCEP fora do ar), 500.

## Testes

`mvn test` — testes unitários (JUnit 5) de regras de negócio, Strategy, Decorator, Router e serviços, usando DAOs em memória e um ViaCEP falso (sem banco e sem internet).
