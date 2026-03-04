package com.backend.usersapp.backend_usersapp.domain.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface GreetingAiService {

    @UserMessage("""
            Genera un saludo de bienvenida a la plataforma de Gestión de Usuarios {{plataform}}
            Usa menos de 120 caracteres y hazlo con el estilo de un administrador de usuarios
            """)
    String generateGreeting(@V("plataform") String plataform);

    @SystemMessage("""
            Eres un experto en seguridad informática que recomienda nombres de usuario (users names) que no existan en la base de datos.
            Debes recomendar máximo 5 nombres de usuario y no deben contener letras mayúsculas ni tildes.
            No incluyas nombres de usuario que estén en la plataforma Users App.
            """)
    String generateUserNameSuggestion(@UserMessage String userMessage);
}
