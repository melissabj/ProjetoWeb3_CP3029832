# ProjetoWeb3_CP3029832

# 🚀 Sistema de Microsserviços de Autenticação e E-mail

Este projeto é uma arquitetura baseada em microsserviços que integra um ecossistema com gerenciamento de usuários, autenticação via JWT, mensageria assíncrona utilizando **RabbitMQ (CloudAMQP)** e disparo automático de e-mails.

---

## 🏗️ Arquitetura do Projeto

O sistema é dividido em três camadas principais:
* **Frontend / Gateway (Node.js/Express):** Hospedado e editado via **Visual Studio Code**. Gerencia as sessões, interage com o usuário e faz o proxy seguro para os serviços Java.
* **User Service (Spring Boot 3.x / Java 17+):** Gerencia regras de negócio, persistência de usuários no banco de dados e publica eventos de e-mail na fila.
* **Email Service (Spring Boot 3.x / Java 17+):** Consome de forma assíncrona as mensagens da fila do RabbitMQ e processa os envios.

---

## 🛠️ Pré-requisitos Gerais

Antes de iniciar qualquer serviço, certifique-se de ter instalado em sua máquina:
* [Java JDK 17](https://www.oracle.com/java/technologies/downloads/) ou superior.
* [Node.js](https://nodejs.org/) (Versão LTS recomendada).
* Uma conta ativa no CloudAMQP (Instância gratuita do RabbitMQ).
* Banco de dados (MySQL/PostgreSQL) rodando localmente ou em nuvem.

---

## ☁️ Configurando a Hospedagem do RabbitMQ no CloudAMQP

Para que os microsserviços se comuniquem na nuvem sem precisar instalar o RabbitMQ localmente, utilizamos o **CloudAMQP** (instância gerenciada gratuita).

### 1. Criando a Instância Gratuita:
1. Acesse o site do [CloudAMQP](https://www.cloudamqp.com/) e crie uma conta (pode usar o login rápido do GitHub ou Google).
2. No painel principal, clique no botão **"Create New Instance"** (Criar Nova Instância).
3. Dê um nome para a sua instância (ex: `sistema-msa`).
4. No plano (**Plan**), selecione a opção gratuita: **Little Lemur (Tiny, max 1M messages/month, $0/mo)**.
5. Clique em **"Select Region"** e escolha uma região próxima (como `sa-east-1` em São Paulo - AWS, ou alguma gratuita disponível).
6. Clique em **"Review"** e, por fim, em **"Create Instance"**.

### 2. Credenciais Necessárias para o Projeto:
Ao clicar no nome da instância criada, você será redirecionado para a aba **Details**. Você precisará coletar as seguintes informações para colar no `application.properties` do Spring Boot:
* **Host:** O endereço do servidor (ex: `beaver.rmq.cloudamqp.com`).
* **Port:** Use **`5671`** (porta padrão segura para conexões criptografadas `amqps`).
* **User & Virtual Host:** No CloudAMQP, o seu nome de usuário e o seu Virtual Host são **exatamente a mesma string de letras** (ex: `rmllbeok`).
* **Password:** A senha gerada automaticamente para a sua instância.

### 3. Como Acessar o RabbitMQ Manager (Painel Visual):
O RabbitMQ possui uma interface web para monitorar filas, conexões e mensagens em tempo real.
1. Na mesma página **Details** da sua instância no CloudAMQP, procure pelo botão verde chamado **"RabbitMQ Manager"** no canto superior direito.
2. Ao clicar nele, você será logado automaticamente no painel administrativo oficial do RabbitMQ.
3. Na aba **Queues and Streams**, você poderá ver a sua fila (ex: `default.email`) sendo criada em tempo real e monitorar a volumetria de mensagens trafegadas.

---

## 💻 Cenários de Execução do Backend (Escolha o seu ambiente)

### 🔹 Cenário A: Executando TUDO pelo Visual Studio Code
Se você prefere centralizar todo o desenvolvimento (Front e Java) dentro do VS Code:

1. Instale o pacote de extensões: **Extension Pack for Java** da Microsoft.
2. Abra a pasta raiz do projeto no VS Code.
3. Para rodar o `user-service`:
   * Navegue até `user-service/src/main/java/com/msa/userservice/UserServiceApplication.java`.
   * Clique no botão **Run** que aparece logo acima da assinatura do método `main`.
4. Para rodar o `email-service`:
   * Abra uma nova janela ou aba do terminal.
   * Navegue até `email-service/src/main/java/com/msa/emailservice/EmailServiceApplication.java`.
   * Clique em **Run**.

### 🔹 Cenário B: Usando IntelliJ IDEA (Recomendado para Java) + VS Code para o Front
1. Abra o **IntelliJ IDEA**.
2. Clique em **Open** e selecione a pasta do `user-service` (o IntelliJ detectará o arquivo `pom.xml` automaticamente). Repita o processo abrindo o `email-service` em uma nova janela.
3. Aguarde o download das dependências do Maven (barra de progresso inferior).
4. No canto superior direito, clique no botão **Play (Run)** para iniciar cada serviço.
5. *Nota:* Se alterar propriedades, lembre-se de ir no menu superior em `Build` -> `Rebuild Project` para limpar os caches do compilador.

### 🔹 Cenário C: Usando Eclipse / STS + VS Code para o Front
1. Abra o **Eclipse / Spring Tool Suite (STS)**.
2. Vá em `File` -> `Import...` -> `Maven` -> `Existing Maven Projects`.
3. Selecione o diretório dos seus microsserviços e clique em **Finish**.
4. Clique com o botão direito sobre o projeto `user-service` -> `Run As` -> `Spring Boot App` (ou `Java Application`). Repita para o `email-service`.

### 🔹 Cenário D: Usando NetBeans + VS Code para o Front
1. Abra o **NetBeans**.
2. Vá em `File` -> `Open Project`.
3. Selecione a pasta de cada microsserviço (o NetBeans reconhece o ícone do Maven).
4. Clique com o botão direito no projeto desejado e selecione **Run** (ou pressione `F6`).

---

## 🌐 Executando o Frontend (Visual Studio Code)

Independente de qual IDE você escolheu para o Java, o Frontend/Node deve ser executado via terminal do VS Code:

1. Abra a pasta correspondente ao servidor Node/Web no VS Code.
2. Abra o terminal integrado (`Ctrl + '` ou `Cmd + '`).
3. Instale as dependências do ecossistema:
   ```bash
   npm install
   ```
4. Inicie o servidor local:
  ```bash
  node server.js
  ```
5. Acesse o painel/dashboard pelo seu navegador no endereço indicado no terminal (ex: http://localhost:3000).


## 🔒 Configuração de Variáveis de Ambiente (application.properties)

Certifique-se de que o application.properties do email-service esteja configurado para ler objetos inferidos do Jackson modernos na porta segura do CloudAMQP:

`application.properties`

spring.rabbitmq.host=beaver.rmq.cloudamqp.com

spring.rabbitmq.port=5671

spring.rabbitmq.username=seu_usuario

spring.rabbitmq.password=sua_senha

spring.rabbitmq.virtual-host=seu_usuario

spring.rabbitmq.ssl.enabled=true 

# --- Configuração do SMTP (Gmail) ---

# Dica: Use "App Passwords" do Google, nunca sua senha de login pessoal.
# É ESTRITAMENTE necessário usar a Senha de App para que o envio funcione. 

Requisitos: Sua conta deve ter a verificação em duas etapas (2FA) ativada.
[Acesse aqui o tutorial](https://support.google.com/accounts/answer/185833?hl=pt)

`application.properties (email-service)`
...
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_de_app_do_google
...

--

##📋 RESUMO DO FLUXO DE EXECUÇÃO (Passo a Passo)

Para colocar todo o ecossistema para rodar perfeitamente do zero, siga exatamente esta ordem:

[BANCO DE DADOS]: Certifique-se de que o seu banco de dados MySQL/PostgreSQL local está ligado.

[MENSAGERIA]: Crie a instância no CloudAMQP, pegue as credenciais e insira nos arquivos application.properties de ambos os serviços Java.

[BACKEND - USER]: Abra e execute o user-service na sua IDE de preferência. Ele criará as tabelas de usuário e fará o vínculo inicial com o broker.

[BACKEND - EMAIL]: Abra e execute o email-service na sua IDE de preferência. Ele ficará escutando ativamente a fila do CloudAMQP de forma assíncrona.

[FRONTEND]: No VS Code, abra a pasta do frontend, execute npm install seguido de node server.js.

[TESTE]: Abra o navegador em http://localhost:3000, faça um novo cadastro ou atualize o perfil de usuário logado pelo Dashboard. Acompanhe a mensagem trafegando pelo RabbitMQ Manager e chegando com sucesso ao console do email-service!
