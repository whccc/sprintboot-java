package com.practica.crudpruebas.hexagonal.pedido.domain;

import java.util.Optional;

public interface PedidoRepositorio {

    Pedido guardar(Pedido pedido);

    Optional<Pedido> buscarPorId(Long id);
}
