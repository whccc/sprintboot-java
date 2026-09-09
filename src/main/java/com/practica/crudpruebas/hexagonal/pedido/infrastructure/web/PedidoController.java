package com.practica.crudpruebas.hexagonal.pedido.infrastructure.web;

import com.practica.crudpruebas.hexagonal.pedido.application.PedidoRequest;
import com.practica.crudpruebas.hexagonal.pedido.application.PedidoResponse;
import com.practica.crudpruebas.hexagonal.pedido.application.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hexagonal/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crear(@Valid @RequestBody PedidoRequest request) {
        return pedidoService.crearPedido(request);
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }
}
