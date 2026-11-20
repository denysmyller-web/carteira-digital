# Carteira Digital — Testes Automatizados I

API REST em Java/Spring Boot para o projeto final do módulo BE-JV-011. Inclui criação de contas, consulta de saldo, depósito, transferência e página HTML para demonstração.

## Executar no Windows / VS Code

Abra esta pasta no VS Code. As extensões Java já estão instaladas na máquina utilizada para a validação.

No terminal, dentro da pasta do projeto:

```powershell
.\executar.cmd iniciar
```

Acesse http://127.0.0.1:8080. Crie duas contas pelo formulário, deposite em uma delas e faça uma transferência. Para encerrar, pressione Ctrl+C no terminal.

```powershell
.\executar.cmd verificar
.\executar.cmd interface
.\executar.cmd visivel
```

`verificar`: testes unitários e de integração, empacotamento e cobertura. `interface`: inclui os testes Selenium em modo headless. `visivel`: inclui Selenium com a janela do Chrome visível. As três opções executam `clean verify`; o relatório é recriado a cada execução.

No VS Code também é possível usar **Terminal > Executar Tarefa** e escolher uma das tarefas `Carteira:`. O arquivo de inicialização Java permite iniciar a classe principal pelo depurador e aponta para o JDK 25 desta máquina; ajuste `javaExec` em `.vscode/launch.json` ao usar outro computador.

Requisitos: JDK 17 a 25, Chrome para Selenium e acesso à internet no primeiro uso para baixar dependências e ChromeDriver. O script `executar.cmd` seleciona o JDK 25 do Eclipse Adoptium quando instalado; em outras máquinas, configure `JAVA_HOME`. O Maven Wrapper está incluído.

Comandos diretos e equivalentes para outros ambientes:

```sh
./mvnw clean verify
./mvnw -Pselenium clean verify
./mvnw spring-boot:run
```

O perfil `selenium` é necessário para executar os testes de interface. Eles não são silenciosamente ignorados quando o perfil está ativo: falhas no navegador fazem a compilação falhar.

## API

| Método | Caminho | Corpo JSON | Sucesso |
| --- | --- | --- | --- |
| POST | `/api/contas` | `{"titular":"Ana"}` | 201, conta com saldo zero |
| GET | `/api/contas/{id}/saldo` | — | 200, `{"saldo":0.0}` |
| POST | `/api/contas/{id}/depositos` | `{"valor":100.0}` | 200, conta atualizada |
| POST | `/api/transferencias` | `{"origemId":1,"destinoId":2,"valor":30.0}` | 200, mensagem |

Valores nulos, não finitos, zero ou negativos são rejeitados com `IllegalArgumentException` e HTTP 400. Transferência sem saldo lança `SaldoInsuficienteException` e retorna HTTP 400. Conta inexistente retorna 404. JSON malformado e identificador de formato inválido retornam 400. Origem e destino devem ser diferentes.

As transferências são transacionais: as duas contas são consultadas antes da alteração e os bloqueios são adquiridos em ordem crescente de ID para reduzir risco de deadlocks. Uma falha provoca rollback. A implementação mantém `Double` no saldo, conforme o enunciado; isso tem limitações de precisão decimal. Para um sistema financeiro real, use `BigDecimal` e defina regras de escala/arredondamento.

O banco H2 é em memória e não recebe dados iniciais. Os testes criam os próprios dados. O banco é apagado ao encerrar a aplicação. Esta é uma aplicação didática local sem autenticação, vinculada a `127.0.0.1`.

## Organização e testes

- `model/Conta`: entidade com `Long id`, `String titular` e `Double saldo`.
- `repository/ContaRepository`: persistência Spring Data JPA.
- `service/ContaService`: regras de negócio e transações.
- `controller/ContaController`: endpoints e DTOs.
- `controller/ApiExceptionHandler`: conversão de exceções em respostas HTTP.
- `ContaServiceTest`: JUnit 5, `@Mock`, `@InjectMocks`, `when`, `verify`, casos positivos e de exceção.
- `ContaControllerTest`: `@SpringBootTest`, `@AutoConfigureMockMvc` e H2 reais, sem mocks do serviço ou repositório.
- `DepositoSeleniumTest`: servidor em porta aleatória, ChromeDriver, espera explícita, formulário de depósito e mensagem de erro.

O JaCoCo verifica pelo menos **80% das linhas por classe** nos pacotes de serviço e controle. O build falha se alguma classe verificada ficar abaixo do limite. O relatório fica em `target/site/jacoco/index.html`, os resultados JUnit em `target/surefire-reports` e a captura Selenium em `target/evidencias/deposito-selenium.png`. Consulte também `docs/VALIDACAO.md`.

## TDD e Git

O repositório Git local incluído registra o trabalho efetivamente realizado, com autoria do assistente Codex:

1. **RED:** testes do serviço definidos com métodos ainda não implementados. A execução registrou 26 testes, com 20 falhas e 6 erros causados pela implementação provisória.
2. **GREEN:** implementação aprovada por 41 testes unitários e de integração.
3. **REFACTOR:** extração das validações e da busca com bloqueio, mantendo testes aprovados; validação completa também com Selenium e JaCoCo.

Use `git log --oneline` para consultar a sequência. Os registros de execução estão em `docs/evidencias-tdd.txt`. Não se trata de um histórico anterior do aluno: este histórico foi criado durante a implementação assistida.

O enunciado solicita entrega pelo link de um repositório público no GitHub. Este projeto foi criado **localmente e ainda não foi publicado**. Ao publicar, envie a branch `main` existente para preservar os commits; carregar arquivos pela interface web em um único commit perde o histórico TDD.

## Relação com as aulas

Foram consultados os tópicos Introdução aos Testes Automatizados, Testes Unitários, Testes Unitários com Spring, Test-Driven Development, Red-Green-Refactor, Testes de Integração, Testes de Interface e Abrangência, no [módulo Testes Automatizados I da turma 1745](https://lms.ada.tech/student/topics/by-class-id/aa5fa9b3-9f0b-467d-a9c5-44ff669fa2e3/by-module-id/8b425c31-ec82-4b3b-8d2b-55e60624c400).

O material apresenta alguns exemplos com `@Before`/`@After`; neste projeto JUnit 5 usa `@BeforeEach`/`@AfterEach`. Mockito é inicializado por `MockitoExtension`. As versões das bibliotecas foram atualizadas em relação aos exemplos antigos para compatibilidade com o ambiente.

Validação realizada com JDK 25 e Spring Boot 3.5.16. Compatibilidade conferida na [documentação oficial do Spring Boot](https://docs.spring.io/spring-boot/3.5/system-requirements.html). JaCoCo 0.8.14 suporta Java 25 conforme o [histórico oficial](https://www.jacoco.org/jacoco/trunk/doc/changes.html).

Veja `docs/APRESENTACAO.md` para um roteiro de defesa de sete minutos.
