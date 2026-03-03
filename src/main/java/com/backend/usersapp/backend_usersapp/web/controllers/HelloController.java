package com.backend.usersapp.backend_usersapp.web.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.usersapp.backend_usersapp.domain.services.GreetingAiService;

@RestController
@RequestMapping("/hello")
public class HelloController {

    private final String plataform;
    private final GreetingAiService aiService;

    public HelloController(@Value("${spring.application.name}") String plataform, GreetingAiService aiService) {
        System.out.println(plataform);
        this.plataform = plataform;
        this.aiService = aiService;
    }

    @GetMapping
    public String hello() {
        return this.aiService.generateGreeting(plataform);
    }

}
