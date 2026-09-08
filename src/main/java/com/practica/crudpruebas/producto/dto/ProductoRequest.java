package com.practica.crudpruebas.producto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

// DTO de ENTRADA: lo que el cliente manda en POST/PUT.
// Un "record" de Java es una clase inmutable de solo datos -- genera solo,
// constructor, getters (nombre(), precio(), etc.), equals/hashCode/toString.
// Las anotaciones de validacion van directo sobre cada campo.
//
// A proposito NO tiene "id": el cliente nunca deberia poder decidir el id
// de un producto nuevo, eso lo genera la base de datos.
public record ProductoRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @Positive(message = "El precio debe ser mayor a 0")
        double precio,

        @PositiveOrZero(message = "El stock no puede ser negativo")
        int stock,

        // Opcional: si no se manda, el producto queda sin categoria.
        Long categoriaId
) {
}
