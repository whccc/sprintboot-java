package com.practica.crudpruebas.producto.repository;

import com.practica.crudpruebas.producto.model.DetalleProducto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleProductoRepository extends JpaRepository<DetalleProducto, Long> {
}
