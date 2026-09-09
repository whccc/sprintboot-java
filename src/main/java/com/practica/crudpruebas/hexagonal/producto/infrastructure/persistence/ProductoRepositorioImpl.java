package com.practica.crudpruebas.hexagonal.producto.infrastructure.persistence;

import com.practica.crudpruebas.hexagonal.categoria.infrastructure.persistence.CategoriaEntity;
import com.practica.crudpruebas.hexagonal.categoria.infrastructure.persistence.CategoriaJpaRepository;
import com.practica.crudpruebas.hexagonal.etiqueta.infrastructure.persistence.EtiquetaEntity;
import com.practica.crudpruebas.hexagonal.etiqueta.infrastructure.persistence.EtiquetaJpaRepository;
import com.practica.crudpruebas.hexagonal.producto.domain.Producto;
import com.practica.crudpruebas.hexagonal.producto.domain.ProductoRepositorio;
import com.practica.crudpruebas.hexagonal.producto.domain.ResumenProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// Este adaptador es el UNICO lugar de todo el proyecto hexagonal que sabe
// que "categoriaId" y "etiquetaIds" (simples Long/Set<Long> en el dominio)
// se traducen a relaciones JPA reales (@ManyToOne, @ManyToMany) contra
// ProductoEntity. El dominio nunca ve un CategoriaEntity ni un EtiquetaEntity.
@Component
public class ProductoRepositorioImpl implements ProductoRepositorio {

    private final ProductoJpaRepository productoJpaRepository;
    private final CategoriaJpaRepository categoriaJpaRepository;
    private final EtiquetaJpaRepository etiquetaJpaRepository;

    public ProductoRepositorioImpl(ProductoJpaRepository productoJpaRepository,
                                    CategoriaJpaRepository categoriaJpaRepository,
                                    EtiquetaJpaRepository etiquetaJpaRepository) {
        this.productoJpaRepository = productoJpaRepository;
        this.categoriaJpaRepository = categoriaJpaRepository;
        this.etiquetaJpaRepository = etiquetaJpaRepository;
    }

    @Override
    @Transactional
    public Producto guardar(Producto producto) {
        ProductoEntity entity = producto.getId() != null
                ? productoJpaRepository.findById(producto.getId()).orElseGet(ProductoEntity::new)
                : new ProductoEntity();

        entity.setNombre(producto.getNombre());
        entity.setPrecio(producto.getPrecio());
        entity.setStock(producto.getStock());

        if (producto.getCategoriaId() != null) {
            CategoriaEntity categoria = categoriaJpaRepository.findById(producto.getCategoriaId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No existe la categoria " + producto.getCategoriaId()));
            entity.setCategoria(categoria);
        } else {
            entity.setCategoria(null);
        }

        if (!producto.getEtiquetaIds().isEmpty()) {
            Set<EtiquetaEntity> etiquetas = Set.copyOf(etiquetaJpaRepository.findAllById(producto.getEtiquetaIds()));
            entity.setEtiquetas(etiquetas);
        }

        return aDominio(productoJpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        return productoJpaRepository.findById(id).map(ProductoRepositorioImpl::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarTodos() {
        return productoJpaRepository.findAll().stream().map(ProductoRepositorioImpl::aDominio).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoJpaRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(ProductoRepositorioImpl::aDominio)
                .toList();
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        productoJpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoriaNombre(String nombreCategoria) {
        return productoJpaRepository.buscarPorCategoriaConFetch(nombreCategoria).stream()
                .map(ProductoRepositorioImpl::aDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Producto> buscarPorPrecioMaximo(double precioMaximo, Pageable pageable) {
        return productoJpaRepository.findByPrecioLessThanEqual(precioMaximo, pageable)
                .map(ProductoRepositorioImpl::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumenProducto> resumenDeProductos() {
        return productoJpaRepository.resumenDeProductos();
    }

    private static Producto aDominio(ProductoEntity entity) {
        Long categoriaId = entity.getCategoria() != null ? entity.getCategoria().getId() : null;
        Set<Long> etiquetaIds = entity.getEtiquetas().stream()
                .map(EtiquetaEntity::getId)
                .collect(Collectors.toSet());
        return new Producto(entity.getId(), entity.getNombre(), entity.getPrecio(), entity.getStock(),
                categoriaId, etiquetaIds);
    }
}
