package com.backend.usersapp.backend_usersapp.domain.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String username) {
        super("El nombre de usuario '" + username + "' ya existe");
    }

}
