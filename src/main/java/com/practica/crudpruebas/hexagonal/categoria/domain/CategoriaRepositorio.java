package com.practica.crudpruebas.hexagonal.categoria.domain;

import java.util.List;
import java.util.Optional;

// PUERTO: el dominio declara QUE necesita (guardar, buscar), pero no sabe
// (ni le importa) si por detras hay JPA, MongoDB, o un archivo de texto.
// La implementacion real vive en infrastructure/persistence.
public interface CategoriaRepositorio {

    Categoria guardar(Categoria categoria);

    Optional<Categoria> buscarPorId(Long id);

    List<Categoria> buscarTodas();
}
