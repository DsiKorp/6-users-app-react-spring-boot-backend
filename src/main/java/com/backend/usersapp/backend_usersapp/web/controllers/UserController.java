package com.backend.usersapp.backend_usersapp.web.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.usersapp.backend_usersapp.domain.dto.SuggestRequestDto;
import com.backend.usersapp.backend_usersapp.domain.dto.UserUpdateDto;
import com.backend.usersapp.backend_usersapp.domain.services.GreetingAiService;
import com.backend.usersapp.backend_usersapp.domain.services.UserService;
import com.backend.usersapp.backend_usersapp.models.entities.User;
import com.backend.usersapp.backend_usersapp.web.exception.Error;
import com.backend.usersapp.backend_usersapp.web.exception.ValidationErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
// @CrossOrigin(origins = "http://localhost:5173")
@CrossOrigin(originPatterns = "*", allowCredentials = "true") // Permitir solicitudes desde cualquier origen con
// credenciales
@Tag(name = "Users", description = "user-controller: Operations about users of the application") // Swagger
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private GreetingAiService aiService;

    @GetMapping(produces = "application/json")
    @Operation(summary = "Get all users", description = "Returns the complete list of users.", responses = {
        @ApiResponse(responseCode = "200", description = "Users found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User[].class)))
    })
    public List<User> list() {
        return userService.findAll();
    }

    @GetMapping(value = "/admin", produces = "application/json")
    @Operation(summary = "Get all admin users", description = "Returns the complete list of admin users.", responses = {
        @ApiResponse(responseCode = "200", description = "Users found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User[].class)))
    })
    public List<User> listAdmin() {
        return userService.findAllAdmin();
    }

    /*
         * - ResponseEntity is a wrapper for the response, it allows us to return
         * different
         * status codes and headers
         * - @PathVariable(name = "id") is optional, it is used to specify the name of
         * the path variable,
         * if it is not specified, it will be the same as the parameter name (id in this
         * case)
         * - We use Optional to handle the case when the user is not found,
         * it allows us to return a 404 Not Found response
         * - Optional is a container object which may or may not contain a non-null
         * value.
         * If a value is present, isPresent() will return true and get() will return the
         * value.
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Get a user by its identifier", description = "Returns a user that matches the sent identifier.", responses = {
        @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<User> show(
            @Parameter(description = "ID of the user to be retrieved", example = "1") @PathVariable(name = "id") Long id) {
        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow()); // 200 OK
        }

        // If the user is not found, return a 404 Not Found response
        return ResponseEntity.notFound().build(); // 404 Not Found
    }

    @GetMapping(value = "/admin/{id}", produces = "application/json")
    @Operation(summary = "Get an admin user by its identifier", description = "Returns an admin user that matches the sent identifier.", responses = {
        @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<User> showAdmin(
            @Parameter(description = "ID of the admin user to be retrieved", example = "1") @PathVariable(name = "id") Long id) {
        Optional<User> userOptional = userService.findByIdAdmin(id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.orElseThrow()); // 200 OK
        }

        // If the user is not found, return a 404 Not Found response
        return ResponseEntity.notFound().build(); // 404 Not Found
    }

    // @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
    // public User create(@RequestBody User user) {
    // return userService.save(user);
    // }
    @PostMapping(produces = "application/json")
    @Operation(summary = "Create a new user", description = "Creates a user with the provided information.", responses = {
        @ApiResponse(responseCode = "201", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Username or email already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class), examples = {
            @ExampleObject(name = "Username y email duplicados", value = "{\n  \"type\": \"duplicate-data-error\",\n  \"message\": \"Se encontraron datos de usuario ya registrados\",\n  \"errors\": [\n    {\n      \"field\": \"username\",\n      \"message\": \"El nombre de usuario 'hola2' ya existe\"\n    },\n    {\n      \"field\": \"email\",\n      \"message\": \"El correo 'hola2@mail.com' ya existe\"\n    }\n  ]\n}"),
            @ExampleObject(name = "Solo username duplicado", value = "{\n  \"type\": \"duplicate-data-error\",\n  \"message\": \"Se encontraron datos de usuario ya registrados\",\n  \"errors\": [\n    {\n      \"field\": \"username\",\n      \"message\": \"El nombre de usuario 'hola2' ya existe\"\n    }\n  ]\n}")
        }))
    })
    public ResponseEntity<User> create(@RequestBody @Valid User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user)); // 201 Created
    }

    @PostMapping(value = "/admin", produces = "application/json")
    @Operation(summary = "Create a new admin user", description = "Creates an admin user with the provided information.", responses = {
        @ApiResponse(responseCode = "201", description = "Admin user created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Username or email already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class), examples = {
            @ExampleObject(name = "Username y email duplicados", value = "{\n  \"type\": \"duplicate-data-error\",\n  \"message\": \"Se encontraron datos de usuario ya registrados\",\n  \"errors\": [\n    {\n      \"field\": \"username\",\n      \"message\": \"El nombre de usuario 'hola2' ya existe\"\n    },\n    {\n      \"field\": \"email\",\n      \"message\": \"El correo 'hola2@mail.com' ya existe\"\n    }\n  ]\n}"),
            @ExampleObject(name = "Solo username duplicado", value = "{\n  \"type\": \"duplicate-data-error\",\n  \"message\": \"Se encontraron datos de usuario ya registrados\",\n  \"errors\": [\n    {\n      \"field\": \"username\",\n      \"message\": \"El nombre de usuario 'hola2' ya existe\"\n    }\n  ]\n}")
        }))
    })
    public ResponseEntity<User> createAdmin(@RequestBody @Valid User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveAdmin(user)); // 201 Created
    }

    /*
    Revisar en UserServiceImp el método findAll() con @Tool para que el agente de IA pueda utilizarlo para responder a las solicitudes de los usuarios,
    @Tool("Buscar todos los usuarios que dentro de la plataforma")
     */
    @PostMapping("/suggest")
    public ResponseEntity<String> generateUserNameSuggestion(@RequestBody SuggestRequestDto suggestRequestDto) {
        return ResponseEntity.ok(this.aiService.generateUserNameSuggestion(suggestRequestDto.userPreferences()));
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Update an existing user", description = "Updates user data by identifier.", responses = {
        @ApiResponse(responseCode = "201", description = "User updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
        @ApiResponse(responseCode = "409", description = "Username or email already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class), examples = {
            @ExampleObject(name = "Username y email duplicados", value = "{\n  \"type\": \"duplicate-data-error\",\n  \"message\": \"Se encontraron datos de usuario ya registrados\",\n  \"errors\": [\n    {\n      \"field\": \"username\",\n      \"message\": \"El nombre de usuario 'hola2' ya existe\"\n    },\n    {\n      \"field\": \"email\",\n      \"message\": \"El correo 'hola2@mail.com' ya existe\"\n    }\n  ]\n}"),
            @ExampleObject(name = "Solo email duplicado", value = "{\n  \"type\": \"duplicate-data-error\",\n  \"message\": \"Se encontraron datos de usuario ya registrados\",\n  \"errors\": [\n    {\n      \"field\": \"email\",\n      \"message\": \"El correo 'hola2@mail.com' ya existe\"\n    }\n  ]\n}")
        }))
    })
    public ResponseEntity<User> update(@RequestBody @Valid UserUpdateDto userUpdateDto,
            @Parameter(description = "ID of the user to be updated", example = "1") @PathVariable Long id) {
        Optional<User> userOptional = userService.update(userUpdateDto, id);
        if (userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(userOptional.orElseThrow()); // 201
            // Created
        }
        return ResponseEntity.notFound().build(); // 404
    }

    @PutMapping(value = "/admin/{id}", produces = "application/json")
    @Operation(summary = "Update an existing user", description = "Updates user data by identifier.", responses = {
        @ApiResponse(responseCode = "201", description = "User updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
        @ApiResponse(responseCode = "409", description = "Username or email already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class), examples = {
            @ExampleObject(name = "Username y email duplicados", value = "{\n  \"type\": \"duplicate-data-error\",\n  \"message\": \"Se encontraron datos de usuario ya registrados\",\n  \"errors\": [\n    {\n      \"field\": \"username\",\n      \"message\": \"El nombre de usuario 'hola2' ya existe\"\n    },\n    {\n      \"field\": \"email\",\n      \"message\": \"El correo 'hola2@mail.com' ya existe\"\n    }\n  ]\n}"),
            @ExampleObject(name = "Solo email duplicado", value = "{\n  \"type\": \"duplicate-data-error\",\n  \"message\": \"Se encontraron datos de usuario ya registrados\",\n  \"errors\": [\n    {\n      \"field\": \"email\",\n      \"message\": \"El correo 'hola2@mail.com' ya existe\"\n    }\n  ]\n}")
        }))
    })
    public ResponseEntity<User> updateAdmin(@RequestBody @Valid UserUpdateDto userUpdateDto,
            @Parameter(description = "ID of the user to be updated", example = "1") @PathVariable Long id) {
        Optional<User> userOptional = userService.updateAdmin(userUpdateDto, id);
        if (userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(userOptional.orElseThrow()); // 201
            // Created
        }
        return ResponseEntity.notFound().build(); // 404
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Delete a user by its identifier", description = "Deletes a user that matches the sent identifier.", responses = {
        @ApiResponse(responseCode = "204", description = "User deleted", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID of the user to be deleted", example = "1") @PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // esta implementación no lanza el user-not-found exception, sino que devuelve
    // un 404 Not Found si el usuario no existe,
    // @DeleteMapping("/{id}")
    // public ResponseEntity<?> deleteById2(@PathVariable Long id) {
    // Optional<User> o = userService.findById(id);
    // if (o.isPresent()) {
    // userService.deleteById(id);
    // return ResponseEntity.noContent().build(); // 204
    // }
    // return ResponseEntity.notFound().build(); // 404
    // }
}
