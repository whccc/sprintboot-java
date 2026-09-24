package com.practica.crudpruebas.clean.producto.adapters.gateway;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {
}
