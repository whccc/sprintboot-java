package com.practica.crudpruebas.clean.producto.entities;

// La capa mas interna de Clean Architecture ("Enterprise Business Rules"):
// objeto de negocio puro, sin ninguna dependencia de framework -- mismo
// espiritu que domain/ en Hexagonal. La dejamos SIN relacion a Categoria
// ni Etiqueta a proposito: el foco de esta carpeta es la estructura de
// Use Cases (lo nuevo de Clean Architecture), no repetir el grafo
// relacional que ya viste completo en hexagonal/producto/.
public class Producto {

    private Long id;
    private String nombre;
    private double precio;
    private int stock;

    public Producto(Long id, String nombre, double precio, int stock) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public static Producto nuevo(String nombre, double precio, int stock) {
        return new Producto(null, nombre, precio, stock);
    }

    public void descontarStock(int cantidad) {
        if (cantidad > this.stock) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        this.stock -= cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }
}
