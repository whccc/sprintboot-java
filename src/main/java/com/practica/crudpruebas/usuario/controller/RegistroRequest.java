package com.practica.crudpruebas.usuario.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 8, message = "La clave debe tener al menos 8 caracteres") String password
) {
}
