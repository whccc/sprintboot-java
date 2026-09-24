package com.practica.crudpruebas.clean.producto.usecases.buscarproducto;

import com.practica.crudpruebas.clean.producto.entities.Producto;
import com.practica.crudpruebas.clean.producto.entities.ProductoNoEncontradoException;
import com.practica.crudpruebas.clean.producto.usecases.boundary.ProductoRepository;
import org.springframework.stereotype.Component;

@Component
public class BuscarProductoUseCase {

    private final ProductoRepository productoRepository;

    public BuscarProductoUseCase(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public BuscarProductoOutput execute(Long id) {
        Producto producto = productoRepository.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
        return new BuscarProductoOutput(producto.getId(), producto.getNombre(), producto.getPrecio(), producto.getStock());
    }
}
