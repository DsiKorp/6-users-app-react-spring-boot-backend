package com.backend.usersapp.backend_usersapp.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backend.usersapp.backend_usersapp.domain.exception.EmailAlreadyExistsException;
import com.backend.usersapp.backend_usersapp.domain.exception.UserAlreadyExistsException;
import com.backend.usersapp.backend_usersapp.domain.exception.UserNotFoundException;

// @RestControllerAdvice para indicar que esta clase es un controlador de excepciones a nivel global,
//  que se encargará de manejar las excepciones lanzadas por los controladores REST 
// y devolver respuestas HTTP adecuadas
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Error> handleException(UserAlreadyExistsException ex) {
        Error error = new Error("user-already-exists", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Error> handleException(EmailAlreadyExistsException ex) {
        Error error = new Error("email-already-exists", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Error> handleException(UserNotFoundException ex) {
        Error error = new Error("user-not-found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
