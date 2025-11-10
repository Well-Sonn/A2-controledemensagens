package com.projetoa2.factory;

import com.projetoa2.model.User;

import java.util.concurrent.atomic.AtomicInteger;

public class UserFactory {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    public static User create(String username, String password) {
        return new User(COUNTER.getAndIncrement(), username, password, username.equals("admin"));
    }
}
