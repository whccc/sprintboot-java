package com.practica.crudpruebas.hexagonal.producto.infrastructure.persistence;

import com.practica.crudpruebas.hexagonal.producto.domain.DetalleProducto;
import com.practica.crudpruebas.hexagonal.producto.domain.DetalleProductoRepositorio;
import org.springframework.stereotype.Component;

@Component
public class DetalleProductoRepositorioImpl implements DetalleProductoRepositorio {

    private final DetalleProductoJpaRepository jpaRepository;
    private final ProductoJpaRepository productoJpaRepository;

    public DetalleProductoRepositorioImpl(DetalleProductoJpaRepository jpaRepository,
                                           ProductoJpaRepository productoJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.productoJpaRepository = productoJpaRepository;
    }

    @Override
    public DetalleProducto guardar(DetalleProducto detalle) {
        ProductoEntity producto = productoJpaRepository.findById(detalle.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("No existe el producto " + detalle.getProductoId()));

        DetalleProductoEntity entity = new DetalleProductoEntity();
        entity.setDescripcionLarga(detalle.getDescripcionLarga());
        entity.setGarantiaMeses(detalle.getGarantiaMeses());
        entity.setProducto(producto);

        DetalleProductoEntity guardado = jpaRepository.save(entity);
        return new DetalleProducto(guardado.getId(), guardado.getDescripcionLarga(),
                guardado.getGarantiaMeses(), producto.getId());
    }
}
