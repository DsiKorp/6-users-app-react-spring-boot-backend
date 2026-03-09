package com.backend.usersapp.backend_usersapp.domain.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// record define un objeto inmutable,
// autogenera los constructores, getters, equals, hashCode y toString
public record UserUpdateDto(
        @NotBlank(message = "El username es obligatorio")
        @JsonAlias({"userName"})
        @Size(min = 4, max = 40, message = "El username debe tener entre 4 y 40 caracteres")
        String username,
        //************************************************************************//
        @NotBlank(message = "El email es obligatorio")
        @JsonAlias({"eMail"})
        @Email(message = "El email no es válido")
        @Size(max = 100, message = "El email no puede tener más de 100 caracteres")
        String email,
        //////////////////////////////////////////////////////////////////////////////
        Boolean admin
        ) {

}
