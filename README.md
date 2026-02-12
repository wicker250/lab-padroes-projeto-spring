# Explorando Padrões de Projetos na Prática com Java

Repositório com as implementações dos padrões de projeto explorados no Lab "Explorando Padrões de Projetos na Prática com Java". Especificamente, este projeto explorou alguns padrões usando o Spring Framework, são eles:
- Singleton
- Strategy/Repository
- Facade

---

## Modificações deste fork (refatorações e melhorias)

Este repositório é um **fork** do projeto original do Lab e contém ajustes para deixar a API mais próxima de um padrão “produção”, mantendo a ideia didática do exercício.

### O que foi melhorado
- **DTOs**: a API não expõe mais entidades JPA diretamente no controller (entrada/saída via DTO).
- **Validação de entrada**: `@Valid` + regras como `nome` obrigatório e `cep` com 8 dígitos.
- **HTTP Status mais corretos**:
  - `POST /clientes` retorna **201 Created** e header `Location`.
  - `DELETE /clientes/{id}` retorna **204 No Content**.
  - `GET/PUT/DELETE` para id inexistente retorna **404 Not Found**.
  - Erros de payload/CEP inválido retornam **400 Bad Request**.
  - Falha na consulta ao ViaCEP retorna **502 Bad Gateway**.
- **Tratamento global de erros** (`@ControllerAdvice`) com JSON padronizado.
- **Correção do PUT**: o `id` do path é aplicado no objeto antes de salvar, evitando inserir um novo registro sem querer.
- **Normalização do CEP**: remove caracteres não numéricos (ex.: `01001-000` → `01001000`) e valida tamanho.
- **Configuração adicionada**: `application.properties` (H2, Swagger, timeouts do Feign, etc).

### Como executar
Requisitos:
- Java 11
- Maven (ou use o wrapper `mvnw` do projeto)

Rodar:
- Windows:
  ```bash
  mvnw.cmd spring-boot:run
