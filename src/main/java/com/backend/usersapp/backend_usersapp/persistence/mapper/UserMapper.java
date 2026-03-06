package com.backend.usersapp.backend_usersapp.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.backend.usersapp.backend_usersapp.domain.dto.UserUpdateDto;
import com.backend.usersapp.backend_usersapp.models.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntityFromDto(UserUpdateDto updateUserDto, @MappingTarget User user);
    // @MappingTarget para indicar que el segundo parámetro es el objeto que se va a actualizar con los valores del DTO, 
    // en lugar de crear un nuevo objeto user
    // lo recibe por referencia, no por valor, por lo que se actualiza directamente el objeto 
    // original sin necesidad de devolverlo
}
