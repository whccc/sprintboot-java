package com.practica.crudpruebas.hexagonal.producto.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleProductoJpaRepository extends JpaRepository<DetalleProductoEntity, Long> {
}
