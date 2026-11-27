# Plano de commits

Cada commit abaixo compila e passa nos testes que existem até ali. Copie os arquivos listados, rode `git add .` e `git commit -m "..."`.

## Commit 01

```
chore: cria estrutura inicial do projeto Maven
```

**Arquivos:**

- `pom.xml`
- `.gitignore`

**Para entender:** Crie as pastas do Maven (src/main/java, src/test/java). O pom lista as dependências (Tomcat embarcado, Gson, driver PostgreSQL, JUnit) e o plugin Shade que gera o jar executável. Compare com o pom da aula.

## Commit 02

```
feat(model): adiciona enums Categoria, FormaPagamento e StatusPedido
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/model/Categoria.java`
- `src/main/java/br/com/lojaroupas/model/FormaPagamento.java`
- `src/main/java/br/com/lojaroupas/model/StatusPedido.java`

**Para entender:** StatusPedido tem o método podeIrPara(), que define o fluxo permitido do pedido (regra de negócio dentro do próprio domínio).

## Commit 03

```
feat(model): adiciona entidades Produto e Cliente
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/model/Produto.java`
- `src/main/java/br/com/lojaroupas/model/Cliente.java`

**Para entender:** POJOs: atributos privados, construtores, getters e setters. Cliente guarda senhaHash, nunca a senha em texto.

## Commit 04

```
feat(model): adiciona entidades ItemPedido e Pedido
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/model/ItemPedido.java`
- `src/main/java/br/com/lojaroupas/model/Pedido.java`

**Para entender:** Pedido contém uma lista de ItemPedido (composição) e referencia o cliente por id. ItemPedido guarda uma cópia do preço e do nome do produto no momento da compra.

## Commit 05

```
feat(exception): cria exceções de aplicação com status HTTP
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/exception/AppException.java`
- `src/main/java/br/com/lojaroupas/exception/NegocioException.java`
- `src/main/java/br/com/lojaroupas/exception/NaoAutorizadoException.java`
- `src/main/java/br/com/lojaroupas/exception/RecursoNaoEncontradoException.java`
- `src/main/java/br/com/lojaroupas/exception/MetodoNaoPermitidoException.java`
- `src/main/java/br/com/lojaroupas/exception/ConflitoException.java`
- `src/main/java/br/com/lojaroupas/exception/IntegracaoException.java`
- `src/main/java/br/com/lojaroupas/exception/PersistenciaException.java`

**Para entender:** Cada exceção carrega o status HTTP (400, 401, 404, 405, 409, 502, 500). Qualquer camada lança, e o Front Controller converte em JSON.

## Commit 06

```
feat(infra): adiciona Config, ConnectionSingleton e JdbcUtil
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/infra/Config.java`
- `src/main/java/br/com/lojaroupas/infra/ConnectionSingleton.java`
- `src/main/java/br/com/lojaroupas/infra/JdbcUtil.java`

**Para entender:** Primeiro padrão GoF: SINGLETON. Leia o comentário da classe para ver a diferença em relação ao exemplo da aula.

## Commit 07

```
feat(db): adiciona script SQL do banco (schema e dados de exemplo)
```

**Arquivos:**

- `sql/schema.sql`

**Para entender:** Execute o script no PostgreSQL. Observe as chaves estrangeiras, o UNIQUE do e-mail e os CHECK de preço e estoque.

## Commit 08

```
feat(dao): adiciona ProdutoDAO e implementação JDBC
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/dao/ProdutoDAO.java`
- `src/main/java/br/com/lojaroupas/dao/ProdutoDAOImpl.java`

**Para entender:** Padrão DAO: a interface é o contrato; a implementação JDBC é o único lugar com SQL de produto.

## Commit 09

```
feat(dao): adiciona ClienteDAO e implementação JDBC
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/dao/ClienteDAO.java`
- `src/main/java/br/com/lojaroupas/dao/ClienteDAOImpl.java`

**Para entender:** Mesmo padrão. Repare em como o erro de e-mail duplicado do banco vira ConflitoException (409).

## Commit 10

```
feat(util): adiciona Validacao e SenhaUtil
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/util/Validacao.java`
- `src/main/java/br/com/lojaroupas/util/SenhaUtil.java`

**Para entender:** Validações reutilizáveis e hash de senha com PBKDF2 (já vem no Java).

## Commit 11

```
feat(dto): adiciona DTOs e mapper de Produto
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/dto/ProdutoRequestDTO.java`
- `src/main/java/br/com/lojaroupas/dto/ProdutoResponseDTO.java`
- `src/main/java/br/com/lojaroupas/mapper/ProdutoMapper.java`

**Para entender:** Padrão DTO: o que entra (Request) e o que sai (Response) são objetos próprios, separados da entidade.

## Commit 12

```
feat(dto): adiciona DTOs e mapper de Cliente e CEP
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/dto/ClienteRequestDTO.java`
- `src/main/java/br/com/lojaroupas/dto/ClienteResponseDTO.java`
- `src/main/java/br/com/lojaroupas/dto/CepResponseDTO.java`
- `src/main/java/br/com/lojaroupas/mapper/ClienteMapper.java`

