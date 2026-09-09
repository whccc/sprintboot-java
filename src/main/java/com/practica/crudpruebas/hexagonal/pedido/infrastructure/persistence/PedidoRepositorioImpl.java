package com.practica.crudpruebas.hexagonal.pedido.infrastructure.persistence;

import com.practica.crudpruebas.hexagonal.pedido.domain.ItemPedido;
import com.practica.crudpruebas.hexagonal.pedido.domain.Pedido;
import com.practica.crudpruebas.hexagonal.pedido.domain.PedidoRepositorio;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PedidoRepositorioImpl implements PedidoRepositorio {

    private final PedidoJpaRepository jpaRepository;

    public PedidoRepositorioImpl(PedidoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        PedidoEntity entity = new PedidoEntity();
        entity.setId(pedido.getId());
        entity.setFecha(pedido.getFecha());
        entity.setItems(pedido.getItems().stream()
                .map(item -> new ItemPedidoEmbeddable(
                        item.getProductoId(), item.getNombreProducto(),
                        item.getCantidad(), item.getPrecioUnitario()))
                .toList());

        PedidoEntity guardado = jpaRepository.save(entity);
        return aDominio(guardado);
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(PedidoRepositorioImpl::aDominio);
    }

    private static Pedido aDominio(PedidoEntity entity) {
        var items = entity.getItems().stream()
                .map(item -> new ItemPedido(
                        item.getProductoId(), item.getNombreProducto(),
                        item.getCantidad(), item.getPrecioUnitario()))
                .toList();
        return new Pedido(entity.getId(), items, entity.getFecha());
    }
}
