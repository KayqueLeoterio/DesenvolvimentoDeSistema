# Serviço de Cadastro (Grupo 1)
Serviço responsável pelas pessoas da escola: **alunos** e **professores**.

## Como executar
O serviço sobe na **porta 8081**. 

Todas as URLs abaixo usam
`http://localhost:8081/alunos`.

Os dados ficam persistidos em `dados/alunos.json` e `dados/professores.json`,
na raiz do projeto. O arquivo é lido quando o serviço sobe e regravado a cada
alteração — se você reiniciar a aplicação, os dados cadastrados.

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
| GET    | `/professor`      | Lista todos os professores  | 200     | —                |
| GET    | `/professor/{id}` | Busca um professor pelo id      | 200     | 404 se não existir |
| POST   | `/professor`         | Cadastra um novo professor  | 201     | 400 dados inválidos |
| PUT    | `/professor/{id}`    | Atualiza um professor existente | 200     | 404 se não existir, 400 dados inválidos |
| DELETE | `/professor/{id}`    | Remove um professor             | 204     | 404 se não existir |

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

> Observação: se o campo `id` for enviado no corpo da requisição, ele é
> ignorado — quem gera o id é sempre o `AlunoRepository`, nunca o cliente
> (ver seção de anotações Jackson).

### Exemplo — erro (404)

`GET /alunos/999` quando o id 999 não existe:
```json
{
  "momento": "2026-09-15T10:32:00",
  "status": 404,
  "erro": "Não encontrado",
  "mensagem": "Aluno com id 999 não encontrado",
  "detalhes": []
}
```

### Exemplo — erro (400, validação)

`POST /alunos` sem o campo `nome`:
```json
{
  "momento": "2026-09-15T10:33:00",
  "status": 400,
  "erro": "Dados inválidos",
  "mensagem": "Um ou mais campos não passaram na validação",
  "detalhes": ["nome: nome é obrigatório"]
}
```

## Anotações do Jackson utilizadas

- **`@JsonFormat(pattern = "dd/MM/yyyy")`** no campo `nascimento` da classe
  `Aluno`: sem essa anotação, o Jackson serializaria a data como um array
  `[2005, 3, 15]` (padrão do `LocalDate`). Com ela, a API troca a data no
  formato `dd/MM/yyyy`, tanto para ler quanto para escrever, que é o formato
  que faz sentido para quem consome a API.
- **`@JsonInclude(JsonInclude.Include.NON_NULL)`** no nível da classe
  `Aluno`: omite do JSON de resposta qualquer campo que esteja nulo, em vez
  de devolver `"campo": null`. Deixa a resposta mais enxuta, especialmente
  útil se no futuro algum campo se tornar opcional.

  > **Por que não usamos `@JsonProperty(access = READ_ONLY)` no `id`?**
  > Chegamos a testar essa anotação para o Jackson ignorar um `id` enviado
  > pelo cliente no corpo da requisição. O problema é que `READ_ONLY` também
  > bloqueia a leitura do campo em **qualquer** desserialização — inclusive
  > quando o próprio `AlunoRepository` recarrega o `dados/alunos.json` ao
  > iniciar a aplicação. Com um arquivo já populado, todos os `id`
  > carregados voltavam `null`, e a aplicação quebrava com
  > `NullPointerException` ao calcular o próximo id disponível. Por isso
  > removemos essa anotação do campo `id` e passamos a garantir "id sempre
  > gerado pelo servidor" diretamente no `AlunoService`
  > (`aluno.setId(null)` antes de cadastrar) e no `AlunoRepository`
  > (que sempre sobrescreve o id na criação e na atualização).

## Regras de negócio implementadas

- **Matrícula única**: não é permitido cadastrar (POST) ou atualizar (PUT) um
  aluno com uma matrícula que já pertence a outro aluno. Se isso for
  tentado, o serviço devolve `400 Bad Request` com uma mensagem explicando o
  motivo. A validação está em `AlunoService.validarMatriculaUnica`.
- **Validação de campos** (Bean Validation, `jakarta.validation`): nome,
  matrícula, e-mail, curso e ano de ingresso são obrigatórios; o e-mail
  precisa ter formato válido; o ano de ingresso segue o padrão `AAAA/1` ou
  `AAAA/2`; e a data de nascimento precisa estar no passado.

## Organização em camadas

- **`controller`** — recebe a requisição HTTP, valida a entrada com
  `@Valid` e decide o código de status da resposta. Não sabe nada sobre como
  os dados são persistidos.
- **`service`** — concentra as regras de negócio (ex: matrícula única). É a
  única camada que decide se uma operação pode ou não acontecer.
- **`repository`** — é o único lugar do projeto que sabe que os alunos estão
  guardados em `dados/alunos.json`. Lê o arquivo quando a aplicação sobe e
  regrava a cada alteração. Se um dia trocarmos por um banco de dados de
  verdade, só essa classe muda.
- **`exception` / `TratarExecaoController`** — tratamento de erros
  centralizado com `@RestControllerAdvice`, sempre devolvendo JSON (nunca um
  200 com erro no corpo).

## O que cada integrante desenvolveu

**Kayque Leotério** — Aluno
  - Model `Aluno`; 
  - Repository `AlunoRepository`; 
  - Service `AlunoService`;
  - Controller `AlunoController`;
  - Tratamento de erros `TratarExecaoController`;
  - Exceções de Alunos  `ErroRespostaAlunoDTO`,`AlunoInvalidoException` e `AlunoNaoEncontradoException` .

**Luiza Mattos** — Professor 
  - Model `Professor`; 
  - Repository `ProfessorRepository`; 
  - Service `ProfessorService`; 
  - Controller `ProfessorController`;  
  - Tratamento de erros `TratarExecaoController`;
  - Exceções  `ErroResposta`.
