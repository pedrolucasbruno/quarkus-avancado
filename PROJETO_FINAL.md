# 🏆 Projeto Final — Curso de Quarkus

> [!IMPORTANT]
> Os itens obrigatórios serão os itens 1, 2, 3, 4 e 5.

## 📦 API de Gerenciamento de Produtos

Você deverá construir uma API RESTful com **Quarkus** que gerencie um catálogo de produtos, com autenticação/autorização via **JWT**, persistência em banco de dados, **cache** e integração com **Kafka**.

---

## 🎯 Requisitos

### 1. Entidade `Produto`

Crie uma entidade `Produto` com os seguintes campos:

| Campo       | Tipo     | Descrição                  |
|-------------|----------|----------------------------|
| `id`        | `Long`   | Identificador (gerado)     |
| `nome`      | `String` | Nome do produto            |
| `descricao` | `String` | Descrição do produto       |
| `preco`     | `Double` | Preço do produto           |
| `estoque`   | `int`    | Quantidade em estoque      |

---

### 2. Entidade `Usuario`

Crie uma entidade `Usuario` com os seguintes campos:

| Campo    | Tipo     | Descrição                        |
|----------|----------|----------------------------------|
| `id`     | `Long`   | Identificador (gerado)           |
| `nome`   | `String` | Nome do usuário                  |
| `email`  | `String` | E-mail (único, usado como login) |
| `senha`  | `String` | Senha armazenada com hash BCrypt |
| `role`   | `String` | Role do usuário: `ADMIN` ou `USER` |

#### 👥 Usuários padrão (data migration ou ApplicationScoped)

Ao iniciar a aplicação, dois usuários devem estar disponíveis:

| Nome          | E-mail                  | Senha       | Role    |
|---------------|-------------------------|-------------|---------|
| Admin Sistema | admin@loja.com          | `admin123`  | `ADMIN` |
| User Padrão   | user@loja.com           | `user123`   | `USER`  |

> 💡 **Dica:** Você pode criá-los via `import.sql` na pasta `resources`, ou em um bean `@ApplicationScoped` com `@Observes StartupEvent`.

---

### 3. Endpoints de Autenticação (`/auth`)

#### `POST /auth/register` — Cadastro de usuário

**Request body:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "minhaSenha123",
  "role": "USER"
}
```

**Respostas esperadas:**

| Situação                     | Status | Body                                              |
|------------------------------|--------|---------------------------------------------------|
| Cadastro realizado           | `201`  | `{ "id": 1, "nome": "João Silva", "email": "joao@email.com", "role": "USER" }` |
| E-mail já cadastrado         | `409`  | `{ "erro": "E-mail já cadastrado" }`              |
| Campos obrigatórios ausentes | `400`  | `{ "erro": "Campos obrigatórios não informados" }` |

> ⚠️ A senha **não** deve ser retornada em nenhuma resposta.

---

#### `POST /auth/login` — Login e geração de token JWT

**Request body:**
```json
{
  "email": "admin@loja.com",
  "senha": "admin123"
}
```

**Respostas esperadas:**

| Situação              | Status | Body                                                          |
|-----------------------|--------|---------------------------------------------------------------|
| Login bem-sucedido    | `200`  | `{ "token": "<jwt>", "tipo": "Bearer", "role": "ADMIN" }`    |
| Credenciais inválidas | `401`  | `{ "erro": "E-mail ou senha inválidos" }`                     |

> O token retornado deve ser utilizado no header `Authorization: Bearer <token>` em todas as demais requisições autenticadas.

---

### 4. CRUD de Produtos (`/produtos`)

Todos os endpoints abaixo exigem autenticação via JWT no header:
```
Authorization: Bearer <token>
```

---

#### `GET /produtos` — Listar todos os produtos
- **Roles permitidas:** `USER`, `ADMIN`
- **Cache:** resultado cacheado

| Situação               | Status | Body                                |
|------------------------|--------|-------------------------------------|
| Sucesso                | `200`  | `[ { "id": 1, "nome": "...", "descricao": "...", "preco": 9.90, "estoque": 10 }, ... ]` |
| Sem token              | `401`  | `{ "erro": "Não autenticado" }`     |

---

#### `GET /produtos/{id}` — Buscar produto por ID
- **Roles permitidas:** `USER`, `ADMIN`
- **Cache:** resultado cacheado

| Situação               | Status | Body                                                                          |
|------------------------|--------|-------------------------------------------------------------------------------|
| Produto encontrado     | `200`  | `{ "id": 1, "nome": "...", "descricao": "...", "preco": 9.90, "estoque": 10 }` |
| Produto não encontrado | `404`  | `{ "erro": "Produto não encontrado" }`                                        |
| Sem token              | `401`  | `{ "erro": "Não autenticado" }`                                               |

---

#### `POST /produtos` — Cadastrar produto
- **Roles permitidas:** `ADMIN`
- **Invalida o cache**

**Request body:**
```json
{
  "nome": "Caneta Azul",
  "descricao": "Caneta esferográfica azul",
  "preco": 1.99,
  "estoque": 3
}
```

| Situação                     | Status | Body                                                                          |
|------------------------------|--------|-------------------------------------------------------------------------------|
| Produto criado               | `201`  | `{ "id": 1, "nome": "Caneta Azul", "descricao": "...", "preco": 1.99, "estoque": 3 }` |
| Campos obrigatórios ausentes | `400`  | `{ "erro": "Campos obrigatórios não informados" }`                            |
| Sem token                    | `401`  | `{ "erro": "Não autenticado" }`                                               |
| Role insuficiente (USER)     | `403`  | `{ "erro": "Acesso negado" }`                                                 |

> ⚠️ Se `estoque < 5`, um evento deve ser enviado ao tópico Kafka `estoque-baixo`.

---

#### `PUT /produtos/{id}` — Atualizar produto
- **Roles permitidas:** `ADMIN`
- **Invalida o cache**

**Request body:** (mesma estrutura do POST, todos os campos)

| Situação                     | Status | Body                                                                               |
|------------------------------|--------|------------------------------------------------------------------------------------|
| Produto atualizado           | `200`  | `{ "id": 1, "nome": "...", "descricao": "...", "preco": 2.50, "estoque": 4 }`      |
| Produto não encontrado       | `404`  | `{ "erro": "Produto não encontrado" }`                                             |
| Campos obrigatórios ausentes | `400`  | `{ "erro": "Campos obrigatórios não informados" }`                                 |
| Sem token                    | `401`  | `{ "erro": "Não autenticado" }`                                                    |
| Role insuficiente (USER)     | `403`  | `{ "erro": "Acesso negado" }`                                                      |

> ⚠️ Se `estoque < 5`, um evento deve ser enviado ao tópico Kafka `estoque-baixo`.

---

#### `DELETE /produtos/{id}` — Remover produto
- **Roles permitidas:** `ADMIN`
- **Invalida o cache**

| Situação                 | Status | Body                                   |
|--------------------------|--------|----------------------------------------|
| Produto removido         | `204`  | *(sem body)*                           |
| Produto não encontrado   | `404`  | `{ "erro": "Produto não encontrado" }` |
| Sem token                | `401`  | `{ "erro": "Não autenticado" }`        |
| Role insuficiente (USER) | `403`  | `{ "erro": "Acesso negado" }`          |

---

### 5. Segurança com JWT

- Configure a segurança da API utilizando **Quarkus SmallRye JWT** (`quarkus-smallrye-jwt`).
- O token JWT deve conter as claims: `sub` (e-mail), `groups` (role), `iat`, `exp`.
- Tempo de expiração sugerido: **1 hora**.
- Endpoints não autenticados devem retornar `401 Unauthorized`.
- Endpoints autenticados com role incorreta devem retornar `403 Forbidden`.

---

### 6. Cache nas buscas

- Utilize a extensão **Quarkus Cache** (`quarkus-cache`) para cachear os resultados de `GET /produtos` e `GET /produtos/{id}`.
- Ao cadastrar, atualizar ou deletar um produto, o cache deve ser **invalidado**.

```java
// Exemplo esperado
@GET
@CacheResult(cacheName = "produtos")
public List<Produto> listar() { ... }

