package com.projetoa2.app;

import com.projetoa2.factory.FabricaUsuario;
import com.projetoa2.model.Mensagem;
import com.projetoa2.model.Usuario;
import com.projetoa2.repository.RepositorioMensagem;
import com.projetoa2.repository.RepositorioUsuario;
import com.projetoa2.repository.txt.RepositorioMensagemTxt;
import com.projetoa2.repository.txt.RepositorioUsuarioTxt;
import com.projetoa2.service.ServicoMensagem;
import com.projetoa2.service.ServicoUsuario;

import java.util.List;

/**
 * Test runner programático para validar fluxos principais sem interação humana.
 */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("[TestRunner] Iniciando testes programáticos...");

        RepositorioUsuario userRepo = new RepositorioUsuarioTxt("data/users.txt");
        RepositorioMensagem msgRepo = new RepositorioMensagemTxt("data/messages.txt");

        ServicoUsuario userService = new ServicoUsuario(userRepo);
        ServicoMensagem msgService = new ServicoMensagem(msgRepo, userRepo);

        // garantir admin
        if (userService.buscarPorNome("admin") == null) {
            Usuario admin = FabricaUsuario.criar("admin", "admin");
            userService.criarUsuario(admin);
            System.out.println("[TestRunner] Admin criado.");
        } else {
            System.out.println("[TestRunner] Admin já existe.");
        }

        // criar usuário de teste
        String nomeUsuario = "usuario1";
        if (userService.buscarPorNome(nomeUsuario) == null) {
            Usuario u = FabricaUsuario.criar(nomeUsuario, "senha1");
            userService.criarUsuario(u);
            System.out.println("[TestRunner] Usuário '" + nomeUsuario + "' criado.");
        } else {
            System.out.println("[TestRunner] Usuário '" + nomeUsuario + "' já existe.");
        }

        // inspecionar usuários antes do broadcast
        System.out.println("[TestRunner] Lista de usuários: ");
        for (Usuario uu : userService.obterTodos()) {
            System.out.println(" - id=" + uu.getId() + " nome=" + uu.getNome() + " admin=" + uu.ehAdministrador());
        }

        // enviar broadcast como admin (tentar usar id do admin encontrado)
        Usuario admin = userService.buscarPorNome("admin");
        int adminId = admin != null ? admin.getId() : 1;
        msgService.registrarObservador(m -> System.out.println("[Observador] Mensagem enviada: " + m.getConteudo()));
        msgService.enviarEmMassa(adminId, "Olá a todos");
        System.out.println("[TestRunner] Broadcast enviado (adminId=" + adminId + ").");

        // listar mensagens do usuario1
        Usuario u1 = userService.buscarPorNome(nomeUsuario);
        if (u1 != null) {
            List<Mensagem> msgs = msgService.obterMensagensParaUsuario(u1.getId());
            System.out.println("[TestRunner] Usuario '" + nomeUsuario + "' tem " + msgs.size() + " mensagens (total).");
            for (Mensagem m : msgs) {
                System.out.println(" - [" + m.getId() + "] De: " + m.getIdRemetente() + " | " + m.getConteudo() + " | lida=" + m.isLida());
            }

            // verificar mensagens nao lidas e marcar como lidas
            List<Mensagem> naoLidas = msgService.obterMensagensNaoLidasParaUsuario(u1.getId());
            System.out.println("[TestRunner] Mensagens não lidas antes: " + naoLidas.size());
            for (Mensagem m : naoLidas) {
                msgService.marcarComoLida(m.getId());
            }
            List<Mensagem> naoLidasDepois = msgService.obterMensagensNaoLidasParaUsuario(u1.getId());
            System.out.println("[TestRunner] Mensagens não lidas depois de marcar: " + naoLidasDepois.size());
        } else {
            System.out.println("[TestRunner] Não encontrou usuário de teste.");
        }

        System.out.println("[TestRunner] Fim.");
    }
}
