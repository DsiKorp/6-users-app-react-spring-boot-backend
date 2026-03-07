package com.backend.usersapp.backend_usersapp.domain.services;

import java.util.List;
import java.util.Optional;

import com.backend.usersapp.backend_usersapp.domain.dto.UserUpdateDto;
import com.backend.usersapp.backend_usersapp.models.entities.User;

public interface UserService {

    User save(User user);

    User saveAdmin(User user);

    Optional<User> update(UserUpdateDto userUpdateDto, Long id);

    Optional<User> updateAdmin(UserUpdateDto userUpdateDto, Long id);

    List<User> findAll();

    List<User> findAllAdmin();

    Optional<User> findById(Long id);

    Optional<User> findByIdAdmin(Long id);

    void deleteById(Long id);
}
