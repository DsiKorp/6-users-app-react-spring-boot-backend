package com.backend.usersapp.backend_usersapp.web.exception;

import java.util.List;

public record ValidationErrorResponse(
        String type, String message,
        List<ValidationFieldError> errors
        ) {

}
