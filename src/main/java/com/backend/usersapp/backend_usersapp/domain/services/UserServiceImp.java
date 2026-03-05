package com.backend.usersapp.backend_usersapp.domain.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.usersapp.backend_usersapp.domain.dto.UserUpdateDto;
import com.backend.usersapp.backend_usersapp.domain.exception.DuplicateUserFieldsException;
import com.backend.usersapp.backend_usersapp.domain.exception.DuplicateUserFieldsException.DuplicateField;
import com.backend.usersapp.backend_usersapp.domain.exception.UserNotFoundException;
import com.backend.usersapp.backend_usersapp.models.entities.User;
import com.backend.usersapp.backend_usersapp.persistence.mapper.UserMapper;
import com.backend.usersapp.backend_usersapp.reposotories.UserRepository;

import dev.langchain4j.agent.tool.Tool;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public User save(User user) {
        validateDuplicatedData(user, null);

        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    // @Tool para indicar que este método es una herramienta que puede ser utilizada por el agente de IA para responder a las solicitudes de los usuarios, 
    // en este caso, para buscar todos los usuarios disponibles en la plataforma
    @Tool("Buscar todos los usuarios que dentro de la plataforma")
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<User> update(UserUpdateDto userUpdateDto, Long id) {
        Optional<User> userOptional = this.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        validateDuplicatedData(userUpdateDto, id);

        User userDb = userOptional.orElseThrow();
        //userDb.setUsername(userUpdateDto.username());
        //userDb.setEmail(userUpdateDto.email());
        //userDb.setPassword(userUpdateDto.password());
        // mapstruct para actualizar el objeto userDb con los valores del DTO userUpdateDto, sin necesidad de escribir manualmente cada asignación de campo, 
        // lo que reduce el código repetitivo y mejora la mantenibilidad del código, además de evitar errores humanos al asignar los campos incorrectamente
        userMapper.updateEntityFromDto(userUpdateDto, userDb);
        return Optional.of(this.userRepository.save(userDb));
    }

    private void validateDuplicatedData(User user, Long excludedUserId) {
        validateDuplicatedData(user.getUsername(), user.getEmail(), excludedUserId);
    }

    private void validateDuplicatedData(UserUpdateDto userUpdateDto, Long excludedUserId) {
        validateDuplicatedData(userUpdateDto.username(), userUpdateDto.email(), excludedUserId);
    }

    private void validateDuplicatedData(String username, String email, Long excludedUserId) {
        List<DuplicateField> duplicateFields = new ArrayList<>();

        if (isUsernameTaken(username, excludedUserId)) {
            duplicateFields.add(new DuplicateField("username", "El nombre de usuario '" + username + "' ya existe"));
        }

        if (isEmailTaken(email, excludedUserId)) {
            duplicateFields.add(new DuplicateField("email", "El correo '" + email + "' ya existe"));
        }

        if (!duplicateFields.isEmpty()) {
            throw new DuplicateUserFieldsException(duplicateFields);
        }
    }

    private boolean isUsernameTaken(String username, Long excludedUserId) {
        if (excludedUserId == null) {
            return this.userRepository.findFirstByUsername(username) != null;
        }

        return this.userRepository.findFirstByUsernameAndIdNot(username, excludedUserId) != null;
    }

    private boolean isEmailTaken(String email, Long excludedUserId) {
        if (excludedUserId == null) {
            return this.userRepository.findFirstByEmail(email) != null;
        }

        return this.userRepository.findFirstByEmailAndIdNot(email, excludedUserId) != null;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (this.userRepository.findFirstById(id) == null) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }

}
