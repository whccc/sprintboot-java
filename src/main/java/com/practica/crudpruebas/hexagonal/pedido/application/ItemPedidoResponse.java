package com.practica.crudpruebas.hexagonal.pedido.application;

public record ItemPedidoResponse(
        Long productoId,
        String nombreProducto,
        int cantidad,
        double precioUnitario,
        double subtotal
) {
}
