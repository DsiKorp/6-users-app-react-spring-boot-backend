package com.backend.usersapp.backend_usersapp.auth.filters;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.backend.usersapp.backend_usersapp.auth.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.backend.usersapp.backend_usersapp.auth.TokenJwtConfig.PREFIX_TOKEN;
import static com.backend.usersapp.backend_usersapp.auth.TokenJwtConfig.TOKEN_EXPIRATION_TIME;
import com.backend.usersapp.backend_usersapp.models.entities.User;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final String secret;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String secret) {
        this.authenticationManager = authenticationManager;
        this.secret = secret;
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = null;
        String password = null;

        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();

            logger.info("Username desde request InputStream (raw) " + username);
            logger.info("Password desde request InputStream (raw) " + password);

        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // JpaUserDetailsService
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        logger.info("authToken " + authToken.toString());

        return authenticationManager.authenticate(authToken);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
                .getUsername();

        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

        List<String> roleAuthorities = roles.stream()
            .map(GrantedAuthority::getAuthority)
            .filter(authority -> authority != null && authority.startsWith("ROLE_"))
            .distinct()
            .toList();

        boolean isAdmin = roleAuthorities.contains("ROLE_ADMIN");

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", roleAuthorities);
        claims.put("isAdmin", isAdmin);
        claims.put("username", username);

        SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        logger.info("secretKey " + secretKey.toString());

        // Generate token
        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();

        logger.info("-- token " + token);

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("message", String.format("Hola %s, has iniciado sesion con exito!", username));
        body.put("username", username);
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Error en la autenticacion username o password incorrecto!");
        body.put("error", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType("application/json");
    }

}
