# Roteiro para apresentação (7 minutos)

## Demonstração — 5 minutos

1. **0:00–0:40:** explique a separação Repository, Service e Controller. Mostre `Conta` e a exceção `SaldoInsuficienteException`.
2. **0:40–2:00:** abra `ContaServiceTest`. Mostre o caso de transferência com saldo, o de saldo insuficiente e um teste parametrizado de valores inválidos. Execute `executar.cmd verificar`.
3. **2:00–3:00:** abra `ContaControllerTest`. Explique que MockMvc chama o fluxo real com Spring e H2. Mostre as asserções HTTP 200/400 e a verificação dos dois saldos persistidos.
4. **3:00–4:00:** abra `target/site/jacoco/index.html`. Entre nos pacotes `service` e `controller`. Mostre o limite de 80% por classe configurado no `pom.xml`.
5. **4:00–5:00:** execute `executar.cmd visivel`. Mostre Selenium preenchendo o depósito e verificando R$ 125,50. Mostre `git log --oneline` e explique a sequência RED, GREEN e REFACTOR.

Antes da apresentação, execute a suíte completa uma vez para baixar o driver e todas as dependências. Não dependa de downloads durante a demonstração. Os testes Selenium iniciam seu próprio servidor; não é necessário manter a aplicação aberta na porta 8080.

## Perguntas técnicas — 2 minutos

- **Para que serve `@Mock`?** Cria um substituto do repositório. O teste unitário não acessa o banco.
- **Para que serve `@InjectMocks`?** Instancia o serviço e injeta o repositório simulado pelo Mockito.
- **O que faz `when(...).thenReturn(...)`?** Define qual resultado o repositório simulado entrega para uma chamada específica.
- **Qual a diferença entre `assertEquals` e `verify`?** A primeira confere um resultado; a segunda confere que uma interação ocorreu.
- **Como testar uma exceção?** `assertThrows(SaldoInsuficienteException.class, () -> ...)` verifica o tipo lançado.
- **Por que usar `@Transactional`?** Débito e crédito pertencem à mesma transação: se uma operação falha, as alterações são revertidas.
- **Por que esperar no Selenium?** A resposta do `fetch` é assíncrona. `WebDriverWait` aguarda a condição observável, evitando um tempo fixo arbitrário.
- **Cobertura alta prova ausência de bugs?** Não. Indica código executado; a qualidade das asserções e a escolha de cenários continuam essenciais.
- **Como demonstrar um teste falhando?** Altere temporariamente o valor esperado do teste de transferência de 70 para 71. Execute o teste, explique a falha e restaure a asserção. Não altere o histórico para simular TDD.

Estude os testes e execute as operações antes de apresentar. Explique com transparência a implementação assistida e as decisões que compreende.
