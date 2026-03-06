package com.backend.usersapp.backend_usersapp.auth.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.backend.usersapp.backend_usersapp.reposotories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<com.backend.usersapp.backend_usersapp.models.entities.User> userOptional = userRepository
                .getUserByUsername(username);

        if (!userOptional.isPresent()) { // if (!username.equals("admin")) {
            throw new UsernameNotFoundException(String.format("Username %s no existe en el sistema!", username));
        }

        com.backend.usersapp.backend_usersapp.models.entities.User user = userOptional.orElseThrow();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(
                // username,
                // "$2a$10$DOMDxjYyfZ/e7RcBfUpzqeaCs8pLgcizuiQWXPkU35nOhZlFcE9MS",
                // "12345",
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                authorities);

    }
}
