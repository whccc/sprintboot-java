package com.practica.crudpruebas.usuario.controller;

import com.practica.crudpruebas.common.security.JwtService;
import com.practica.crudpruebas.usuario.model.Usuario;
import com.practica.crudpruebas.usuario.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// Los UNICOS 2 endpoints publicos de toda la API (ver SecurityConfig:
// "/api/auth/**" esta en permitAll()). Todo lo demas, de aca en mas,
// necesita un token valido en el header Authorization.
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Registro y login -- los unicos 2 endpoints publicos de la API")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un usuario nuevo y devuelve un token de una")
    // SecurityRequirements() VACIO le dice a Swagger "este endpoint puntual
    // NO hereda el bearerAuth global" -- sin esto, la UI le pondria el
    // candado igual, aunque el endpoint sea publico de verdad.
    @SecurityRequirements
    public TokenResponse registrar(@Valid @RequestBody RegistroRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese username");
        }

        // NUNCA se guarda la clave en texto plano -- BCrypt genera un hash
        // de un solo sentido (no se puede "deshashear" para recuperarla).
        Usuario usuario = new Usuario(request.username(), passwordEncoder.encode(request.password()), "USER");
        usuarioRepository.save(usuario);

        return new TokenResponse(jwtService.generarToken(usuario.getUsername()));
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica un usuario existente y devuelve un token")
    @SecurityRequirements
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        // authenticate() hace todo el trabajo: busca el Usuario via
        // UsuarioDetailsService, compara la clave con passwordEncoder.matches(),
        // y tira BadCredentialsException si algo no coincide -- lo captura
        // ManejadorDeErrores (agregamos el handler mas abajo).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        return new TokenResponse(jwtService.generarToken(request.username()));
    }
}
