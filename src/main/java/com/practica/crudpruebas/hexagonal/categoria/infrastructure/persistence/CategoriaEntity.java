package com.practica.crudpruebas.hexagonal.categoria.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Esta clase SI tiene anotaciones de framework -- es su unico trabajo:
// describir como se guarda una categoria en la tabla "categoria_hex".
// Tabla separada de "categoria" (la del feature original) para que las 2
// versiones del proyecto sean 100% independientes entre si.
@Entity
@Table(name = "categoria_hex")
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    public CategoriaEntity() {
    }

    public CategoriaEntity(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
}