**Para entender:** ClienteResponseDTO não tem senha: é exatamente o problema que o DTO resolve (não expor dados sensíveis).

## Commit 13

```
feat(integration): integra com a API externa ViaCEP
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/integration/EnderecoCep.java`
- `src/main/java/br/com/lojaroupas/integration/CepGateway.java`
- `src/main/java/br/com/lojaroupas/integration/ViaCepClient.java`

**Para entender:** Consumo da API externa com o HttpClient do Java. A interface CepGateway permite trocar a API ou usar uma falsa nos testes.

## Commit 14

```
feat(service): adiciona ProdutoService
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/service/ProdutoService.java`

**Para entender:** Camada de serviço: validações e regras. Recebe e devolve DTOs, conversa com o DAO só pela interface.

## Commit 15

```
feat(service): adiciona ClienteService e CepService
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/service/ClienteService.java`
- `src/main/java/br/com/lojaroupas/service/CepService.java`

**Para entender:** O cliente informa só o CEP; o endereço vem do ViaCEP. A senha é transformada em hash antes de ir ao banco.

## Commit 16

```
feat(strategy): implementa cálculo de frete com padrão Strategy
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/strategy/FreteStrategy.java`
- `src/main/java/br/com/lojaroupas/strategy/FreteGratisStrategy.java`
- `src/main/java/br/com/lojaroupas/strategy/FreteLocalStrategy.java`
- `src/main/java/br/com/lojaroupas/strategy/FreteRegionalStrategy.java`
- `src/main/java/br/com/lojaroupas/strategy/FreteNacionalStrategy.java`
- `src/main/java/br/com/lojaroupas/strategy/FreteStrategyFactory.java`

**Para entender:** Segundo padrão GoF: STRATEGY. Cada regra de frete é uma classe; a fábrica escolhe pela UF (vinda do ViaCEP) e pelo valor.

## Commit 17

```
feat(decorator): implementa descontos com padrão Decorator
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/decorator/PrecoPedido.java`
- `src/main/java/br/com/lojaroupas/decorator/SubtotalPedido.java`
- `src/main/java/br/com/lojaroupas/decorator/DescontoDecorator.java`
- `src/main/java/br/com/lojaroupas/decorator/DescontoPixDecorator.java`
- `src/main/java/br/com/lojaroupas/decorator/DescontoQuantidadeDecorator.java`
- `src/main/java/br/com/lojaroupas/decorator/PrecoPedidoFactory.java`

**Para entender:** Terceiro padrão GoF: DECORATOR. Cada desconto embrulha o anterior; dá para empilhar sem alterar as outras classes.

## Commit 18

```
feat(dao): adiciona PedidoDAO com transação (pedido, itens e estoque)
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/dao/PedidoDAO.java`
- `src/main/java/br/com/lojaroupas/dao/PedidoDAOImpl.java`

**Para entender:** Transação JDBC: setAutoCommit(false), commit e rollback. O UPDATE com 'AND estoque >= ?' impede estoque negativo em compras simultâneas.

## Commit 19

```
feat(dto): adiciona DTOs e mapper de Pedido
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/dto/ItemPedidoRequestDTO.java`
- `src/main/java/br/com/lojaroupas/dto/PedidoRequestDTO.java`
- `src/main/java/br/com/lojaroupas/dto/StatusRequestDTO.java`
- `src/main/java/br/com/lojaroupas/dto/ItemPedidoResponseDTO.java`
- `src/main/java/br/com/lojaroupas/dto/PedidoResponseDTO.java`
- `src/main/java/br/com/lojaroupas/mapper/PedidoMapper.java`

**Para entender:** A resposta do pedido já mostra subtotal, desconto, frete, total, tipo de frete e descontos aplicados.

## Commit 20

```
feat(service): adiciona PedidoService
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/service/PedidoService.java`

**Para entender:** O coração do sistema: valida, monta itens, aplica Decorator (descontos) e Strategy (frete), grava e muda status. Leia o método criar() linha a linha.

## Commit 21

```
feat(util): adiciona JsonUtil, HttpUtil e ErroDTO
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/util/JsonUtil.java`
- `src/main/java/br/com/lojaroupas/util/HttpUtil.java`
- `src/main/java/br/com/lojaroupas/dto/ErroDTO.java`

**Para entender:** Leitura do corpo JSON, escrita da resposta e o formato padrão de erro da API.

## Commit 22

```
feat(command): adiciona interface Command e commands de produto
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/command/Command.java`
- `src/main/java/br/com/lojaroupas/command/ListarProdutosCommand.java`
- `src/main/java/br/com/lojaroupas/command/BuscarProdutoCommand.java`
- `src/main/java/br/com/lojaroupas/command/CadastrarProdutoCommand.java`
- `src/main/java/br/com/lojaroupas/command/AtualizarProdutoCommand.java`
- `src/main/java/br/com/lojaroupas/command/RemoverProdutoCommand.java`

