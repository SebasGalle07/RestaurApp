package com.restaurapp.demo.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final DbUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1) Opcional: permitir preflight CORS sin autenticación
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Rutas públicas (deben coincidir con lo permitido en SecurityConfig)
        String path = request.getRequestURI();
        if (path.startsWith("/auth/") || "/actuator/health".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Evitar trabajo si ya estamos autenticados
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4) Leer y validar header Authorization
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        authHeader = authHeader.trim();
        if (!authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 5) Parsear y validar JWT
            Claims claims = jwtService.parse(token).getBody();
            String username = claims.getSubject();
            if (username == null || username.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            // 6) Cargar usuario y poblar SecurityContext
            UserDetails ud = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (ExpiredJwtException e) {
            // Token expirado: dejamos seguir sin autenticación; el controller devolverá 401 si la ruta lo exige
            // (Opcional) request.setAttribute("jwt_error", "expired");
        } catch (JwtException | IllegalArgumentException e) {
            // Token inválido o mal formado
            // (Opcional) request.setAttribute("jwt_error", "invalid");
        }

        // 7) Continuar la cadena
        filterChain.doFilter(request, response);
    }
}
