package com.projetoa2.service;

import com.projetoa2.model.User;
import com.projetoa2.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User u) {
        userRepository.save(u);
        return u;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(int id) { return userRepository.findById(id); }

    public void deleteUser(int id) { userRepository.delete(id); }

    public User authenticate(String username, String password) {
        User u = userRepository.findByUsername(username);
        if (u != null && u.getPassword().equals(password)) return u;
        return null;
    }
}
