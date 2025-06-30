package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.User;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Long createUser(String name, String email, Integer age) {
        User user = new User(name, email, age);
        userDao.save(user);
        return user.getId();
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public void updateUser(Long id, String name, String email, Integer age) {
        userDao.findById(id).ifPresent(user -> {
            if (name != null) user.setName(name);
            if (email != null) user.setEmail(email);
            if (age != null) user.setAge(age);
            userDao.update(user);
        });
    }

    public boolean deleteUser(Long id) {
        if (userDao.findById(id).isPresent()) {
            userDao.delete(id);
            return true;
        }
        return false;
    }
}