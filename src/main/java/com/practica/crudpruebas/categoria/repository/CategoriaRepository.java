package com.practica.crudpruebas.categoria.repository;

import com.practica.crudpruebas.categoria.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
