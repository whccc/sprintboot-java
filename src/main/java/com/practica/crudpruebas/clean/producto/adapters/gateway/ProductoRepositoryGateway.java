package com.practica.crudpruebas.clean.producto.adapters.gateway;

import com.practica.crudpruebas.clean.producto.entities.Producto;
import com.practica.crudpruebas.clean.producto.usecases.boundary.ProductoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// El "Gateway" -- misma responsabilidad que un *RepositorioImpl en
// Hexagonal (traduce Entity <-> objeto de dominio), pero implementando
// una interfaz que vive en usecases/boundary/, no en entities/. Nadie
// mas en esta carpeta conoce ProductoEntity ni JpaRepository -- ni
// siquiera los Use Cases, que solo ven ProductoRepository.
@Component
public class ProductoRepositoryGateway implements ProductoRepository {

    private final ProductoJpaRepository productoJpaRepository;

    public ProductoRepositoryGateway(ProductoJpaRepository productoJpaRepository) {
        this.productoJpaRepository = productoJpaRepository;
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

        return aDominio(productoJpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        return productoJpaRepository.findById(id).map(ProductoRepositoryGateway::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoJpaRepository.findAll().stream().map(ProductoRepositoryGateway::aDominio).toList();
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        productoJpaRepository.deleteById(id);
    }

    private static Producto aDominio(ProductoEntity entity) {
        return new Producto(entity.getId(), entity.getNombre(), entity.getPrecio(), entity.getStock());
    }
}
