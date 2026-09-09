package com.practica.crudpruebas.hexagonal.pedido.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido_hex")
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    @ElementCollection
    @CollectionTable(name = "pedido_item_hex", joinColumns = @JoinColumn(name = "pedido_id"))
    private List<ItemPedidoEmbeddable> items = new ArrayList<>();

    public PedidoEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public List<ItemPedidoEmbeddable> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoEmbeddable> items) {
        this.items = items;
    }
}
