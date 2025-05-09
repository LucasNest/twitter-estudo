# 🐦 Fake Twitter - Projeto de Estudo com Spring Boot (Backend)
Este projeto é uma aplicação fictícia inspirada no Twitter, desenvolvida apenas como backend, com o objetivo de estudar e aplicar conceitos essenciais do Spring Security, JWT, JPA, password encoder e banco de dados PostgreSQL.

🔧 Tecnologias Utilizadas
- Java 17

- Spring Boot

- Spring Security

- JWT (JSON Web Token)

- JPA (Hibernate)

- PostgreSQL

# 🎯 Objetivo
Criar uma aplicação backend simulando funcionalidades básicas de autenticação, registro e publicação de mensagens, reforçando a prática com autenticação segura e controle de acesso usando JWT.

# ⚙️ Funcionalidades Implementadas

- Registro e login de usuários

- Geração de token JWT com autenticação

- Proteção de endpoints com roles

- Publicação e listagem de "tweets"

- Persistência com JPA e PostgreSQL

- Senhas criptografadas com PasswordEncoder

#🔐 Segurança

- JWT é usado para autenticação stateless.

- Spring Security protege os endpoints sensíveis.

- Senhas são armazenadas usando BCryptPasswordEncoder.
