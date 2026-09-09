package com.practica.crudpruebas.hexagonal.producto.infrastructure.persistence;

import com.practica.crudpruebas.hexagonal.categoria.infrastructure.persistence.CategoriaEntity;
import com.practica.crudpruebas.hexagonal.etiqueta.infrastructure.persistence.EtiquetaEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "producto_hex")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double precio;
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaEntity categoria;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "producto_etiqueta_hex",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    private Set<EtiquetaEntity> etiquetas = new HashSet<>();

    public ProductoEntity() {
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

    public CategoriaEntity getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaEntity categoria) {
        this.categoria = categoria;
    }

    public Set<EtiquetaEntity> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(Set<EtiquetaEntity> etiquetas) {
        this.etiquetas = etiquetas;
    }
}
