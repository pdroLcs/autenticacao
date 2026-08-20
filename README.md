# Autenticação API

Projeto Java com Spring Boot para autenticação de usuários utilizando JWT (JSON Web Token), Spring Security e PostgreSQL.

## O que é o projeto

Esta aplicação expõe uma API REST para:

- registrar usuários
- fazer login
- renovar tokens de acesso com refresh token
- fazer logout
- proteger endpoints com autenticação baseada em JWT

A estrutura é focada em autenticação, com persistência no banco PostgreSQL e migrações automatizadas via Flyway.

## Stack principal

- Java 21
- Spring Boot 3 / Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT (Auth0)
- Springdoc OpenAPI / Swagger

## Estrutura do projeto

```text
autenticacao/
├── src/
│   ├── main/
│   │   ├── java/dev/pdrolcs/autenticacao/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── docs/
│   │   │   └── AutenticacaoApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/dev/pdrolcs/autenticacao/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Endpoints principais

A API principal fica em:

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`

## Requisitos

Antes de rodar, certifique-se de ter:

- Java 21+
- Maven (ou use o wrapper `mvnw`)
- PostgreSQL em execução

## Variáveis de ambiente

Configure as seguintes variáveis no ambiente antes de iniciar a aplicação:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/autenticacao
export DB_USERNAME=seu_usuario
export DB_PASSWORD=sua_senha
export JWT_SECRET=sua_chave_secreta
```

No Windows PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/autenticacao"
$env:DB_USERNAME="seu_usuario"
$env:DB_PASSWORD="sua_senha"
$env:JWT_SECRET="sua_chave_secreta"
```

## Como rodar o projeto

Na raiz do projeto:

```bash
./mvnw spring-boot:run
```

Ou, para gerar o jar e rodar:

```bash
./mvnw clean package
java -jar target/autenticacao-0.0.1-SNAPSHOT.jar
```

A aplicação geralmente sobe na porta padrão:

- `http://localhost:8080`

Se estiver usando Swagger/OpenAPI, a documentação pode ficar em:

- `http://localhost:8080/swagger-ui/index.html`

## Como rodar testes

Para executar a suíte de testes do projeto:

```bash
./mvnw test
```

Se quiser apenas compilar e validar a aplicação sem rodar testes:

```bash
./mvnw test-compile
```