package com.practica.crudpruebas.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @EnableMethodSecurity habilita @PreAuthorize en los Controllers (lo
// usamos mas abajo, en el endpoint de solo-ADMIN).
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // BCrypt: cada vez que hasheas la MISMA clave da un resultado DISTINTO
    // (lleva un "salt" random incluido) -- por eso nunca comparas password
    // en texto plano contra el hash con equals(), siempre con
    // passwordEncoder.matches(claveEnTextoPlano, hashGuardado).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protege contra ataques que abusan de COOKIES de sesion.
                // Con JWT no hay sesion ni cookie de por medio -- no aplica.
                .csrf(csrf -> csrf.disable())
                // STATELESS: el server NUNCA guarda sesion entre requests.
                // Cada request se autentica de cero, con el token que trae.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // login/registro: publicos
                        .anyRequest().authenticated()                  // todo lo demas: necesita token valido
                )
                // Sin esto, Spring Security trata a un request SIN token como un
                // usuario "anonimo autenticado" -- si le falta permiso, dispara
                // AccessDeniedException (403) en vez de AuthenticationException
                // (401). Para una API REST con JWT, la convencion correcta es:
                // 401 = no mandaste token (o es invalido), 403 = mandaste un
                // token valido pero te falta el rol/permiso necesario.
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // Nuestro filtro corre ANTES que el filtro estandar de Spring
                // Security que maneja login por formulario -- asi el token
                // ya establecio quien sos antes de que se evalue la regla
                // de arriba (anyRequest().authenticated()).
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
