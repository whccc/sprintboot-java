package com.practica.crudpruebas.common.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Corre UNA vez por cada request HTTP, antes de que llegue a ningun
// Controller. Su trabajo: leer el header "Authorization: Bearer <token>",
// validarlo, y si es valido, decirle a Spring Security "este request viene
// de un usuario autenticado" -- llenando el SecurityContext.
//
// Si no hay token, o es invalido, simplemente NO llena el SecurityContext
// -- el request sigue su curso sin autenticar, y sera SecurityConfig quien
// decida si ese endpoint puntual necesita autenticacion o no.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // saca "Bearer "

        try {
            String username = jwtService.extraerUsername(token);

            // Solo autenticamos si: (1) hay username en el token, y (2)
            // todavia no hay nadie autenticado en este request (evita pisar
            // una autenticacion ya hecha por otro mecanismo).
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.esValido(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Request autenticado via JWT para el usuario '{}'", username);
                } else {
                    log.warn("Token JWT invalido o expirado para el usuario '{}' desde {}",
                            username, request.getRemoteAddr());
                }
            }
        } catch (JwtException ex) {
            // Token malformado, firma invalida, o expirado (JJWT tira
            // ExpiredJwtException/MalformedJwtException/SignatureException,
            // todas heredan de JwtException). Sin este catch, esto se iba
            // sin capturar hasta explotar como un 500 -- en vez de dejar que
            // SecurityConfig decida (401/403) como con cualquier request sin
            // autenticar. NUNCA se loguea el token en si, solo el motivo.
            log.warn("Token JWT rechazado desde {}: {}", request.getRemoteAddr(), ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
