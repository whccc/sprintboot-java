package com.practica.crudpruebas.clean.producto.entities;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(Long id) {
        super("No existe un producto con id " + id);
    }
}
