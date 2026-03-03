package com.backend.usersapp.backend_usersapp.domain.dto;

public record UserDto(
        Long id,
        String userName,
        String eMail,
        String password
        ) {

}
