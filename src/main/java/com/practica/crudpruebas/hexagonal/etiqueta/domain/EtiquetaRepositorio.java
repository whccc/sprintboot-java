package com.practica.crudpruebas.hexagonal.etiqueta.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EtiquetaRepositorio {

    Etiqueta guardar(Etiqueta etiqueta);

    Optional<Etiqueta> buscarPorId(Long id);

    List<Etiqueta> buscarTodas();

    // Usado por Producto para resolver un conjunto de ids en objetos de dominio.
    List<Etiqueta> buscarPorIds(Set<Long> ids);
}
