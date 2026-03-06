package com.backend.usersapp.backend_usersapp.auth.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.backend.usersapp.backend_usersapp.auth.TokenJwtConfig.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    private String secret;

    public JwtValidationFilter(AuthenticationManager authenticationManager, String secret) {
        super(authenticationManager);
        this.secret = secret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        logger.info("header " + header);

        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(PREFIX_TOKEN, "");
        logger.info("token " + token);

        byte[] tokenDecodedBytes = Base64.getDecoder().decode(token);

        String tokenDecodedString = new String(tokenDecodedBytes);

        logger.info("tokenDecodedString " + tokenDecodedString);

        String[] tokenParts = tokenDecodedString.split("\\.");
        logger.info("tokenParts " + tokenParts.length);

        String secretToken = tokenParts[0];
        String username = tokenParts[1];
        logger.info("secretToken " + secretToken);
        logger.info("username " + username);

        logger.info("secret " + secret);
        logger.info("secretToken " + secretToken);

        if (secret.equals(secretToken)) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            // password no es necesario por que se esta validando el token, no para validar
            // el usuario
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null,
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);
            chain.doFilter(request, response);
        } else {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Error en la autenticacion username o password incorrecto!");
            // body.put("error", failed.getMessage());

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(401);
            response.setContentType("application/json");
        }
    }

}
