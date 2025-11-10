# Sistema de Notificações (CLI)

Este projeto é um sistema de notificações simples em linha de comando (CLI) escrito em Java com Maven. Ele permite que um administrador gerencie usuários e envie mensagens (individual e broadcast) e que usuários comuns recebam, leiam e respondam mensagens — com persistência em arquivos TXT.

## Objetivo

Fornecer um sistema didático e testável que demonstra aplicação de princípios de arquitetura e padrões (SRP, Dependency Injection, Factory, Observer, Singleton) em um projeto Java pequeno. É adequado como trabalho acadêmico, prova de conceito ou base para extensão.

## Funcionalidades

- Autenticação simples por nome e senha.
- Usuário administrador (criado automaticamente quando não existir) com permissões para:
   - Cadastrar usuários
   - Listar usuários
   - Excluir usuários (admin não pode ser removido)
   - Enviar mensagem para um usuário
   - Enviar mensagem em massa (broadcast)
- Usuários comuns podem:
   - Enviar mensagem para outro usuário
   - Ler mensagens agrupadas por remetente (apenas mensagens não lidas são exibidas como novas)
   - Responder mensagens de outros usuários (não é permitido responder mensagens enviadas pelo admin)

## Arquitetura e padrões aplicados

- SRP (Single Responsibility): modelos (`Usuario`, `Mensagem`), repositórios, serviços e UI têm responsabilidades separadas.
- Dependency Injection: serviços recebem repositórios via construtor (injeção manual no `Main`).
- Factory: `FabricaUsuario` e `FabricaMensagem` centralizam criação e geração de IDs.
- Observer: `ObservadorMensagem` (ex.: `NotificadorConsole`) recebe notificações de envio de mensagem.
- Singleton: `ArmazenamentoArquivo` centraliza leitura/gravação de arquivos TXT.

## Onde e como cada padrão foi aplicado

A seguir há uma descrição mais prática de onde (arquivos/pacotes) e como cada padrão foi aplicado no projeto.

- SRP (Single Responsibility)
   - Onde: `src/main/java/com/projetoa2/model` (`Usuario`, `Mensagem`), `src/main/java/com/projetoa2/service` (serviços), `src/main/java/com/projetoa2/repository` (contratos) e `src/main/java/com/projetoa2/app` (`Main` / UI).
   - Como: cada camada tem uma responsabilidade clara — modelos apenas representam dados; repositórios lidam com persistência; serviços contêm regras de negócio; `Main` cuida da interação com o usuário e da composição das dependências.

- Dependency Injection (injeção manual)
   - Onde: `src/main/java/com/projetoa2/app/Main.java` e construtores em `ServicoUsuario` e `ServicoMensagem`.
   - Como: `Main` cria implementações concretas (ex.: `RepositorioUsuarioTxt`, `RepositorioMensagemTxt`) e injeta essas instâncias nos serviços via construtor. Isso permite trocar implementações (por exemplo, TXT → JSON) sem alterar a lógica dos serviços.

- Factory
   - Onde: `src/main/java/com/projetoa2/factory/FabricaUsuario.java` e `FabricaMensagem.java`.
   - Como: centralizam a criação de entidades e a geração de IDs (leem o estado atual dos arquivos para evitar colisões). Isso encapsula a lógica de criação e mantém serviços/repositórios livres dessa responsabilidade.

- Observer
   - Onde: `src/main/java/com/projetoa2/observer/ObservadorMensagem.java` e `NotificadorConsole.java`.
   - Como: o serviço de mensagem notifica observadores quando uma mensagem é enviada; o `NotificadorConsole` imprime notificações no console. O padrão facilita adicionar outros observadores (ex.: envio por e-mail) sem alterar o fluxo de envio.

- Singleton
   - Onde: `src/main/java/com/projetoa2/storage/ArmazenamentoArquivo.java`.
   - Como: fornece pontos únicos para leitura/escrita/append em arquivos, evitando replicação de código de I/O e possibilitando controle centralizado (criação de diretórios/arquivos quando necessário).

- Repository (padrão de repositório)
   - Onde: interfaces em `src/main/java/com/projetoa2/repository` e implementações TXT em `src/main/java/com/projetoa2/repository/txt` (`RepositorioUsuarioTxt`, `RepositorioMensagemTxt`).
   - Como: abstraem persistência por trás de contratos. Serviços interagem com as interfaces de repositório, não com detalhes de arquivo.

Essas escolhas visam facilitar manutenção e evolução: por exemplo, para trocar a persistência para JSON basta implementar as interfaces de repositório e injetá-las no `Main`, sem tocar a lógica de negócio.

## Persistência (formatos de arquivo)

- `data/users.txt`: cada linha representa um usuário no formato:

   id|nome|senha|adminFlag

   Exemplo: `1|admin|admin|1`

- `data/messages.txt`: cada linha representa uma mensagem no formato:

   id|idRemetente|idDestinatario|conteudo|momentoEpochMillis|lidaFlag

   - `lidaFlag` é `1` (lida) ou `0` (não lida). O sistema é compatível com linhas antigas sem a flag (assume não lida).

   Exemplo: `1|1|2|Olá a todos|1610000000000|0`

> Observação: os arquivos ficam na pasta `data/` do projeto. Mantemos um formato simples para facilitar inspeção e edição manual.

## Como executar

Abra um terminal e execute os comandos a partir da raiz do projeto (onde está o `pom.xml`). Substitua `mvn` pelo gerenciador de sua preferência, mantendo a configuração Maven.

PowerShell (Windows) — execute na raiz do projeto:

```powershell
mvn compile
mvn exec:java
```

Terminal (Unix/macOS):

```bash
mvn compile
mvn exec:java
```

Observações:
- O `mainClass` está definido no `pom.xml`, então `mvn exec:java` iniciará o CLI.
- Usuário administrador padrão: `admin` / `admin` (criado automaticamente na primeira execução, se ausente).

## Testes

Os testes unitários usam JUnit. Para executar:

```bash
mvn test
```

## Notas de uso e comportamento importantes

- Mensagens não lidas são exibidas agrupadas por remetente; ao abrir as mensagens de um remetente, elas são marcadas como lidas.
- Usuários não podem responder mensagens do admin — o admin é apenas gerenciador/emitente.
- IDs são gerados pela fábrica a partir do conteúdo atual de `data/users.txt` para evitar colisões entre execuções.

## Possíveis melhorias

- Substituir persistência TXT por JSON (Jackson) para maior robustez.
- Hash de senhas (BCrypt) para segurança.
- Melhorar UI com cores ANSI e suporte a scripts de batch para testes automatizados.

## Estrutura de código (onde procurar)

- `src/main/java/com/projetoa2/model` — entidades (`Usuario`, `Mensagem`).
- `src/main/java/com/projetoa2/repository` — contratos de repositório.
- `src/main/java/com/projetoa2/repository/txt` — implementações TXT.
- `src/main/java/com/projetoa2/service` — lógica de negócio (Serviço de Usuário e Serviço de Mensagem).
- `src/main/java/com/projetoa2/factory` — fábricas para criação de entidades.
- `src/main/java/com/projetoa2/observer` — observadores de mensagens (ex.: console).
- `src/main/java/com/projetoa2/app/Main.java` — ponto de entrada e UI CLI.

---

