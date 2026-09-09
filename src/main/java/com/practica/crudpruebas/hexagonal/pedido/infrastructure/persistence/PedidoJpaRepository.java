package com.practica.crudpruebas.hexagonal.pedido.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, Long> {
}
