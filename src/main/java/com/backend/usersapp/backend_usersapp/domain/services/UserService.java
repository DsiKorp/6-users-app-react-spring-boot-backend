package com.backend.usersapp.backend_usersapp.domain.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.usersapp.backend_usersapp.domain.dto.UserDto;
import com.backend.usersapp.backend_usersapp.domain.dto.UserUpdateDto;
import com.backend.usersapp.backend_usersapp.models.entities.User;

public interface UserService {

    UserDto save(User user);

    User saveAdmin(User user);

    Optional<UserDto> update(UserUpdateDto userUpdateDto, Long id);

    Optional<User> updateAdmin(UserUpdateDto userUpdateDto, Long id);

    List<UserDto> findAll();

    Page<UserDto> findAll(Pageable pageable);

    List<User> findAllAdmin();

    Optional<UserDto> findById(Long id);

    Optional<User> findByIdAdmin(Long id);

    void deleteById(Long id);

}
