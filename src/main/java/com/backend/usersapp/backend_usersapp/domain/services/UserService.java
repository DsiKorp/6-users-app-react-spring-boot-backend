package com.backend.usersapp.backend_usersapp.domain.services;

import java.util.List;
import java.util.Optional;

import com.backend.usersapp.backend_usersapp.models.entities.User;

public interface UserService {

    User save(User user);

    Optional<User> update(User user, Long id);

    List<User> findAll();

    Optional<User> findById(Long id);

    void deleteById(Long id);
}
