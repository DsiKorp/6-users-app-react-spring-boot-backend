package com.backend.usersapp.backend_usersapp.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40, unique = true)
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 40, message = "El username debe tener entre 4 y 40 caracteres")
    private String username;

    @Column(nullable = false, length = 100, unique = true)
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    @Size(max = 100, message = "El email no puede tener más de 100 caracteres")
    private String email;

    @Column(nullable = false, length = 60)
    @NotBlank(message = "El password es obligatorio")
    @Size(min = 6, max = 60, message = "El password debe tener entre 6 y 60 caracteres")
    private String password;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
