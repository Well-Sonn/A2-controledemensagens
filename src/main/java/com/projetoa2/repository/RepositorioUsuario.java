package com.projetoa2.repository;

import com.projetoa2.model.Usuario;

public interface RepositorioUsuario extends Repositorio<Usuario> {
    Usuario buscarPorNome(String nome);
}
