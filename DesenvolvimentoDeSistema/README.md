# Serviço de Cadastro (Grupo 1)
Serviço responsável pelas pessoas da escola: **alunos** e **professores**.

## Repositório: https://github.com/KayqueLeoterio/DesenvolvimentoDeSistema.git

## Sumário

- [Trabalho 1](#trabalho-1) — CRUD de alunos e professores (persistência em arquivos `.json`)
- [Trabalho 2](#trabalho-2) — banco H2 com Spring Data JPA e comunicação entre serviços
  - [Requisitos do Grupo 1 (Cadastro)](#requisitos-do-grupo-1-cadastro) — o que cumpre cada requisito e onde está no código
  - [Endpoints para testar no Postman](#endpoints-para-testar-no-postman)
  - [Endpoints do serviço](#endpoints-do-serviço)
  - [Banco de dados](#banco-de-dados)
  - [Comunicação com outros serviços](#comunicação-com-outros-serviços)
  - [Regras de integridade entre serviços](#regras-de-integridade-entre-serviços)
  - [Como subir os três serviços juntos](#como-subir-os-três-serviços-juntos)

---

## Trabalho 1

> **Nota:** esta seção documenta o Trabalho 1, em que os dados eram gravados em arquivos `.json`. No Trabalho 2 essa persistência foi substituída por um banco H2 com Spring Data JPA (a pasta `dados/` não existe mais). O estado atual do serviço está descrito em [Trabalho 2](#trabalho-2).

### Como executar
O serviço sobe na **porta 8081**. 

As URLs usam os caminhos base:
* Alunos: `http://localhost:8081/alunos`
* Professores: `http://localhost:8081/professores`

Os dados ficam persistidos em `dados/alunos.json` e `dados/professores.json`, na raiz do projeto. O arquivo é lido quando o serviço sobe e regravado a cada alteração — garantindo a persistência das informações mesmo se a aplicação for reiniciada.

### Endpoints — Aluno

| Método | Caminho              | Descrição                   | Sucesso | Erros            |
|--------|-----------------------|------------------------------|---------|------------------|
| GET    | `/alunos`             | Lista todos os alunos        | 200     | —                |
| GET    | `/alunos/{id}`        | Busca um aluno pelo id       | 200     | 404 se não existir |
| POST   | `/alunos`             | Cadastra um novo aluno       | 201     | 400 dados inválidos / matrícula duplicada |
| PUT    | `/alunos/{id}`        | Atualiza um aluno existente  | 200     | 404 se não existir, 400 dados inválidos |
| DELETE | `/alunos/{id}`        | Remove um aluno              | 204     | 404 se não existir |

### Endpoints — Professor

| Método | Caminho           | Descrição                   | Sucesso | Erros            |
|--------|-------------------|-----------------------------|---------|------------------|
| GET    | `/professores`      | Lista todos os professores (aceita query `area`) | 200     | —                |
| GET    | `/professores/{id}` | Busca um professor pelo id  | 200     | 404 se não existir |
| POST   | `/professores`      | Cadastra um novo professor  | 201     | 400 dados inválidos / SIAPE duplicado |
| PUT    | `/professores/{id}` | Atualiza um professor existente | 200     | 404 se não existir, 400 dados inválidos |
| DELETE | `/professores/{id}` | Remove um professor         | 204     | 404 se não existir |

---

### Exemplos Práticos de Requisição e Resposta

#### Exemplo — POST /alunos
Requisição:
```json
{
  "nome": "Ana Souza",
  "matricula": "2023001",
  "email": "ana.souza@escola.edu.br",
  "curso": "Informática",
  "anoIngresso": "2023/1",
  "nascimento": "15/03/2005"
}
```
Resposta (201 Created):
```json
{
  "id": 1,
  "nome": "Ana Souza",
  "matricula": "2023001",
  "email": "ana.souza@escola.edu.br",
  "curso": "Informática",
  "anoIngresso": "2023/1",
  "nascimento": "15/03/2005"
}
```

#### Exemplo — POST /professores
Requisição:
```json
{
  "nome": "Manoella Azevedo",
  "siape": "1234567",
  "area": "Informática",
  "email": "manoella.azevedo@ifsul.edu.br"
}
```
Resposta (201 Created):
```json
{
  "id": 1,
  "nome": "Manoella Azevedo",
  "siape": "1234567",
  "area": "Informática",
  "email": "manoella.azevedo@ifsul.edu.br"
}
```
> **Observação:** Se o campo `id` for enviado no corpo da requisição em qualquer POST, ele será ignorado pelo servidor — quem gerencia a geração incremental do id são as classes de repositório (`AlunoRepository` e `ProfessorRepository`).

#### Exemplo — GET /professores (Com filtro por área)
Chamada por Query String: `GET http://localhost:8081/professores?area=Informatica`

Resposta (200 OK):
```json
[
  {
    "id": 1,
    "nome": "Manoella Azevedo",
    "siape": "1234567",
    "area": "Informática",
    "email": "manoella.azevedo@ifsul.edu.br"
  }
]
```

#### Exemplo — erro (404 Not Found)
`GET /alunos/999` ou `GET /professores/999` quando o identificador não existe:
```json
{
  "momento": "2026-09-15T10:32:00",
  "status": 404,
  "erro": "Não encontrado",
  "mensagem": "Aluno com id 999 não encontrado",
  "detalhes": []
}
```

#### Exemplo — erro (400 Bad Request, validação)
`POST /alunos` sem o campo `nome`:
```json
{
  "momento": "2026-09-15T10:33:00",
  "status": 400,
  "erro": "Dados inválidos",
  "mensagem": "Um ou mais campos não passaram na validação",
  "detalhes": ["nome: Nome é obrigatório"]
}
```

#### Exemplo — erro (400 Bad Request, SIAPE duplicado)
`POST /professores` informando um código SIAPE pertencente a outro docente:
```json
{
  "momento": "2026-09-15T11:05:22",
  "status": 400,
  "erro": "Requisição inválida",
  "mensagem": "Já existe um professor cadastrado com o SIAPE 1234567",
  "detalhes": []
}
```

---

### Anotações do Jackson utilizadas

- **`@JsonFormat(pattern = "dd/MM/yyyy")`** no campo `nascimento` da classe `Aluno`: sem essa anotação, o Jackson serializaria a data como um array `[2005, 3, 15]` (padrão do `LocalDate`). Com ela, a API troca a data no formato estruturado `dd/MM/yyyy`, facilitando o consumo externo.
- **`@JsonInclude(JsonInclude.Include.NON_NULL)`** no nível das classes `Aluno` e `Professor`: omite do JSON de resposta qualquer propriedade que esteja com valor nulo, poupando tráfego de dados e deixando os objetos mais enxutos.

> **Por que não usamos `@JsonProperty(access = READ_ONLY)` no `id`?**
> Testamos essa anotação para o Jackson ignorar um `id` enviado no payload. O problema é que `READ_ONLY` bloqueia a leitura do campo em qualquer desserialização — inclusive quando o repositório lê o arquivo local (`.json`) ao inicializar a aplicação. Com isso, os registros populados carregavam com IDs nulos, quebrando as rotas com `NullPointerException`. Removemos a propriedade e tratamos o comportamento diretamente no escopo interno da aplicação.

---

### Regras de negócio implementadas

#### Alunos
- **Matrícula única**: Não é permitido cadastrar (POST) ou atualizar (PUT) um aluno com uma matrícula que já pertence a outro discente. Devolve `400 Bad Request`.
- **Validação de campos (Bean Validation)**: `nome`, `matricula`, `email`, `curso` e `nascimento` são obrigatórios. O e-mail precisa ter formato válido, o ano de ingresso segue o padrão regulamentado `AAAA/1` ou `AAAA/2`, e a data de nascimento obrigatoriamente deve estar no passado.

#### Professores
- **SIAPE único**: Não é permitido duplicar o número de identificação do SIAPE entre professores diferentes em rotas de cadastro ou modificação. Caso aconteça, o sistema lança `ProfessorInvalidoException` capturada como `400 Bad Request`.
- **Validação de campos (Bean Validation)**: Os campos `nome`, `siape`, `area` e `email` são estritamente obrigatórios. O campo de correio eletrônico valida a tipagem padrão com `@Email`.
- **Filtro na listagem**: O endpoint de busca geral de docentes aceita um parâmetro de filtragem por query string baseado na sua disciplina/área de atuação (`?area=nome_da_area`).

---

### Organização em camadas

- **`controller`** — intercepta as requisições HTTP, valida os modelos com `@Valid` e despacha as respostas estruturadas com seus respectivos HTTP Status Codes.
- **`service`** — centraliza os fluxos lógicos e validações imperativas do negócio acadêmico (ex: chaves e códigos únicos).
- **`repository`** — camada isolada responsável por gerenciar a leitura e persistência em arquivos locais JSON na pasta de destino `dados/`.
- **`exception` / `TratarExecaoController`** — escuta global e unificada de exceções com a diretiva `@RestControllerAdvice`, montando e devolvendo payloads de erro sem vazar informações da pilha de execução.

---

### O que cada integrante desenvolveu

**Kayque Leotério** — Aluno
  - Model `Aluno`; 
  - Repository `AlunoRepository`; 
  - Service `AlunoService`;
  - Controller `AlunoController`;
  - Tratamento de erros centralizado `TratarExecaoController`;
  - Exceções e DTOs de Alunos: `ErroRespostaAlunoDTO`, `AlunoInvalidoException` e `AlunoNaoEncontradoException`.

**Luiza Mattos** — Professor 
  - Model `Professor`; 
  - Repository `ProfessorRepository`; 
  - Service `ProfessorService`; 
  - Controller `ProfessorController`;  
  - Tratamento e mapeamento das rotas de Professor no `TratarExecaoController`;
  - Exceções e DTOs de Professores: `ErroRespostaProfessorDTO`, `ProfessorInvalidoException` e `ProfessorNaoEncontradoException`.


---

## Trabalho 2

No Trabalho 2 o serviço de Cadastro passou a guardar os dados em um **banco H2 relacional** (Spring Data JPA) e a **consultar os serviços de Turmas (8082) e Matrículas (8083) por HTTP** para impedir remoções que deixariam o sistema inconsistente.

### Requisitos do Grupo 1 (Cadastro)

| # | Requisito | Como foi cumprido | Onde está no código |
|---|-----------|-------------------|---------------------|
| 1 | Recusar a remoção de professor responsável por turma, com **409** | Antes de apagar, o service consulta o serviço de Turmas (`GET /turmas?professorId={id}`). Se vier alguma turma, lança `ConflitoException`, que o `@RestControllerAdvice` converte em 409. | `ProfessorService.remover` → `TurmasClient.listarTurmasDoProfessor` |
| 2 | Recusar a remoção de aluno com matrícula ativa, com **409** | Antes de apagar, o service consulta o serviço de Matrículas (`GET /matriculas?alunoId={id}&situacao=ativa`). Se vier alguma matrícula, lança `ConflitoException` (409). Matrículas trancadas não bloqueiam a remoção. | `AlunoService.remover` → `MatriculasClient.alunoPossuiMatriculaAtiva` |
| 3 | Oferecer `GET /professores/{id}/turmas` | Confere se o professor existe (404 se não) e devolve as turmas buscadas em tempo real no serviço de Turmas, sem gravá-las no banco do Cadastro. | `ProfessorController.listarTurmas` → `ProfessorService.listarTurmas` → `TurmasClient` |

**Fluxo da remoção (professor e aluno):**
1. Confere se o registro existe → **404** se não existir (nenhum outro serviço é consultado).
2. Consulta o outro serviço pelo `client` → **503** se ele estiver fora do ar, estourar o tempo limite ou responder com erro.
3. Se houver vínculo (turma ou matrícula ativa) → **409**.
4. Caso contrário, remove → **204**.

**Exemplo — 409 ao remover professor responsável por turma** (`DELETE /professores/1`):
```json
{
  "momento": "2026-11-24T21:40:12",
  "status": 409,
  "erro": "Conflito",
  "mensagem": "Professor com id 1 é responsável por turma(s) e não pode ser removido",
  "detalhes": []
}
```

**Exemplo — 409 ao remover aluno com matrícula ativa** (`DELETE /alunos/1`):
```json
{
  "momento": "2026-11-24T21:41:03",
  "status": 409,
  "erro": "Conflito",
  "mensagem": "Aluno com id 1 possui matrícula ativa e não pode ser removido",
  "detalhes": []
}
```

**Exemplo — 503 com o serviço consultado fora do ar** (`DELETE /professores/1` com Turmas desligado):
```json
{
  "momento": "2026-11-24T21:42:30",
  "status": 503,
  "erro": "Serviço indisponível",
  "mensagem": "O serviço de Turmas está fora do ar ou não respondeu a tempo.",
  "detalhes": []
}
```

**Exemplo — `GET /professores/1/turmas`** (200 OK). A lista é a que o serviço de Turmas devolve em `content`, sem alterações:
```json
[
  {
    "id": 1,
    "codigo": "DSC01",
    "nomeDisciplina": "Desenvolvimento de Sistemas Computacionais",
    "professorId": 1,
    "semestre": "2026/2",
    "sala": "lab 03",
    "diasSemana": ["SEGUNDA", "QUARTA"],
    "horarioInicial": "19:00",
    "horarioFinal": "22:15",
    "cargaHoraria": 60,
    "numeroVagas": 30
  }
]
```
Professor sem turmas devolve `[]`. Professor inexistente devolve 404.

---

### Endpoints para testar no Postman

Com os três serviços no ar e a carga inicial de cada um. Rode **nesta ordem**: os `DELETE` que dão 204 apagam dados. Para recomeçar, pare o serviço e apague a pasta `banco/`; a carga inicial recria tudo.

| Método | URL | Esperado | O que prova |
|--------|-----|----------|-------------|
| GET | `http://localhost:8081/professores/1/turmas` | 200, lista com as turmas do professor 1 | Requisito 3: busca no serviço de Turmas |
| GET | `http://localhost:8081/professores/3/turmas` | 200, `[]` | Professor sem turmas |
| GET | `http://localhost:8081/professores/999/turmas` | 404 | Professor inexistente |
| DELETE | `http://localhost:8081/professores/1` | **409** | Requisito 1: professor com turma |
| DELETE | `http://localhost:8081/alunos/1` | **409** | Requisito 2: aluno com matrícula ativa |
| DELETE | `http://localhost:8081/professores/999` | 404 | Id inexistente |
| DELETE | `http://localhost:8081/alunos/999` | 404 | Id inexistente |
| DELETE | `http://localhost:8081/alunos/7` | 204 | Aluno só com matrícula trancada pode ser removido |
| DELETE | `http://localhost:8081/alunos/6` | 204 | Aluno sem matrícula |
| DELETE | `http://localhost:8081/professores/3` | 204 | Professor sem turma |

**Teste do 503:**
1. Desligue o serviço de Turmas e repita `GET /professores/1/turmas` e `DELETE /professores/1`. Os dois devem devolver **503**.
2. Suba o Turmas de novo, desligue o de Matrículas e repita `DELETE /alunos/1`. Deve devolver **503**.
3. Com o Turmas desligado, `GET /professores/999/turmas` continua devolvendo 404, porque o professor é conferido no banco antes de consultar o outro serviço.

**URLs de apoio** (conferem se os outros serviços estão respondendo):
- `http://localhost:8082/turmas?professorId=1`
- `http://localhost:8083/matriculas?alunoId=1&situacao=ativa`

**Documentação (Swagger UI):** `http://localhost:8081/swagger-ui/index.html`

---

### Endpoints do serviço

| Método | Caminho | Descrição | Sucesso | Erros |
|--------|---------|-----------|---------|-------|
| GET | `/alunos` | Lista alunos, paginado (`?page=0&size=10&sort=nome`) | 200 | 400 parâmetro ou ordenação inválidos |
| GET | `/alunos/{id}` | Busca um aluno | 200 | 404 |
| POST | `/alunos` | Cadastra um aluno | 201 | 400 dados inválidos ou matrícula duplicada |
| PUT | `/alunos/{id}` | Atualiza um aluno | 200 | 400, 404 |
| DELETE | `/alunos/{id}` | Remove um aluno | 204 | 404, **409** matrícula ativa, **503** Matrículas indisponível |
| GET | `/professores` | Lista professores, paginado (aceita `?area=`) | 200 | 400 parâmetro ou ordenação inválidos |
| GET | `/professores/{id}` | Busca um professor | 200 | 404 |
| GET | `/professores/{id}/turmas` | Turmas pelas quais o professor é responsável | 200 | 404 professor inexistente, **503** Turmas indisponível |
| POST | `/professores` | Cadastra um professor | 201 | 400 dados inválidos ou SIAPE duplicado |
| PUT | `/professores/{id}` | Atualiza um professor | 200 | 400, 404 |
| DELETE | `/professores/{id}` | Remove um professor | 204 | 404, **409** responsável por turma, **503** Turmas indisponível |

---

### Banco de dados

* **Banco:** H2, gravado em arquivo: `jdbc:h2:file:./banco/cadastro` (usuário `sa`, senha vazia). A pasta `banco/` é criada na primeira execução e está no `.gitignore`.
* **Persistência:** os dados continuam lá quando o serviço é reiniciado.
* **Como acessar:** pelo console web do H2 em `http://localhost:8081/h2-console` (informe a URL JDBC acima) ou por um cliente SQL (IntelliJ Database, DBeaver) usando a mesma URL. Para usar um cliente externo, pare o serviço antes, porque o H2 em arquivo trava o banco enquanto está em uso.
* **Carga inicial:** a classe `CargaInicial` (`CommandLineRunner`) cadastra 6 professores e 8 alunos de exemplo **somente se as tabelas estiverem vazias** (`count() == 0`), sem duplicar a cada reinício.

**Entidades e tabelas**

| Entidade | Tabela | Colunas |
|----------|--------|---------|
| `Aluno` | `alunos` | `id` (PK, gerado), `nome`, `matricula`, `email`, `curso`, `ano_ingresso`, `nascimento` |
| `Professor` | `professores` | `id` (PK, gerado), `nome`, `siape`, `area`, `email` |

**Repositórios** (interfaces `JpaRepository`): `findAll(Pageable)` para as listagens paginadas, `ProfessorRepository.findByAreaIgnoreCase(area, pageable)` para o filtro, e `existsByMatriculaIgnoreCase`, `existsBySiapeIgnoreCase` e as variantes `...AndIdNot` (usadas na atualização para ignorar o próprio registro).

**Mudanças nas camadas:**
- `repository`: virou interface JPA; continua sendo a única camada que acessa o banco e não conhece HTTP.
- `client` (novo): `TurmasClient` e `MatriculasClient`, feitos com `RestClient`. Somente o `service` os usa.
- `config`: `RestClientConfig` (URLs e timeouts) e `CargaInicial`.
- `exception`: `ConflitoException` (409) e `ServicoIndisponivelException` (503), tratadas no `TratarExecaoController` (`@RestControllerAdvice`).

---

### Comunicação com outros serviços

**Endpoints consumidos**

| Serviço | Chamada | Para quê |
|---------|---------|----------|
| Turmas (8082) | `GET /turmas?professorId={id}` | Impedir a remoção de professor responsável por turma e alimentar `GET /professores/{id}/turmas` |
| Matrículas (8083) | `GET /matriculas?alunoId={id}&situacao=ativa` | Impedir a remoção de aluno com matrícula ativa |

As respostas podem vir como lista simples ou como página do Spring Data (campo `content`); o serviço aceita os dois formatos. O valor de situação usado é `ativa` (o serviço de Matrículas aceita maiúsculas e minúsculas).


