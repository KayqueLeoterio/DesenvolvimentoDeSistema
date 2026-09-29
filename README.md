# Serviço de Cadastro (Grupo 1)
Serviço responsável pelas pessoas da escola: **alunos** e **professores**.

## Repositório: https://github.com/KayqueLeoterio/DesenvolvimentoDeSistema.git
## Como executar
O serviço sobe na **porta 8081**. 

As URLs usam os caminhos base:
* Alunos: `http://localhost:8081/alunos`
* Professores: `http://localhost:8081/professor`

Os dados ficam persistidos em `dados/alunos.json` e `dados/professores.json`, na raiz do projeto. O arquivo é lido quando o serviço sobe e regravado a cada alteração — garantindo a persistência das informações mesmo se a aplicação for reiniciada.

## Endpoints — Aluno

| Método | Caminho              | Descrição                   | Sucesso | Erros            |
|--------|-----------------------|------------------------------|---------|------------------|
| GET    | `/alunos`             | Lista todos os alunos        | 200     | —                |
| GET    | `/alunos/{id}`        | Busca um aluno pelo id       | 200     | 404 se não existir |
| POST   | `/alunos`             | Cadastra um novo aluno       | 201     | 400 dados inválidos / matrícula duplicada |
| PUT    | `/alunos/{id}`        | Atualiza um aluno existente  | 200     | 404 se não existir, 400 dados inválidos |
| DELETE | `/alunos/{id}`        | Remove um aluno              | 204     | 404 se não existir |

## Endpoints — Professor

| Método | Caminho           | Descrição                   | Sucesso | Erros            |
|--------|-------------------|-----------------------------|---------|------------------|
| GET    | `/professor`      | Lista todos os professores (aceita query `area`) | 200     | —                |
| GET    | `/professor/{id}` | Busca um professor pelo id  | 200     | 404 se não existir |
| POST   | `/professor`      | Cadastra um novo professor  | 201     | 400 dados inválidos / SIAPE duplicado |
| PUT    | `/professor/{id}` | Atualiza um professor existente | 200     | 404 se não existir, 400 dados inválidos |
| DELETE | `/professor/{id}` | Remove um professor         | 204     | 404 se não existir |

---

## Exemplos Práticos de Requisição e Resposta

### Exemplo — POST /alunos
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

### Exemplo — POST /professor
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

### Exemplo — GET /professor (Com filtro por área)
Chamada por Query String: `GET http://localhost:8081/professor?area=Informatica`

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

### Exemplo — erro (404 Not Found)
`GET /alunos/999` ou `GET /professor/999` quando o identificador não existe:
```json
{
  "momento": "2026-09-15T10:32:00",
  "status": 404,
  "erro": "Não encontrado",
  "mensagem": "Aluno com id 999 não encontrado",
  "detalhes": []
}
```

### Exemplo — erro (400 Bad Request, validação)
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

### Exemplo — erro (400 Bad Request, SIAPE duplicado)
`POST /professor` informando um código SIAPE pertencente a outro docente:
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

## Anotações do Jackson utilizadas

- **`@JsonFormat(pattern = "dd/MM/yyyy")`** no campo `nascimento` da classe `Aluno`: sem essa anotação, o Jackson serializaria a data como um array `[2005, 3, 15]` (padrão do `LocalDate`). Com ela, a API troca a data no formato estruturado `dd/MM/yyyy`, facilitando o consumo externo.
- **`@JsonInclude(JsonInclude.Include.NON_NULL)`** no nível das classes `Aluno` e `Professor`: omite do JSON de resposta qualquer propriedade que esteja com valor nulo, poupando tráfego de dados e deixando os objetos mais enxutos.

> **Por que não usamos `@JsonProperty(access = READ_ONLY)` no `id`?**
> Testamos essa anotação para o Jackson ignorar um `id` enviado no payload. O problema é que `READ_ONLY` bloqueia a leitura do campo em qualquer desserialização — inclusive quando o repositório lê o arquivo local (`.json`) ao inicializar a aplicação. Com isso, os registros populados carregavam com IDs nulos, quebrando as rotas com `NullPointerException`. Removemos a propriedade e tratamos o comportamento diretamente no escopo interno da aplicação.

---

## Regras de negócio implementadas

### Alunos
- **Matrícula única**: Não é permitido cadastrar (POST) ou atualizar (PUT) um aluno com uma matrícula que já pertence a outro discente. Devolve `400 Bad Request`.
- **Validação de campos (Bean Validation)**: `nome`, `matricula`, `email`, `curso` e `nascimento` são obrigatórios. O e-mail precisa ter formato válido, o ano de ingresso segue o padrão regulamentado `AAAA/1` ou `AAAA/2`, e a data de nascimento obrigatoriamente deve estar no passado.

### Professores
- **SIAPE único**: Não é permitido duplicar o número de identificação do SIAPE entre professores diferentes em rotas de cadastro ou modificação. Caso aconteça, o sistema lança `ProfessorInvalidoException` capturada como `400 Bad Request`.
- **Validação de campos (Bean Validation)**: Os campos `nome`, `siape`, `area` e `email` são estritamente obrigatórios. O campo de correio eletrônico valida a tipagem padrão com `@Email`.
- **Filtro na listagem**: O endpoint de busca geral de docentes aceita um parâmetro de filtragem por query string baseado na sua disciplina/área de atuação (`?area=nome_da_area`).

---

## Organização em camadas

- **`controller`** — intercepta as requisições HTTP, valida os modelos com `@Valid` e despacha as respostas estruturadas com seus respectivos HTTP Status Codes.
- **`service`** — centraliza os fluxos lógicos e validações imperativas do negócio acadêmico (ex: chaves e códigos únicos).
- **`repository`** — camada isolada responsável por gerenciar a leitura e persistência em arquivos locais JSON na pasta de destino `dados/`.
- **`exception` / `TratarExecaoController`** — escuta global e unificada de exceções com a diretiva `@RestControllerAdvice`, montando e devolvendo payloads de erro sem vazar informações da pilha de execução.

---

## O que cada integrante desenvolveu

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
