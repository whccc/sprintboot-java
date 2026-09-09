package com.practica.crudpruebas.hexagonal.etiqueta.infrastructure.persistence;

import com.practica.crudpruebas.hexagonal.etiqueta.domain.Etiqueta;
import com.practica.crudpruebas.hexagonal.etiqueta.domain.EtiquetaRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class EtiquetaRepositorioImpl implements EtiquetaRepositorio {

    private final EtiquetaJpaRepository jpaRepository;

    public EtiquetaRepositorioImpl(EtiquetaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Etiqueta guardar(Etiqueta etiqueta) {
        EtiquetaEntity entity = new EtiquetaEntity(etiqueta.getId(), etiqueta.getNombre());
        return aDominio(jpaRepository.save(entity));
    }

    @Override
    public Optional<Etiqueta> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(EtiquetaRepositorioImpl::aDominio);
    }

    @Override
    public List<Etiqueta> buscarTodas() {
        return jpaRepository.findAll().stream().map(EtiquetaRepositorioImpl::aDominio).toList();
    }

    @Override
    public List<Etiqueta> buscarPorIds(Set<Long> ids) {
        return jpaRepository.findAllById(ids).stream().map(EtiquetaRepositorioImpl::aDominio).toList();
    }

    private static Etiqueta aDominio(EtiquetaEntity entity) {
        return new Etiqueta(entity.getId(), entity.getNombre());
    }
}
