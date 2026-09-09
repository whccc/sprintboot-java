package com.practica.crudpruebas.hexagonal.etiqueta.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EtiquetaJpaRepository extends JpaRepository<EtiquetaEntity, Long> {
}
