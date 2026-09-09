package com.practica.crudpruebas.hexagonal.producto.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

// PUERTO de Producto. Nota: usar Page<T>/Pageable aca es una concesion
// pragmatica -- son tipos de Spring Data, no 100% "dominio puro". Un purista
// definiria su propio "Pagina<T>" para no acoplar el dominio a Spring. Para
// un proyecto de este tamaño, ese nivel de pureza es mas ceremonia que
// beneficio real -- es una decision consciente, no un descuido.
public interface ProductoRepositorio {

    Producto guardar(Producto producto);

    Optional<Producto> buscarPorId(Long id);

    List<Producto> buscarTodos();

    List<Producto> buscarPorNombre(String nombre);

    void eliminar(Long id);

    // Equivalente al JOIN FETCH que vimos en el feature original: trae el
    // producto Y su categoria en una sola consulta, sin problema N+1.
    List<Producto> buscarPorCategoriaNombre(String nombreCategoria);

    Page<Producto> buscarPorPrecioMaximo(double precioMaximo, Pageable pageable);

    List<ResumenProducto> resumenDeProductos();
}
