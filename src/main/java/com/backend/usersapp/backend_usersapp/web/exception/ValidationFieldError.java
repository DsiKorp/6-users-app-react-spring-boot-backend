package com.backend.usersapp.backend_usersapp.web.exception;

public record ValidationFieldError(
        String field,
        String message
        ) {

}
