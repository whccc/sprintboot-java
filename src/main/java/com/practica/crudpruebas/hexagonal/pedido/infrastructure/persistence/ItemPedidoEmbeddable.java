package com.practica.crudpruebas.hexagonal.pedido.infrastructure.persistence;

import jakarta.persistence.Embeddable;

// @Embeddable: no es una tabla con su propia identidad, es un "pedazo" que
// se guarda incrustado en las filas de otra tabla (pedido_item_hex, ligada
// a pedido_hex por FK, pero SIN tener su propio @Entity/@Id). Refleja
// exactamente que ItemPedido (dominio) es un value object, no un agregado.
@Embeddable
public class ItemPedidoEmbeddable {

    private Long productoId;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;

    public ItemPedidoEmbeddable() {
    }

    public ItemPedidoEmbeddable(Long productoId, String nombreProducto, int cantidad, double precioUnitario) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Long getProductoId() {
        return productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }
}
