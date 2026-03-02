package com.backend.usersapp.backend_usersapp.services;

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
            Eres un experto en cine que recomienda películas personalizadas según los gustos del usuario.
            Debes recomendar máximo 3 películas.
            No incluyas películas que estén por fuera de la plataforma PlatziPlay.
            """)
    String generateMoviesSuggestion(@UserMessage String userMessage);
}
