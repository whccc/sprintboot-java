package com.practica.crudpruebas.hexagonal.pedido.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PedidoRequest(
        @NotEmpty(message = "El pedido debe tener al menos una linea")
        @Valid
        List<ItemPedidoRequest> items
) {
}
