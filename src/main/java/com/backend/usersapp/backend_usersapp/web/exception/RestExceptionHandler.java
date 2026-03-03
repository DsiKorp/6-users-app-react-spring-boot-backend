package com.backend.usersapp.backend_usersapp.web.exception;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    // MethodArgumentNotValidException es la excepción que se lanza cuando la validación de los argumentos de un método falla,
    // en este caso, cuando la validación de los campos del objeto User falla al crear un nuevo usuario o actualizar un usuario existente
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleException(MethodArgumentNotValidException ex) {
        List<ValidationFieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ValidationFieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .sorted(Comparator.comparing(ValidationFieldError::field, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(ValidationFieldError::message, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();

        ValidationErrorResponse error = new ValidationErrorResponse(
                "validation-error",
                "Se encontraron errores de validación",
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Validar esta excepción genérica para manejar cualquier otra excepción que no haya sido manejada por los métodos anteriores,
    // de esta manera, evitamos que se devuelvan respuestas HTTP con códigos de estado 500 Internal Server Error 
    // sin un mensaje de error claro para el cliente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception ex) {
        Error error = new Error("unknown-error", ex.getMessage());
        //ex.printStackTrace();
        return ResponseEntity.internalServerError().body(error);
    }
}
