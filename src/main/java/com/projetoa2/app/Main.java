package com.projetoa2.app;

import com.projetoa2.factory.FabricaUsuario;
import com.projetoa2.factory.FabricaMensagem;
import com.projetoa2.model.Usuario;
import com.projetoa2.model.Mensagem;
import com.projetoa2.repository.RepositorioUsuario;
import com.projetoa2.repository.RepositorioMensagem;
import com.projetoa2.repository.txt.RepositorioUsuarioTxt;
import com.projetoa2.repository.txt.RepositorioMensagemTxt;
import com.projetoa2.service.ServicoUsuario;
import com.projetoa2.service.ServicoMensagem;
import com.projetoa2.observer.NotificadorConsole;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        RepositorioUsuario userRepo = new RepositorioUsuarioTxt("data/users.txt");
        RepositorioMensagem messageRepo = new RepositorioMensagemTxt("data/messages.txt");

        ServicoUsuario userService = new ServicoUsuario(userRepo);
        ServicoMensagem messageService = new ServicoMensagem(messageRepo, userRepo);

        messageService.registrarObservador(new NotificadorConsole());

        // garantir que admin exista
        if (userService.buscarPorNome("admin") == null) {
            Usuario admin = FabricaUsuario.criar("admin", "admin");
            userService.criarUsuario(admin);
        }

        while (true) {
            printHeader("Sistema de Notificações");
            printBox("Menu Principal", List.of("1) Login", "2) Sair"));
            System.out.print("Escolha: ");
            String opt = scanner.nextLine().trim();
            if (opt.equals("1")) {
                loginFlow(userService, messageService);
            } else if (opt.equals("2")) {
                System.out.println("Saindo...");
                break;
            }
        }
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("========================================");
        System.out.println("  " + title);
        System.out.println("========================================");
    }

    private static void printBox(String title, List<String> lines) {
        int width = title.length();
        for (String l : lines) if (l.length() > width) width = l.length();
        width += 4; 
        String border = "+" + "-".repeat(Math.max(0, width)) + "+";
        System.out.println(border);
        String centered = "| " + padBoth(title, width - 2) + " |";
        System.out.println(centered);
        System.out.println("|" + " ".repeat(width) + "|");
        for (String l : lines) {
            System.out.println("| " + padRight(l, width - 2) + " |");
        }
        System.out.println(border);
    }

    private static String padRight(String s, int n) {
        if (s.length() >= n) return s;
        return s + " ".repeat(n - s.length());
    }

    private static String padBoth(String s, int n) {
        if (s.length() >= n) return s;
        int total = n - s.length();
        int left = total/2;
        int right = total - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }

    private static void loginFlow(ServicoUsuario userService, ServicoMensagem messageService) {
        System.out.print("Usuário: ");
        String username = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String password = scanner.nextLine().trim();

        Usuario usuario = userService.autenticar(username, password);
        if (usuario == null) {
            System.out.println("Credenciais inválidas.");
            return;
        }

        if (usuario.ehAdministrador()) {
            adminMenu(userService, messageService);
        } else {
            userMenu(usuario, userService, messageService);
        }
    }

    private static void adminMenu(ServicoUsuario userService, ServicoMensagem messageService) {
        while (true) {
            System.out.println("--- Menu Administrador ---");
            System.out.println("1) Cadastrar usuário");
            System.out.println("2) Listar usuários");
            System.out.println("3) Excluir usuário");
            System.out.println("4) Enviar mensagem para um usuário");
            System.out.println("5) Enviar mensagem em massa");
            System.out.println("6) Voltar");
            System.out.print("Escolha: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1":
                    printBox("Cadastrar Usuário", List.of("Preencha os dados abaixo:"));
                    System.out.print("Novo username: ");
                    String u = scanner.nextLine().trim();
                    System.out.print("Senha: ");
                    String p = scanner.nextLine().trim();
                    Usuario novoUsuario = FabricaUsuario.criar(u, p);
                    userService.criarUsuario(novoUsuario);
                    System.out.println("\nUsuário criado com sucesso.\n");
                    break;
                case "2":
                    List<String> linhas = new ArrayList<>();
                    for (Usuario usr : userService.obterTodos()) {
                        linhas.add(usr.getId() + " | " + usr.getNome() + (usr.ehAdministrador() ? " (admin)" : ""));
                    }
                    printBox("Lista de Usuários", linhas);
                    break;
                case "3":
                    System.out.print("ID do usuário que seja excluir: ");
                    String idStr = scanner.nextLine().trim();
                    try {
                        int id = Integer.parseInt(idStr);
                        Usuario alvo = userService.buscarPorId(id);
                        if (alvo == null) {
                            System.out.println("Usuário não encontrado.");
                        } else if (alvo.ehAdministrador()) {
                            System.out.println("Não é permitido remover o administrador.");
                        } else {
                            boolean ok = userService.removerUsuario(id);
                            if (ok) System.out.println("Usuário removido."); else System.out.println("Falha ao remover usuário.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ID inválido.");
                    }
                    break;
                case "4":
                    printBox("Enviar Mensagem (Admin)", List.of("Informe o ID do destinatário e o conteúdo"));
                    System.out.print("ID do destinatário: ");
                    String r = scanner.nextLine().trim();
                    System.out.print("Conteúdo: ");
                    String content = scanner.nextLine().trim();
                    try {
                        int rid = Integer.parseInt(r);
                        messageService.enviarMensagemParaUm(1, rid, content); // admin id = 1 por fábrica
                        System.out.println("\nMensagem enviada com sucesso.\n");
                    } catch (NumberFormatException e) {
                        System.out.println("ID inválido.");
                    }
                    break;
                case "5":
                    printBox("(Admin)", List.of("Digite a mensagem que será enviada a todos os usuários:"));
                    System.out.print("Conteúdo da massa: ");
                    String bc = scanner.nextLine().trim();
                    messageService.enviarEmMassa(1, bc);
                    System.out.println("\nMassa enviada com sucesso.\n");
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void userMenu(Usuario usuario, ServicoUsuario userService, ServicoMensagem messageService) {
        while (true) {
            System.out.println("--- Menu Usuário (" + usuario.getNome() + ") ---");
            System.out.println("1) Enviar nova mensagem");
            System.out.println("2) Ler mensagens");
            System.out.println("3) Sair");
            System.out.print("Escolha: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1":
                    printBox("Enviar Mensagem", List.of("Preencha os campos abaixo:"));
                    System.out.print("ID do destinatário: ");
                    String r = scanner.nextLine().trim();
                    System.out.print("Conteúdo: ");
                    String content = scanner.nextLine().trim();
                    try {
                        int rid = Integer.parseInt(r);
                        messageService.enviarMensagemParaUm(usuario.getId(), rid, content);
                        System.out.println("\nMensagem enviada com sucesso.\n");
                    } catch (NumberFormatException e) {
                        System.out.println("ID inválido.");
                    }
                    break;
                case "2":
                    java.util.Map<Integer,Integer> contagens = messageService.contarNaoLidasPorRemetente(usuario.getId());
                    if (contagens.isEmpty()) {
                        System.out.println("Você não tem novas mensagens.");
                        break;
                    }

                    java.util.List<Integer> remetentes = new java.util.ArrayList<>(contagens.keySet());
                    remetentes.sort((a,b) -> {
                        String na = userService.buscarPorId(a)!=null?userService.buscarPorId(a).getNome():"";
                        String nb = userService.buscarPorId(b)!=null?userService.buscarPorId(b).getNome():"";
                        return na.compareToIgnoreCase(nb);
                    });

                    while (true) {
                        List<String> remLines = new ArrayList<>();
                        for (int i=0;i<remetentes.size();i++) {
                            int rid = remetentes.get(i);
                            String nomeRem = userService.buscarPorId(rid)!=null?userService.buscarPorId(rid).getNome():"(desconhecido)";
                            remLines.add((i+1) + ") " + nomeRem + " (id=" + rid + ") : " + contagens.get(rid) + " mensagens não lidas");
                        }
                        printBox("Suas Mensagens", remLines);
                        System.out.print("Escolha o remetente (número) para abrir ou Enter para voltar: ");
                        String escolha = scanner.nextLine().trim();
                        if (escolha.isEmpty()) break;
                        try {
                            int idx = Integer.parseInt(escolha) - 1;
                            if (idx < 0 || idx >= remetentes.size()) { System.out.println("Escolha inválida."); continue; }
                            int remetenteEscolhido = remetentes.get(idx);
                            String nomeRem = userService.buscarPorId(remetenteEscolhido)!=null?userService.buscarPorId(remetenteEscolhido).getNome():"(desconhecido)";

                            java.util.List<Mensagem> msgs = messageService.obterMensagensNaoLidasPorRemetente(usuario.getId(), remetenteEscolhido);
                            if (msgs.isEmpty()) { System.out.println("Nenhuma mensagem nova desse remetente."); continue; }

                            System.out.println("--- Mensagens de " + nomeRem + " ---");
                            for (Mensagem m : msgs) {
                                System.out.println("- [" + m.getId() + "] " + m.getConteudo());
                            }
                            // marcar como lidas
                            for (Mensagem m : msgs) messageService.marcarComoLida(m.getId());

                            Usuario remetenteObj = userService.buscarPorId(remetenteEscolhido);
                            boolean remetenteEhAdmin = remetenteObj!=null && remetenteObj.ehAdministrador();
                            if (remetenteEhAdmin) {
                                System.out.println("Mensagens do admin lidas. Não é possível responder.");
                                contagens = messageService.contarNaoLidasPorRemetente(usuario.getId());
                                remetentes = new java.util.ArrayList<>(contagens.keySet());
                                remetentes.sort((a,b) -> {
                                    String na = userService.buscarPorId(a)!=null?userService.buscarPorId(a).getNome():"";
                                    String nb = userService.buscarPorId(b)!=null?userService.buscarPorId(b).getNome():"";
                                    return na.compareToIgnoreCase(nb);
                                });
                                continue;
                            }

                            System.out.print("Deseja responder alguma mensagem? (ID ou Enter): ");
                            String choice = scanner.nextLine().trim();
                            if (!choice.isEmpty()) {
                                try {
                                    int mid = Integer.parseInt(choice);
                                    Mensagem original = messageService.buscarPorId(mid);
                                    if (original != null && original.getIdRemetente() == remetenteEscolhido) {
                                        System.out.print("Resposta: ");
                                        String resp = scanner.nextLine().trim();
                                        messageService.enviarMensagemParaUm(usuario.getId(), original.getIdRemetente(), "RE: " + resp);
                                        System.out.println("Resposta enviada.");
                                    } else {
                                        System.out.println("Mensagem inválida ou não pertence a esse remetente.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("ID inválido.");
                                }
                            }

                            contagens = messageService.contarNaoLidasPorRemetente(usuario.getId());
                            remetentes = new java.util.ArrayList<>(contagens.keySet());
                            remetentes.sort((a,b) -> {
                                String na = userService.buscarPorId(a)!=null?userService.buscarPorId(a).getNome():"";
                                String nb = userService.buscarPorId(b)!=null?userService.buscarPorId(b).getNome():"";
                                return na.compareToIgnoreCase(nb);
                            });
                            if (remetentes.isEmpty()) { System.out.println("Nenhuma nova mensagem restante."); break; }
                        } catch (NumberFormatException e) {
                            System.out.println("Escolha inválida.");
                        }
                    }
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }
}
