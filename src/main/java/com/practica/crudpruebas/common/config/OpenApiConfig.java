package com.practica.crudpruebas.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Sin @SecurityScheme, Swagger UI muestra los endpoints protegidos igual
// que los publicos -- no sabe que existe un JWT de por medio. Con esto,
// aparece el boton "Authorize": pegas el token una vez, y todos los
// requests que dispares desde la UI lo mandan solo, sin copiarlo a mano
// en cada uno. @SecurityRequirement a nivel global aplica esto a TODOS
// los endpoints por default -- despues se puede excluir puntualmente
// (ver AuthController) para los 2 que son publicos de verdad.
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CRUD Pruebas API",
                version = "0.0.1",
                description = "API de aprendizaje Spring Boot -- CRUD de productos con 3 arquitecturas "
                        + "paralelas (por feature, hexagonal, clean) sobre el mismo dominio de negocio."
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token obtenido de POST /api/auth/login o /api/auth/registro. "
                + "Se manda como header: Authorization: Bearer <token>."
)
public class OpenApiConfig {
}
