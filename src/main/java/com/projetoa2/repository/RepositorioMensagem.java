package com.projetoa2.repository;

import com.projetoa2.model.Mensagem;

import java.util.List;

public interface RepositorioMensagem extends Repositorio<Mensagem> {
    List<Mensagem> buscarPorDestinatario(int idDestinatario);
}
