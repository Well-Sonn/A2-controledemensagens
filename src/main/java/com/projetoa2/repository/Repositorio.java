package com.projetoa2.repository;

import java.util.List;

public interface Repositorio<T> {
    List<T> buscarTodos();
    T buscarPorId(int id);
    void salvar(T entidade);
    void remover(int id);
}