**Para entender:** Quarto padrão GoF: COMMAND. Mesma ideia do CadastrarLivroCommand da aula, agora chamando um Service.

## Commit 23

```
feat(command): adiciona commands de cliente e consulta de CEP
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/command/CadastrarClienteCommand.java`
- `src/main/java/br/com/lojaroupas/command/ListarClientesCommand.java`
- `src/main/java/br/com/lojaroupas/command/BuscarClienteCommand.java`
- `src/main/java/br/com/lojaroupas/command/AtualizarClienteCommand.java`
- `src/main/java/br/com/lojaroupas/command/RemoverClienteCommand.java`
- `src/main/java/br/com/lojaroupas/command/ConsultarCepCommand.java`

**Para entender:** Cada Command é pequeno: lê a requisição, chama o Service, escreve a resposta.

## Commit 24

```
feat(command): adiciona commands de pedido
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/command/CriarPedidoCommand.java`
- `src/main/java/br/com/lojaroupas/command/ListarPedidosCommand.java`
- `src/main/java/br/com/lojaroupas/command/BuscarPedidoCommand.java`
- `src/main/java/br/com/lojaroupas/command/ListarPedidosDoClienteCommand.java`
- `src/main/java/br/com/lojaroupas/command/AtualizarStatusPedidoCommand.java`

**Para entender:** Com isso todas as ações da API existem. Falta o Front Controller que as despacha.

## Commit 25

```
feat(controller): adiciona Rota, Router e autenticação por API key
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/controller/Rota.java`
- `src/main/java/br/com/lojaroupas/controller/Router.java`
- `src/main/java/br/com/lojaroupas/controller/AutenticacaoInterceptor.java`

**Para entender:** Router: tabela método + URL para Command (404 e 405). Interceptor: autenticação transversal pelo header X-API-Key.

## Commit 26

```
feat(controller): implementa o FrontControllerServlet
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/controller/FrontControllerServlet.java`

**Para entender:** Padrão FRONT CONTROLLER: ponto único de entrada com roteamento, autenticação, tratamento centralizado de exceções e log.

## Commit 27

```
feat: registra as rotas e adiciona a classe Main com Tomcat embarcado
```

**Arquivos:**

- `src/main/java/br/com/lojaroupas/controller/Rotas.java`
- `src/main/java/br/com/lojaroupas/Main.java`

**Para entender:** Aqui tudo se encaixa. Rode a Main e teste GET /api/produtos no Postman.

## Commit 28

```
test: adiciona DAOs e ViaCEP falsos para testes
```

**Arquivos:**

- `src/test/java/br/com/lojaroupas/fake/FakeProdutoDAO.java`
- `src/test/java/br/com/lojaroupas/fake/FakeClienteDAO.java`
- `src/test/java/br/com/lojaroupas/fake/FakePedidoDAO.java`
- `src/test/java/br/com/lojaroupas/fake/FakeCepGateway.java`

**Para entender:** Como DAO e CepGateway são interfaces, os testes usam versões em memória, sem banco e sem internet.

## Commit 29

```
test: testa Strategy, Decorator e fluxo de status
```

**Arquivos:**

- `src/test/java/br/com/lojaroupas/strategy/FreteStrategyFactoryTest.java`
- `src/test/java/br/com/lojaroupas/decorator/PrecoPedidoFactoryTest.java`
- `src/test/java/br/com/lojaroupas/model/StatusPedidoTest.java`

**Para entender:** Testes dos padrões GoF e da regra de transição de status.

## Commit 30

```
test: testa os serviços de produto, cliente e pedido
```

**Arquivos:**

- `src/test/java/br/com/lojaroupas/service/ProdutoServiceTest.java`
- `src/test/java/br/com/lojaroupas/service/ClienteServiceTest.java`
- `src/test/java/br/com/lojaroupas/service/PedidoServiceTest.java`

**Para entender:** PedidoServiceTest confere os cálculos de desconto e frete, o estoque e o cancelamento.

## Commit 31

```
test: testa Validacao, SenhaUtil e Router
```

**Arquivos:**

- `src/test/java/br/com/lojaroupas/util/ValidacaoTest.java`
- `src/test/java/br/com/lojaroupas/util/SenhaUtilTest.java`
- `src/test/java/br/com/lojaroupas/controller/RouterTest.java`

**Para entender:** Rode mvn test: todos devem passar.

## Commit 32

```
docs: adiciona README e diagramas (classes, componentes e banco)
```

**Arquivos:**

- `README.md`
- `docs/diagrama-classes.md`
- `docs/diagrama-componentes.md`
- `docs/modelo-banco.md`

**Para entender:** O README é exigido pelo professor: domínio, instruções de execução e onde cada padrão foi implementado. Os diagramas usam Mermaid, que o GitHub renderiza.

## Commit 33

```
docs: adiciona coleção do Postman
```

**Arquivos:**

- `postman/loja-roupas.postman_collection.json`

**Para entender:** Importe no Postman e rode a coleção (Runner). Já tem testes automáticos de status e casos de erro.
