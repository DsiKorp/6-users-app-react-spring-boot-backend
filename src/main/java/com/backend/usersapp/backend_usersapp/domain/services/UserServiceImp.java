package com.backend.usersapp.backend_usersapp.domain.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.usersapp.backend_usersapp.domain.exception.EmailAlreadyExistsException;
import com.backend.usersapp.backend_usersapp.domain.exception.UserAlreadyExistsException;
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
        // Verificar si ya existe un usuario con el mismo nombre de usuario, si es así, 
        // lanzar una excepción UserAlreadyExistsException para evitar duplicados en la base de datos
        if (this.userRepository.findFirstByUsername(user.getUsername()) != null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        if (this.userRepository.findFirstByEmail(user.getEmail()) != null) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

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

        if (this.userRepository.findFirstByUsernameAndIdNot(user.getUsername(), id) != null) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        if (this.userRepository.findFirstByEmailAndIdNot(user.getEmail(), id) != null) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        User userDb = userOptional.orElseThrow();
        userDb.setUsername(user.getUsername());
        userDb.setEmail(user.getEmail());
        userDb.setPassword(user.getPassword());
        return Optional.of(this.userRepository.save(userDb));
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
