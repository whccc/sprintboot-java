package com.practica.crudpruebas.hexagonal.pedido.domain;

// Value object: no tiene identidad propia ni ciclo de vida independiente --
// no existe "buscar un ItemPedido por id" en ningun lado, solo vive DENTRO
// de un Pedido. Por eso Pedido no necesita un puerto/repositorio separado
// para sus items.
public class ItemPedido {

    private final Long productoId;
    private final String nombreProducto;
    private final int cantidad;
    private final double precioUnitario;

    public ItemPedido(Long productoId, String nombreProducto, int cantidad, double precioUnitario) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public double subtotal() {
        return cantidad * precioUnitario;
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
