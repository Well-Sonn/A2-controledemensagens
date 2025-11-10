package com.projetoa2.service;

import com.projetoa2.factory.FabricaMensagem;
import com.projetoa2.model.Mensagem;
import com.projetoa2.model.Usuario;
import com.projetoa2.observer.ObservadorMensagem;
import com.projetoa2.repository.RepositorioMensagem;
import com.projetoa2.repository.RepositorioUsuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ServicoMensagem {
    private final RepositorioMensagem repositorioMensagem;
    private final RepositorioUsuario repositorioUsuario;
    private final List<ObservadorMensagem> observadores = new ArrayList<>();

    public ServicoMensagem(RepositorioMensagem repositorioMensagem, RepositorioUsuario repositorioUsuario) {
        this.repositorioMensagem = repositorioMensagem;
        this.repositorioUsuario = repositorioUsuario;
    }

    public void registrarObservador(ObservadorMensagem o) { observadores.add(o); }

    public void enviarMensagemParaUm(int idRemetente, int idDestinatario, String conteudo) {
        Usuario destinatario = repositorioUsuario.buscarPorId(idDestinatario);
        if (destinatario == null) return;
        Mensagem m = FabricaMensagem.criar(idRemetente, idDestinatario, conteudo);
        repositorioMensagem.salvar(m);
        notificarObservadores(m);
    }

    public void enviarEmMassa(int idRemetente, String conteudo) {
        List<Usuario> todos = repositorioUsuario.buscarTodos();
        for (Usuario u : todos) {
            if (u.getId() == idRemetente) continue;
            Mensagem m = FabricaMensagem.criar(idRemetente, u.getId(), conteudo);
            repositorioMensagem.salvar(m);
            notificarObservadores(m);
        }
    }

    public List<Mensagem> obterMensagensParaUsuario(int idUsuario) {
        return repositorioMensagem.buscarPorDestinatario(idUsuario);
    }

    public List<Mensagem> obterMensagensNaoLidasParaUsuario(int idUsuario) {
        List<Mensagem> todas = repositorioMensagem.buscarPorDestinatario(idUsuario);
        List<Mensagem> out = new ArrayList<>();
        for (Mensagem m : todas) if (!m.isLida()) out.add(m);
        return out;
    }

    public void marcarComoLida(int idMensagem) {
        Mensagem m = repositorioMensagem.buscarPorId(idMensagem);
        if (m == null) return;
        if (!m.isLida()) {
            m.setLida(true);
            repositorioMensagem.salvar(m);
        }
    }

    public Mensagem buscarPorId(int id) { return repositorioMensagem.buscarPorId(id); }

    private void notificarObservadores(Mensagem m) {
        for (ObservadorMensagem o : observadores) o.aoEnviarMensagem(m);
    }

    /**
     * Conta mensagens não lidas agrupadas por id do remetente para um destinatário.
     */
    public Map<Integer, Integer> contarNaoLidasPorRemetente(int idUsuario) {
        List<Mensagem> naoLidas = obterMensagensNaoLidasParaUsuario(idUsuario);
        Map<Integer, Integer> map = new HashMap<>();
        for (Mensagem m : naoLidas) {
            map.merge(m.getIdRemetente(), 1, Integer::sum);
        }
        return map;
    }

    /**
     * Retorna mensagens não lidas para um destinatário vindas de um remetente específico.
     */
    public List<Mensagem> obterMensagensNaoLidasPorRemetente(int idDestinatario, int idRemetente) {
        return obterMensagensNaoLidasParaUsuario(idDestinatario).stream()
                .filter(m -> m.getIdRemetente() == idRemetente)
                .collect(Collectors.toList());
    }
}
