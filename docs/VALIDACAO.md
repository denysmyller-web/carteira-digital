# Validação executada em 20/11/2026

Ambiente: Windows, JDK 25, Maven 3.9.16, Spring Boot 3.5.16, H2, Selenium 4.27.0 e JaCoCo 0.8.14.

Comando equivalente reproduzível: `executar.cmd interface` (`mvnw -Pselenium clean verify`). Nesta sessão o Maven foi executado com um repositório de dependências temporário dentro da área de trabalho.

Resultado: **BUILD SUCCESS**, **43 testes**, zero falhas, zero erros, zero testes ignorados.

- Serviço (JUnit 5/Mockito): 26 testes, incluindo expansões dos testes parametrizados.
- Integração HTTP (MockMvc/Spring/H2): 15 testes.
- Interface (Selenium/Chrome): 2 testes.

## Cobertura de linhas por classe

| Classe | Linhas cobertas | Linhas não cobertas | Cobertura |
| --- | ---: | ---: | ---: |
| `ContaService` | 30 | 0 | 100.00% |
| `ContaController` | 6 | 0 | 100.00% |
| `ContaController$SaldoResponse` | 1 | 0 | 100.00% |
| `ContaController$CriarContaRequest` | 1 | 0 | 100.00% |
| `ContaController$TransferenciaRequest` | 1 | 0 | 100.00% |
| `ContaController$ValorRequest` | 1 | 0 | 100.00% |
| `ApiExceptionHandler` | 4 | 0 | 100.00% |
| `ContaController$MensagemResponse` | 1 | 0 | 100.00% |

O limite JaCoCo de 80% por classe de serviço e controle foi aprovado. Abra `target/site/jacoco/index.html` na pasta do projeto para consultar o relatório completo.

Os testes de interface confirmaram depósito de R$ 25,50 em uma conta com R$ 100,00, resultando em R$ 125,50 persistidos no H2, e mensagem de erro para conta inexistente. Captura em `target/evidencias/deposito-selenium.png`.
