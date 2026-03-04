package com.backend.usersapp.backend_usersapp.reposotories;

import org.springframework.data.repository.CrudRepository;

import com.backend.usersapp.backend_usersapp.models.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
    // Query method para buscar un usuario por su nombre de usuario, devuelve la primera coincidencia 
    // o null si no se encuentra ningún usuario con ese nombre de usuario
    // Spring Data JPA genera automáticamente la implementación de este método a partir del nombre del método, siguiendo la convención de nomenclatura

    User findFirstByUsername(String username);

    User findFirstByUsernameAndIdNot(String username, Long id);

    User findFirstByEmail(String email);

    User findFirstByEmailAndIdNot(String email, Long id);

    User findFirstById(Long id);
}
