package com.practica.crudpruebas.hexagonal.categoria.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

// Este SI es "el Spring Data de siempre" -- trabaja con la Entity, no con
// el dominio. Nadie fuera de "infrastructure.persistence" deberia importar
// esta interfaz directamente.
public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, Long> {
}
