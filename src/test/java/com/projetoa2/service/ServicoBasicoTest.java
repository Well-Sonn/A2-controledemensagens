package com.projetoa2.service;

import com.projetoa2.factory.FabricaUsuario;
import com.projetoa2.model.Mensagem;
import com.projetoa2.model.Usuario;
import com.projetoa2.repository.txt.RepositorioMensagemTxt;
import com.projetoa2.repository.txt.RepositorioUsuarioTxt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ServicoBasicoTest {

    private final Path users = Path.of("target/test-data/users-test.txt");
    private final Path messages = Path.of("target/test-data/messages-test.txt");

    @AfterEach
    public void cleanup() throws IOException {
        Files.deleteIfExists(users);
        Files.deleteIfExists(messages);
    }

    @Test
    public void criarUsuario_e_buscarPorNome_deve_funcionar() {
        RepositorioUsuarioTxt userRepo = new RepositorioUsuarioTxt(users.toString());
        ServicoUsuario su = new ServicoUsuario(userRepo);

        Usuario u = FabricaUsuario.criar("tuser","tsenha");
        su.criarUsuario(u);

        Usuario encontrado = su.buscarPorNome("tuser");
        Assertions.assertNotNull(encontrado);
        Assertions.assertEquals("tuser", encontrado.getNome());
    }

    @Test
    public void enviarMensagem_para_um_deve_persistir() {
        RepositorioUsuarioTxt userRepo = new RepositorioUsuarioTxt(users.toString());
        RepositorioMensagemTxt msgRepo = new RepositorioMensagemTxt(messages.toString());

        ServicoUsuario su = new ServicoUsuario(userRepo);
        ServicoMensagem sm = new ServicoMensagem(msgRepo, userRepo);

        Usuario a = FabricaUsuario.criar("uA","pA");
        Usuario b = FabricaUsuario.criar("uB","pB");
        su.criarUsuario(a);
        su.criarUsuario(b);

        sm.enviarMensagemParaUm(a.getId(), b.getId(), "Oi B");

        List<Mensagem> msgs = sm.obterMensagensParaUsuario(b.getId());
        Assertions.assertEquals(1, msgs.size());
        Assertions.assertEquals("Oi B", msgs.get(0).getConteudo());
    }
}
