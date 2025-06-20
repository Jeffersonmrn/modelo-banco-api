# API Bancária RESTful com Java e Spring Boot 💸

Este projeto é uma API RESTful desenvolvida com **Java 17 + Spring Boot**, que simula um sistema bancário simples com funcionalidades reais de cadastro, autenticação, movimentação financeira e controle de acesso.

Além das transferências tradicionais (TED e DOC), implementa a simulação de **PIX** via microserviço, reforçando o uso prático da API em ambientes financeiros modernos. O repositório está aqui mesmo nesse perfil do Github com nome de (pix-simulado-do-modelo-banco). Rode ele na porta 8081, enquanto este projeto aqui, modelo-banco roda na porta 8080.

---

## 🚀 Funcionalidades principais

- **Autenticação segura com JWT** (validação e controle de sessão)
- **Cadastro de usuários** com validação de CPF único e dados obrigatórios  
- **Criação automática de contas** vinculadas aos usuários no cadastro 
- Simulação completa de **envio e recebimento de PIX** via microserviço dedicado  
- Listagem detalhada de todas as transferências realizadas e recebidas por usuário  
- Consulta individualizada de transferências e extratos bancários  
- **Controle de acesso baseado em perfis** (admin e usuário comum), garantindo segurança e privacidade  
- Tratamento padronizado de exceções para autenticação, autorização, validação e recursos não encontrados  

---

## 🧱 Arquitetura e design

A aplicação segue o padrão MVC em camadas para facilitar manutenção e escalabilidade, separando:

- Controladores REST para endpoints HTTP  
- Serviços com regras de negócio e validação  
- Repositórios para persistência em banco de dados H2 (memória)  
- Modelos de domínio bem definidos e DTOs para transferência de dados  

O microserviço dedicado ao PIX simula um cenário simples mas realista de movimentação instantânea entre contas, conectado ao sistema principal via chamadas REST.

---

## 📦 Tecnologias utilizadas

- Java 17  
- Spring Boot (Web, Security, Data JPA)  
- Auth0 JWT para autenticação  
- Banco de dados H2 (em memória, para testes)  
- Maven para gerenciamento de dependências   

---

## 🔐 Segurança

- Tokens JWT com validade de 2 horas, gerados no login  
- Usuários comuns acessam somente suas contas e movimentações  
- Administradores têm acesso completo a todas as operações e dados  
- Controle baseado em CPF e papel (Role) do usuário  
- Criação automática do usuário administrador na inicialização da aplicação  

---

## 📡 Endpoints principais

### Autenticação
| Método | Endpoint          | Descrição                 |
|--------|-------------------|---------------------------|
| POST   | /auth/register    | Cadastra novo usuário      |
| POST   | /auth/login       | Gera token JWT             |

### Usuários
| Método | Endpoint          | Descrição                    |
|--------|-------------------|------------------------------|
| GET    | /usuarios/{id}    | Retorna dados do usuário     |
| GET    | /usuarios         | Lista todos usuários (Admin) |
| PUT    | /usuarios/{id}    | Atualiza dados do usuário    |

### Contas e movimentações
| Método | Endpoint                | Descrição                                 |
|--------|-------------------------|-------------------------------------------|
| POST   | /api/contas/depositar   | Depositar valor na conta                   |
| POST   | /api/contas/sacar       | Sacar valor da conta                       |
| GET    | /api/contas             | Lista todas contas                         |
| GET    | /usuarios/{id}/extrato  | Lista extrato bancário do usuário          |

### Transferências
| Método | Endpoint                  | Descrição                                 |
|--------|---------------------------|-------------------------------------------|
| POST   | /api/transferencias       | Envia transferência PIX, TED ou DOC       |
| GET    | /api/transferencias       | Lista transferências do usuário (Admin vê todas) |
| GET    | /api/transferencias/{id}  | Busca transferência específica (autorização) |

---

## 📝 Requisições JSON

**POST Registrar Usuário**  
```json
{
  "nome": "Jefferson",
  "email": "jefferson@email.com",
  "senha": "senha123",
  "cpf": "39053344705"
}
```
**POST Login Admin**  
```json
{
  "email": "admin@bank.com",
  "senha": "123456"
}
```
**POST Login Usuário**  
```json
{
  "agencia": "0002",
  "numeroConta": "50280155-0",
  "senha": "senha123"
}
```
**POST Depositar**  
```json
{
  "contaId": 2,
  "valor": 500.00
}
```
**POST Sacar**  
```json
{
  "contaId": 2,
  "valor": 100.00
}
```
**POST Transferência via PIX**  
```json
{
  "origemId": 2,
  "destinoId": 1,
  "valor": 5.50,
  "tipo": "PIX",
  "chavePix": "joao@email.com"
}
```
**POST Transferência via DOC/TED**  
```json
{
  "origemId": 2,
  "destinoId": 1,
  "valor": 3.00,
  "tipo": "DOC"
}
```
**PUT Atualizar Usuário (email e senha)**  
```json
{
  "email": "novoemail@exemplo.com",
  "senha": "novasenha123"
}
```

A API estará disponível em: http://localhost:8080

**Documentação do projeto: http://localhost:8080/swagger-ui/index.html**

📄
Distribuído sob a licença MIT.

✍️ Autor
**Jefferson Moreno**
