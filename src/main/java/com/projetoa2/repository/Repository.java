package com.projetoa2.repository;

import java.util.List;

public interface Repository<T> {
    List<T> findAll();
    T findById(int id);
    void save(T entity);
    void delete(int id);
}
