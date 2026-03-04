package com.backend.usersapp.backend_usersapp.domain.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.usersapp.backend_usersapp.domain.exception.DuplicateUserFieldsException;
import com.backend.usersapp.backend_usersapp.domain.exception.DuplicateUserFieldsException.DuplicateField;
import com.backend.usersapp.backend_usersapp.domain.exception.UserNotFoundException;
import com.backend.usersapp.backend_usersapp.models.entities.User;
import com.backend.usersapp.backend_usersapp.reposotories.UserRepository;

import dev.langchain4j.agent.tool.Tool;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

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
    public Optional<User> update(User user, Long id) {
        Optional<User> userOptional = this.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        validateDuplicatedData(user, id);

        User userDb = userOptional.orElseThrow();
        userDb.setUsername(user.getUsername());
        userDb.setEmail(user.getEmail());
        //userDb.setPassword(user.getPassword());
        return Optional.of(this.userRepository.save(userDb));
    }

    private void validateDuplicatedData(User user, Long excludedUserId) {
        List<DuplicateField> duplicateFields = new ArrayList<>();

        if (isUsernameTaken(user.getUsername(), excludedUserId)) {
            duplicateFields.add(new DuplicateField("username", "El nombre de usuario '" + user.getUsername() + "' ya existe"));
        }

        if (isEmailTaken(user.getEmail(), excludedUserId)) {
            duplicateFields.add(new DuplicateField("email", "El correo '" + user.getEmail() + "' ya existe"));
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
