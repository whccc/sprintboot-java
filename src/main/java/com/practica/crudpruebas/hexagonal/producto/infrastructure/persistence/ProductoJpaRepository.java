package com.practica.crudpruebas.hexagonal.producto.infrastructure.persistence;

import com.practica.crudpruebas.hexagonal.producto.domain.ResumenProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {

    List<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT p FROM ProductoEntity p JOIN FETCH p.categoria c WHERE c.nombre = :nombreCategoria")
    List<ProductoEntity> buscarPorCategoriaConFetch(@Param("nombreCategoria") String nombreCategoria);

    Page<ProductoEntity> findByPrecioLessThanEqual(double precioMaximo, Pageable pageable);

    // JPQL con "new" construye el RECORD DE DOMINIO directo -- no hace falta
    // una interfaz de projection de Spring Data como en el feature original.
    @Query("""
            SELECT new com.practica.crudpruebas.hexagonal.producto.domain.ResumenProducto(p.nombre, p.precio, c.nombre)
            FROM ProductoEntity p JOIN p.categoria c
            """)
    List<ResumenProducto> resumenDeProductos();
}
