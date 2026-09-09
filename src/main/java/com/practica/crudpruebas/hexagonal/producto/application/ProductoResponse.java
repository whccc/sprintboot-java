package com.practica.crudpruebas.hexagonal.producto.application;

import java.util.List;

// DTO de salida. A diferencia del dominio Producto (que solo tiene
// categoriaId/etiquetaIds), este SI trae los nombres -- porque es lo que
// el cliente HTTP necesita ver. El Service es quien hace el "join" entre
// los distintos puertos para armar esto.
public record ProductoResponse(
        Long id,
        String nombre,
        double precio,
        int stock,
        String categoriaNombre,
        List<String> etiquetas
) {
}
