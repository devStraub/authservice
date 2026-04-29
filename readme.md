# 🔐 Auth Service

Microsserviço de autenticação desenvolvido com Spring Boot, projetado para ser reutilizável em múltiplas aplicações.

Suporta múltiplos métodos de autenticação e emissão de tokens JWT para integração com outros microsserviços.

---

## 🚀 Tecnologias

* Java 17
* Spring Boot
* Spring Security
* OAuth2 Client
* JWT (io.jsonwebtoken)
* JPA / Hibernate
* H2 (dev/test)
* PostgreSQL (produção)
* Maven

---

## 🧠 Arquitetura

Este serviço atua como **Identity Provider (Auth Server)** dentro de uma arquitetura de microsserviços.

### Responsabilidades:

* Autenticação de usuários
* Emissão de tokens JWT
* Integração com provedores externos (OAuth2)
* Base para autenticação passwordless (OTP)

---

## 🔑 Métodos de Autenticação

### 1. Login com Email e Senha

```http
POST /auth/login
```

---

### 2. Login Social (OAuth2)

Suporte a:

* Google
* Microsoft (planejado)

Endpoint:

```http
GET /oauth2/authorization/{provider}
```

---

### 3. OTP (Passwordless) *(em desenvolvimento)*

* Envio de código por email/SMS
* Validação por código temporário

---

## 🔐 JWT

Todos os métodos de autenticação retornam um token JWT que deve ser utilizado para acessar outros microsserviços.

Exemplo:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## ⚙️ Configuração

### application.properties

```properties
jwt.secret=${JWT_SECRET}
jwt.expiration=3600000
```

---

## 🧪 Profiles

O projeto utiliza profiles para separar ambientes:

| Profile | Descrição                     |
| ------- | ----------------------------- |
| test    | Sem OAuth2, ambiente isolado  |
| dev     | OAuth2 habilitado (Google)    |
| prod    | Configuração real de produção |

---

## ▶️ Como rodar o projeto

### 1. Clonar o repositório

```bash
git clone https://github.com/devStraub/authservice.git
cd authservice
```

---

### 2. Rodar com Maven

```bash
mvn spring-boot:run
```

---

### 3. Acessar

```
http://localhost:8080
```

---

## 🧪 Testes básicos

### Registrar usuário

```http
POST /auth/register
```

### Login

```http
POST /auth/login
```

---

## 🔒 Segurança

* Senhas criptografadas com BCrypt
* Tokens JWT assinados
* Preparado para uso com API Gateway
* Suporte a OAuth2

---

## 📌 Próximos passos

* [ ] Integração completa com Google OAuth2
* [ ] Integração com Microsoft OAuth2
* [ ] Implementação de OTP (email/SMS)
* [ ] Refresh Token
* [ ] Controle de roles e permissões
* [ ] Integração com API Gateway

---

## 👨‍💻 Autor

Desenvolvido por Michel Pech

---

## 📄 Licença

Este projeto está sob a licença MIT.
