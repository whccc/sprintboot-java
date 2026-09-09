package com.practica.crudpruebas.hexagonal.producto.domain;

import java.util.HashSet;
import java.util.Set;

// Objeto de DOMINIO. Nota clave respecto a la version "por feature": aca
// Producto NO referencia a Categoria ni a Etiqueta como objetos completos --
// solo guarda sus IDs. Es la practica estandar en DDD: un agregado referencia
// a otro agregado por identidad, no por objeto -- evita que el dominio
// dependa de como esta armado el grafo de persistencia de otro feature.
//
// El constructor valida invariantes de negocio -- esto es lo que distingue
// un "modelo rico" de la version anemica (solo getters/setters) que
// teniamos en el feature original.
public class Producto {

    private Long id;
    private String nombre;
    private double precio;
    private int stock;
    private Long categoriaId;
    private Set<Long> etiquetaIds;

    public Producto(Long id, String nombre, double precio, int stock, Long categoriaId, Set<Long> etiquetaIds) {
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
        this.categoriaId = categoriaId;
        this.etiquetaIds = etiquetaIds != null ? new HashSet<>(etiquetaIds) : new HashSet<>();
    }

    public static Producto nuevo(String nombre, double precio, int stock, Long categoriaId) {
        return new Producto(null, nombre, precio, stock, categoriaId, new HashSet<>());
    }

    // --- Comportamiento de negocio (no solo getters/setters) ---

    public void asignarCategoria(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public void agregarEtiqueta(Long etiquetaId) {
        this.etiquetaIds.add(etiquetaId);
    }

    public void actualizarDatos(String nombre, double precio, int stock) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public void descontarStock(int cantidad) {
        if (cantidad > this.stock) {
            throw new IllegalArgumentException("Stock insuficiente en el producto origen");
        }
        this.stock -= cantidad;
    }

    public void aumentarStock(int cantidad) {
        this.stock += cantidad;
    }

    // --- Getters (sin setters sueltos -- los cambios pasan por los metodos
    // de comportamiento de arriba, no por un setStock() generico) ---

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

    public Long getCategoriaId() {
        return categoriaId;
    }

    public Set<Long> getEtiquetaIds() {
        return etiquetaIds;
    }
}
