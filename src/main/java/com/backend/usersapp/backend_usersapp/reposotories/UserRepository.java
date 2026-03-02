package com.backend.usersapp.backend_usersapp.reposotories;

import org.springframework.data.repository.CrudRepository;

import com.backend.usersapp.backend_usersapp.models.entities.User;

public interface UserRepository  extends CrudRepository<User, Long>{

}
