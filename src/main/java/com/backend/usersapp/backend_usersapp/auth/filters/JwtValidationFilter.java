package com.backend.usersapp.backend_usersapp.auth.filters;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.backend.usersapp.backend_usersapp.auth.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.backend.usersapp.backend_usersapp.auth.TokenJwtConfig.PREFIX_TOKEN;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    private final String secret;

    public JwtValidationFilter(AuthenticationManager authenticationManager, String secret) {
        super(authenticationManager);
        this.secret = secret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(HEADER_AUTHORIZATION);

        logger.info("header " + header);

        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace(PREFIX_TOKEN, "");
        logger.info("token " + token);

        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Object authoritiesClaims = claims.get("authorities");

            String username = claims.getSubject();
            logger.info("username " + username);

            Collection<? extends GrantedAuthority> authorities = List.of();
            if (authoritiesClaims instanceof Collection<?> authoritiesCollection) {
                authorities = authoritiesCollection.stream()
                        .map(String::valueOf)
                        .filter(authority -> authority.startsWith("ROLE_"))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
            }

            // password no es necesario por que se esta validando el token, no para validar
            // el usuario
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null,
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);
            chain.doFilter(request, response);
        } catch (JwtException e) {
            Map<String, Object> body = new HashMap<>();
            body.put("message", "Token no valido");
            body.put("error", e.getMessage());

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(403);
            response.setContentType("application/json");
        }
    }

}
