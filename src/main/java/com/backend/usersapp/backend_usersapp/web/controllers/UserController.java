package com.backend.usersapp.backend_usersapp.web.controllers;

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

import com.backend.usersapp.backend_usersapp.domain.services.UserService;
import com.backend.usersapp.backend_usersapp.models.entities.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
// @CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Users", description = "user-controller: Operations about users of the application") // Swagger
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> list() {
        return userService.findAll();
    }

    /* 
        - ResponseEntity is a wrapper for the response, it allows us to return different 
        status codes and headers
        - @PathVariable(name = "id") is optional, it is used to specify the name of the path variable, 
        if it is not specified, it will be the same as the parameter name (id in this case)
        - We use Optional to handle the case when the user is not found, 
        it allows us to return a 404 Not Found response
        - Optional is a container object which may or may not contain a non-null value. 
        If a value is present, isPresent() will return true and get() will return the value.
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            summary = "Get a user by its identifier",
            description = "Returns a user that matches the sent identifier.",
            responses = {
                @ApiResponse(responseCode = "200", description = "User found",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    public ResponseEntity<User> show(@Parameter(description = "ID of the user to be retrieved", example = "1") @PathVariable(name = "id") Long id) {
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
    public ResponseEntity<?> create(@RequestBody @Valid User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user)); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody @Valid User user, @Parameter(description = "ID of the user to be updated", example = "1") @PathVariable Long id) {
        Optional<User> userOptional = userService.update(user, id);
        if (userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(userOptional.orElseThrow()); // 201 Created
        }
        return ResponseEntity.notFound().build(); // 404
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@Parameter(description = "ID of the user to be deleted") @PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // esta implementación no lanza el user-not-found exception, sino que devuelve un 404 Not Found si el usuario no existe, 
    //@DeleteMapping("/{id}")
    // public ResponseEntity<?> deleteById2(@PathVariable Long id) {
    //     Optional<User> o = userService.findById(id);
    //     if (o.isPresent()) {
    //         userService.deleteById(id);
    //         return ResponseEntity.noContent().build(); // 204
    //     }
    //     return ResponseEntity.notFound().build(); // 404
    // }
}
