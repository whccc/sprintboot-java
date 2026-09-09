package com.practica.crudpruebas.hexagonal.pedido.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Pedido es el AGREGADO RAIZ: la unica puerta de entrada para modificar sus
// items. Nadie agrega un ItemPedido "directo" -- siempre pasa por
// pedido.agregarItem(...), que es donde vive la regla del descuento.
//
// Fijate que esta clase NO sabe nada de Producto ni de Categoria -- ni
// siquiera conoce esos tipos. Solo recibe los datos que ya fueron
// validados y resueltos por el Service (nombreProducto, precioUnitario).
public class Pedido {

    private Long id;
    private final List<ItemPedido> items = new ArrayList<>();
    private final LocalDateTime fecha;

    private static final double UMBRAL_DESCUENTO = 1000.0;
    private static final double PORCENTAJE_DESCUENTO = 0.10;

    public Pedido() {
        this.fecha = LocalDateTime.now();
    }

    public Pedido(Long id, List<ItemPedido> items, LocalDateTime fecha) {
        this.id = id;
        this.items.addAll(items);
        this.fecha = fecha;
    }

    public void agregarItem(Long productoId, String nombreProducto, int cantidad, double precioUnitario) {
        items.add(new ItemPedido(productoId, nombreProducto, cantidad, precioUnitario));
    }

    // Regla de negocio que SI vive aca (no en el Service): el descuento solo
    // depende de datos que el propio Pedido ya tiene (sus items) -- no
    // necesita consultar nada afuera.
    public double calcularTotal() {
        double subtotal = items.stream().mapToDouble(ItemPedido::subtotal).sum();
        return subtotal > UMBRAL_DESCUENTO ? subtotal * (1 - PORCENTAJE_DESCUENTO) : subtotal;
    }

    public boolean tieneDescuentoAplicado() {
        double subtotalSinDescuento = items.stream().mapToDouble(ItemPedido::subtotal).sum();
        return subtotalSinDescuento > UMBRAL_DESCUENTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemPedido> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
