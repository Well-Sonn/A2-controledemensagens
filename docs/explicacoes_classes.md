Sistema de Notificações — Explicação de classes e princípios
=============================================================

Este documento reúne a explicação detalhada de cada classe do projeto e a descrição dos padrões de projeto e do princípio SOLID aplicados. Use como roteiro para apresentação.

Índice
------

- Visão geral
- Classe a classe
- Padrões aplicados (Factory, Observer, Dependency Injection)
- Princípio SOLID aplicado (SRP)
- Fluxo de execução (quem chama quem)
- Pontos para demonstração


Visão geral
-----------

Projeto: Sistema de Notificações (CLI) em Java com Maven.

Camadas principais:
- UI: `src/main/java/com/projetoa2/app/Main.java` (CLI)
- Services: `src/main/java/com/projetoa2/service`
- Repositories: `src/main/java/com/projetoa2/repository` e `.../repository/txt`
- Models: `src/main/java/com/projetoa2/model`
- Factories: `src/main/java/com/projetoa2/factory`
- Observer: `src/main/java/com/projetoa2/observer`
- Storage: `src/main/java/com/projetoa2/storage/ArmazenamentoArquivo`


Classe a classe (detalhado)
---------------------------

1) `Main` — `src/main/java/com/projetoa2/app/Main.java`
- Responsabilidade: ponto de entrada e UI CLI; wiring (injeção manual) de repositórios e serviços; menus e fluxos (login/admin/usuário).
- Principais ações: cria `RepositorioUsuarioTxt` e `RepositorioMensagemTxt`; instancia `ServicoUsuario` e `ServicoMensagem`; registra observadores como `NotificadorConsole`; garante criação do admin padrão; implementa `loginFlow()`, `adminMenu()` e `userMenu()` com UX de leitura e envio de mensagens.
- Observações: UI é responsável por interação e por aplicar algumas regras de UX (ex.: impedir resposta a mensagens do admin).


2) `TestRunner` — `src/main/java/com/projetoa2/app/TestRunner.java`
- Responsabilidade: execução programática para demonstrar fluxos sem entrada manual (cria usuários, envia broadcast, marca mensagens como lidas).
- Uso: útil para apresentação automatizada ou execução em ambiente sem stdin.


3) `Usuario` — `src/main/java/com/projetoa2/model/Usuario.java`
- Responsabilidade: modelo de usuário com `id`, `nome`, `senha` e `administrador`.
- Serialização: `serializar()` e `desserializar(String)` usam formato `id|nome|senha|adminFlag`.
- Observação: senhas em texto plano (melhoria recomendada: hash com BCrypt).


4) `Mensagem` — `src/main/java/com/projetoa2/model/Mensagem.java`
- Responsabilidade: modelo de mensagem com `id`, `idRemetente`, `idDestinatario`, `conteudo`, `momento` e `lida`.
- Serialização: `id|idRemetente|idDestinatario|conteudo|momento|lidaFlag` (compatível com linhas antigas sem a flag).
- Importância: flag `lida` permite agrupar/contar mensagens não lidas e marcar como lidas após exibição.


5) `RepositorioUsuarioTxt` — `src/main/java/com/projetoa2/repository/txt/RepositorioUsuarioTxt.java`
- Responsabilidade: persistir `Usuario` em `data/users.txt` (ler tudo, desserializar, modificar e regravar).
- Operações: `buscarTodos()`, `buscarPorId()`, `buscarPorNome()`, `salvar(Usuario)`, `remover(int)`.
- Trade-off: simplicidade e legibilidade; regrava todo o arquivo ao salvar (ok para escala pequena).


6) `RepositorioMensagemTxt` — `src/main/java/com/projetoa2/repository/txt/RepositorioMensagemTxt.java`
- Responsabilidade: persistir `Mensagem` em `data/messages.txt`.
- Operações: `buscarTodos()`, `buscarPorId()`, `buscarPorDestinatario()`, `salvar(Mensagem)`, `remover(int)`.
- Observação: preserva a flag `lida` ao salvar mensagens atualizadas.


