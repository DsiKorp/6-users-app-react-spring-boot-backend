package com.backend.usersapp.backend_usersapp.persistence.mapper;

import com.backend.usersapp.backend_usersapp.domain.dto.UserDto;
import com.backend.usersapp.backend_usersapp.models.entities.User;

// Uses builder pattern to create a mapper for UserDto
public class DtoMapperUser {

    private User user;

    private DtoMapperUser() {
        // Constructor privado para evitar instanciación
    }

    public static DtoMapperUser builder() {
        return new DtoMapperUser();
    }

    public DtoMapperUser setUser(User user) {
        this.user = user;
        return this;
    }

    public UserDto build() {
        if (user == null) {
            throw new RuntimeException("Debe pasar el entity user!");
        }
        return new UserDto(this.user.getId(), user.getUsername(), user.getEmail());
    }

}
