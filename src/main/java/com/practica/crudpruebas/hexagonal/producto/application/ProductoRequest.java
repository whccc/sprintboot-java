package com.practica.crudpruebas.hexagonal.producto.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

// Validacion de FORMA (HTTP): que el JSON tenga los campos con el tipo/
// formato esperado. Es una capa DISTINTA a la validacion de NEGOCIO que
// hace el constructor de Producto (dominio) -- las 2 existen a proposito,
// no es redundancia: esta protege la forma del request, la del dominio
// protege la invariante de negocio sin importar quien la llame.
public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @Positive(message = "El precio debe ser mayor a 0")
        double precio,

        @PositiveOrZero(message = "El stock no puede ser negativo")
        int stock,

        Long categoriaId
) {
}
