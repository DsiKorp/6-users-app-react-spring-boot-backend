package com.backend.usersapp.backend_usersapp.domain.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }

}
