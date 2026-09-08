package com.practica.crudpruebas.producto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.practica.crudpruebas.categoria.model.Categoria;
import com.practica.crudpruebas.etiqueta.model.Etiqueta;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Positive(message = "El precio debe ser mayor a 0")
    private double precio;

    private int stock;

    // N:1 -- muchos productos pertenecen a una categoria.
    // Producto es el DUENO: tiene la columna categoria_id.
    // FetchType.LAZY = no trae la categoria hasta que alguien la pida
    // explicitamente (evita cargar datos de mas "por si acaso").
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    // El proxy que genera Hibernate para relaciones LAZY expone campos internos
    // (hibernateLazyInitializer, handler) que Jackson intenta serializar por
    // reflexion. Se los decimos que ignore explicitamente.
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Categoria categoria;

    // N:N -- un producto puede tener varias etiquetas, y una etiqueta puede
    // estar en varios productos. Se necesita una tabla intermedia
    // (producto_etiqueta) que Hibernate crea solo con @JoinTable.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "producto_etiqueta",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    private Set<Etiqueta> etiquetas = new HashSet<>();

    public Producto() {
    }

    public Producto(Long id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
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

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Set<Etiqueta> getEtiquetas() {
        return etiquetas;
    }

    public void agregarEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.add(etiqueta);
        etiqueta.getProductos().add(this);
    }
}
