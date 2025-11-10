package com.projetoa2.repository;

import com.projetoa2.model.User;

public interface UserRepository extends Repository<User> {
    User findByUsername(String username);
}
