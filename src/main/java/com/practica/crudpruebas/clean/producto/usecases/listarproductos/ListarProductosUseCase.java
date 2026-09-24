package com.practica.crudpruebas.clean.producto.usecases.listarproductos;

import com.practica.crudpruebas.clean.producto.usecases.boundary.ProductoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarProductosUseCase {

    private final ProductoRepository productoRepository;

    public ListarProductosUseCase(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ListarProductosOutput> execute() {
        return productoRepository.listarTodos().stream()
                .map(p -> new ListarProductosOutput(p.getId(), p.getNombre(), p.getPrecio(), p.getStock()))
                .toList();
    }
}
