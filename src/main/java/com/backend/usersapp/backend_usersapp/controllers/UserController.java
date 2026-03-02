package com.backend.usersapp.backend_usersapp.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.usersapp.backend_usersapp.models.entities.User;
import com.backend.usersapp.backend_usersapp.services.UserService;

@RestController
@RequestMapping("/users")
// @CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> list() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    // name = "id" is optional, but it is a good practice to use it for better readability
    // ResponseEntity is a wrapper for the response, it allows us to return different 
    //  status codes and headers
    public ResponseEntity<?> show(@PathVariable(name = "id") Long id) {
        // We use Optional to handle the case when the user is not found, 
        // it allows us to return a 404 Not Found response
        // Optional is a container object which may or may not contain a non-null value. 
        // If a value is present, isPresent() will return true and get() will return the value.
        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow()); // 200 OK
        }

        // If the user is not found, return a 404 Not Found response
        return ResponseEntity.notFound().build(); // 404 Not Found
    }

    // @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
    // public User create(@RequestBody User user) {
    //     return userService.save(user);
    // }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user)); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody User user, @PathVariable Long id) {
        Optional<User> userOptional = userService.update(user, id);
        if (userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(userOptional.orElseThrow()); // 201 Created
        }
        return ResponseEntity.notFound().build(); // 404
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        Optional<User> o = userService.findById(id);

        if (o.isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.notFound().build(); // 404
    }
}
