package com.practica.crudpruebas.hexagonal.pedido.application;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemPedidoRequest(
        @NotNull Long productoId,
        @Positive int cantidad
) {
}
