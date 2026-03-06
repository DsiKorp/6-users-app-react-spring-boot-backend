package com.backend.usersapp.backend_usersapp.reposotories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.backend.usersapp.backend_usersapp.models.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findFirstByName(String name);
}
