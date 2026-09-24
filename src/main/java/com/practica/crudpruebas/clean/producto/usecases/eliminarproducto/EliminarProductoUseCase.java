package com.practica.crudpruebas.clean.producto.usecases.eliminarproducto;

import com.practica.crudpruebas.clean.producto.usecases.boundary.ProductoRepository;
import org.springframework.stereotype.Component;

@Component
public class EliminarProductoUseCase {

    private final ProductoRepository productoRepository;

    public EliminarProductoUseCase(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public void execute(Long id) {
        productoRepository.eliminar(id);
    }
}
