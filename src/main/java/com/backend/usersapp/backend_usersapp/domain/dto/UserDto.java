package com.backend.usersapp.backend_usersapp.domain.dto;

public record UserDto(
        Long id,
        String username,
        String email,
        Boolean admin
        ) {

}