7) `ServicoUsuario` — `src/main/java/com/projetoa2/service/ServicoUsuario.java`
- Responsabilidade: regras de negócio para usuários (criação, autenticação, remoção com restrições).
- Comportamento: `criarUsuario`, `autenticar`, `removerUsuario` (impede remover admin), `obterTodos`, `buscarPorId`.


8) `ServicoMensagem` — `src/main/java/com/projetoa2/service/ServicoMensagem.java`
- Responsabilidade: regras de negócio para envio, consulta e marcação de mensagens.
- Métodos-chave: `enviarMensagemParaUm`, `enviarEmMassa`, `obterMensagensParaUsuario`, `obterMensagensNaoLidasParaUsuario`, `marcarComoLida`, `contarNaoLidasPorRemetente`, `obterMensagensNaoLidasPorRemetente`, `registrarObservador`.
- Observadores: notifica implementações de `ObservadorMensagem` quando mensagem é enviada.


9) `FabricaUsuario` — `src/main/java/com/projetoa2/factory/FabricaUsuario.java`
- Responsabilidade: criar `Usuario` com ID único.
- Implementação: usa `AtomicInteger` inicializado a partir de `data/users.txt` para evitar colisões de ID após reinícios; `criar(nome, senha)` retorna `Usuario` com `administrador=true` quando nome é "admin".


10) `FabricaMensagem` — `src/main/java/com/projetoa2/factory/FabricaMensagem.java`
- Responsabilidade: criar `Mensagem` com ID e timestamp; define `lida=false` por padrão.
- Observação: contador de mensagens reinicia em cada execução (poderíamos inicializá-lo lendo `data/messages.txt` similarmente).


11) `ArmazenamentoArquivo` — `src/main/java/com/projetoa2/storage/ArmazenamentoArquivo.java`
- Responsabilidade: singleton que centraliza leitura/escrita/anexação de arquivos.
- Métodos: `lerTudo(caminho)`, `escreverTudo(caminho, linhas)`, `anexar(caminho, linha)`; garante criação de diretórios e arquivos quando inexistentes.


12) `ObservadorMensagem` e `NotificadorConsole` — `src/main/java/com/projetoa2/observer`
- `ObservadorMensagem`: interface com `void aoEnviarMensagem(Mensagem m)`.
- `NotificadorConsole`: implementação que imprime uma notificação no console quando acionada.
- Uso: registra-se no `ServicoMensagem` para receber eventos de envio.


Padrões aplicados (detalhe)
--------------------------

1) Factory
- Local: `src/main/java/com/projetoa2/factory` (`FabricaUsuario`, `FabricaMensagem`).
- Papel: centralizar criação de objetos e geração de IDs/timestamps.
- Benefício: encapsula política de criação; facilita alteração (ex.: IDs vindos do BD) sem mexer nos serviços.

2) Observer
- Local: `src/main/java/com/projetoa2/observer` e registro em `ServicoMensagem`.
- Papel: permitir que ações ocorram quando mensagens são enviadas (log, e-mail, etc.) sem acoplar diretamente o serviço a essas ações.
- Benefício: extensibilidade e baixo acoplamento.

3) Dependency Injection (injeção manual via construtor)
- Local: `Main` (wiring) e construtores em `ServicoUsuario`/`ServicoMensagem`.
- Papel: fornecer dependências (repositórios) aos serviços externamente.
- Benefício: facilita troca de implementações e testes unitários.


Princípio SOLID aplicado
------------------------

SRP (Single Responsibility Principle)
- Aplicação: cada pacote/classe tem responsabilidade única: modelos só representam dados; repositórios só persistem; serviços só contêm regras de negócio; UI só lida com interação.
- Por que: reduz acoplamento, facilita manutenção, torna o código mais legível e testável.


