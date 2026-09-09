package com.practica.crudpruebas.hexagonal.categoria.infrastructure.persistence;

import com.practica.crudpruebas.hexagonal.categoria.domain.Categoria;
import com.practica.crudpruebas.hexagonal.categoria.domain.CategoriaRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// ADAPTADOR: implementa el puerto del dominio usando JPA por detras.
// Esta clase es la UNICA que sabe traducir CategoriaEntity <-> Categoria.
@Component
public class CategoriaRepositorioImpl implements CategoriaRepositorio {

    private final CategoriaJpaRepository jpaRepository;

    public CategoriaRepositorioImpl(CategoriaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        CategoriaEntity entity = new CategoriaEntity(categoria.getId(), categoria.getNombre());
        CategoriaEntity guardada = jpaRepository.save(entity);
        return aDominio(guardada);
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(CategoriaRepositorioImpl::aDominio);
    }

    @Override
    public List<Categoria> buscarTodas() {
        return jpaRepository.findAll().stream()
                .map(CategoriaRepositorioImpl::aDominio)
                .toList();
    }

    private static Categoria aDominio(CategoriaEntity entity) {
        return new Categoria(entity.getId(), entity.getNombre());
    }
}
