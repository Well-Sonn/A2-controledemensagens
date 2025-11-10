package com.projetoa2.service;

import com.projetoa2.model.Usuario;
import com.projetoa2.repository.RepositorioUsuario;

import java.util.List;

public class ServicoUsuario {
    private final RepositorioUsuario repositorioUsuario;

    public ServicoUsuario(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    public Usuario criarUsuario(Usuario u) {
        repositorioUsuario.salvar(u);
        return u;
    }

    public List<Usuario> obterTodos() {
        return repositorioUsuario.buscarTodos();
    }

    public Usuario buscarPorNome(String nome) {
        return repositorioUsuario.buscarPorNome(nome);
    }

    public Usuario buscarPorId(int id) { return repositorioUsuario.buscarPorId(id); }

    /**
     * Remove um usuário por id. Não permite remoção de administradores.
     * @param id id do usuário a remover
     * @return true se removido, false se não encontrado ou se for administrador
     */
    public boolean removerUsuario(int id) {
        Usuario u = repositorioUsuario.buscarPorId(id);
        if (u == null) return false;
        if (u.ehAdministrador()) return false;
        repositorioUsuario.remover(id);
        return true;
    }

    public Usuario autenticar(String nome, String senha) {
        Usuario u = repositorioUsuario.buscarPorNome(nome);
        if (u != null && u.getSenha().equals(senha)) return u;
        return null;
    }
}
