package com.practica.crudpruebas.hexagonal.pedido.application;

import java.util.List;

public record PedidoResponse(
        Long id,
        List<ItemPedidoResponse> items,
        double total,
        boolean descuentoAplicado
) {
}