@POST
@CacheInvalidateAll(cacheName = "produtos")
public Response cadastrar(Produto produto) { ... }
```

---

### 7. Integração com Kafka — Alerta de Estoque Baixo

- Utilize a extensão **SmallRye Reactive Messaging** com Kafka (`quarkus-smallrye-reactive-messaging-kafka`).
- Sempre que um produto for **cadastrado ou atualizado** com `estoque < 5`, envie uma mensagem para o tópico `estoque-baixo`.
- A **mesma aplicação** deve consumir esse tópico e exibir o log abaixo:

```
[ALERTA DE ESTOQUE] Produto 'Caneta Azul' com estoque baixo (3 unidades). E-mail de alerta enviado para estoque@empresa.com
```

> 💡 Use Dev Services do Quarkus: com `%dev.quarkus.kafka.devservices.enabled=true`, o Kafka sobe automaticamente sem instalação.

---

## 🛠️ Tecnologias Esperadas

- **Quarkus** (versão 3.x)
- **Hibernate ORM com Panache** (`quarkus-hibernate-orm-panache`)
- **Banco de dados:** PostgreSQL via Dev Services (`quarkus-jdbc-postgresql`)
- **SmallRye JWT** (`quarkus-smallrye-jwt` + `quarkus-smallrye-jwt-build`)
- **Quarkus Cache** (`quarkus-cache`)
- **SmallRye Reactive Messaging + Kafka** (`quarkus-smallrye-reactive-messaging-kafka`)

---

## ✅ Critérios de Avaliação

| Critério                                              | Pontuação |
|-------------------------------------------------------|-----------|
| CRUD funcional com persistência no banco              | 2,0 pts   |
| Endpoints de login e cadastro funcionando             | 1,5 pts   |
| Segurança com JWT e controle de roles (ADMIN / USER)  | 2,0 pts   |
| Cache implementado e invalidado corretamente          | 2,0 pts   |
| Integração com Kafka (producer + consumer + log)      | 1,5 pts   |
| Organização do código e boas práticas                 | 1,0 pt    |
| **Total**                                             | **10 pts**|

---

## 📁 Entregáveis

1. Repositório **Git** (GitHub, GitLab, etc.) com o código-fonte.
2. Arquivo `README.md` com instruções de como executar o projeto localmente.
3. Exemplos de requisições (arquivo `.http`, coleção do **Postman/Insomnia** ou `curl`).
4. Os **dois usuários padrão** (`admin@loja.com` / `user@loja.com`) devem funcionar ao subir a aplicação.

---

## 🚀 Como executar (sugestão para o README dos alunos)

```bash
# Executar em modo dev (Quarkus Dev Services cuida do banco e do Kafka)
./mvnw quarkus:dev
```

---

> 📌 **Observação:** O objetivo deste projeto é avaliar a sua capacidade de integrar as principais funcionalidades do ecossistema Quarkus aprendidas durante o curso. Foque em fazer funcionar corretamente antes de se preocupar com código "perfeito". Boa sorte! 🍀

