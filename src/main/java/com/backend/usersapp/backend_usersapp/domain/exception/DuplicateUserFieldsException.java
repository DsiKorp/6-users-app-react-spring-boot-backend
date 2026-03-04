package com.backend.usersapp.backend_usersapp.domain.exception;

import java.util.List;

public class DuplicateUserFieldsException extends RuntimeException {

    private final List<DuplicateField> duplicateFields;

    public DuplicateUserFieldsException(List<DuplicateField> duplicateFields) {
        super("Se encontraron datos de usuario ya registrados");
        this.duplicateFields = List.copyOf(duplicateFields);
    }

    public List<DuplicateField> getDuplicateFields() {
        return duplicateFields;
    }

    public record DuplicateField(
            String field,
            String message
            ) {

    }
}
