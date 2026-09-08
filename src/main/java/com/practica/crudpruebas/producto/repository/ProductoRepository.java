package com.practica.crudpruebas.producto.repository;

import com.practica.crudpruebas.producto.dto.ProductoResumen;
import com.practica.crudpruebas.producto.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // 1) Query derivada por nombre de metodo (simple)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // 2) Query derivada navegando una relacion (Producto -> Categoria).
    //    Internamente SI genera un JOIN, pero solo para el WHERE:
    //    la categoria de cada producto sigue sin "traerse" (queda LAZY).
    //    Si despues se llama getCategoria().getNombre() por cada producto,
    //    se dispara 1 query extra por producto = problema N+1.
    List<Producto> findByCategoriaNombre(String nombreCategoria);

    // 3) La misma busqueda, pero con JOIN FETCH: le decimos a Hibernate
    //    "trae el producto Y su categoria en la MISMA query". Con esto no
    //    hay N+1 -- todo llega en 1 sola consulta SQL con INNER JOIN.
    @Query("SELECT p FROM Producto p JOIN FETCH p.categoria c WHERE c.nombre = :nombreCategoria")
    List<Producto> buscarPorCategoriaConFetch(@Param("nombreCategoria") String nombreCategoria);

    // 4) Paginacion + ordenamiento: Spring Data maneja el LIMIT/OFFSET solo.
    //    Pageable ya trae page, size y sort -- se arma desde el Controller.
    Page<Producto> findByPrecioLessThanEqual(double precioMaximo, Pageable pageable);

    // 5) JOIN EXPLICITO en JPQL, sin depender del nombre del metodo.
    //    Esto es lo que en EF Core seria escribir un LINQ con .Join() a mano,
    //    o un query() con varios .Where() encadenados. Uno mismo controla
    //    exactamente que tablas entran y como se relacionan.
    //    JPQL habla de ENTIDADES y sus campos (Producto, categoria, etiquetas),
    //    no de tablas ni columnas fisicas -- Hibernate traduce eso a SQL real.
    @Query("""
            SELECT DISTINCT p FROM Producto p
            JOIN p.categoria c
            JOIN p.etiquetas e
            WHERE c.nombre = :categoria
            AND e.nombre = :etiqueta
            """)
    List<Producto> buscarPorCategoriaYEtiqueta(@Param("categoria") String categoria,
                                                @Param("etiqueta") String etiqueta);

    // 6) La MISMA idea, pero en SQL nativo -- para cuando necesitas algo que
    //    JPQL no puede expresar (funciones propias del motor, CTEs, window
    //    functions, etc). Aca ya hablas de tablas y columnas reales.
    @Query(value = """
            SELECT DISTINCT p.* FROM producto p
            INNER JOIN categoria c ON c.id = p.categoria_id
            INNER JOIN producto_etiqueta pe ON pe.producto_id = p.id
            INNER JOIN etiqueta e ON e.id = pe.etiqueta_id
            WHERE c.nombre = :categoria
            AND e.nombre = :etiqueta
            """, nativeQuery = true)
    List<Producto> buscarPorCategoriaYEtiquetaNativo(@Param("categoria") String categoria,
                                                       @Param("etiqueta") String etiqueta);

    // 7) Projection: JOIN + SELECT de columnas puntuales (no la entidad
    //    completa). Equivalente a "select new { p.Nombre, c.Nombre }" en LINQ.
    @Query("""
            SELECT p.nombre AS nombre, p.precio AS precio, c.nombre AS categoriaNombre
            FROM Producto p JOIN p.categoria c
            """)
    List<ProductoResumen> resumenDeProductos();
}
