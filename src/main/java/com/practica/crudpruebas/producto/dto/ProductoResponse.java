package com.practica.crudpruebas.producto.dto;

import com.practica.crudpruebas.etiqueta.model.Etiqueta;
import com.practica.crudpruebas.producto.model.Producto;

import java.util.List;

// DTO de SALIDA: lo que el cliente recibe. Nunca es la entidad JPA directa.
//
// Ventajas concretas que ya vimos en este proyecto:
// 1. Nunca se filtran detalles internos de Hibernate (el problema del
//    "hibernateLazyInitializer" que parcheamos antes con @JsonIgnoreProperties
//    -- con DTOs ese parche ya no hace falta, se soluciona de raiz).
// 2. Solo viajan los campos que decidimos exponer (categoriaNombre, no el
//    objeto Categoria completo con su propia lista de productos).
// 3. Cambiar la entidad (agregar una columna interna, renombrar un campo)
//    no rompe el contrato de la API mientras no toquemos este DTO.
public record ProductoResponse(
        Long id,
        String nombre,
        double precio,
        int stock,
        String categoriaNombre,
        List<String> etiquetas
) {

    // Factory estatico: conversion Entidad -> DTO en un solo lugar.
    // Se llama DENTRO del Service, mientras la sesion de Hibernate sigue
    // abierta -- por eso es seguro leer producto.getCategoria().getNombre()
    // aunque la relacion sea LAZY.
    public static ProductoResponse desde(Producto producto) {
        String categoriaNombre = producto.getCategoria() != null
                ? producto.getCategoria().getNombre()
                : null;

        List<String> nombresEtiquetas = producto.getEtiquetas().stream()
                .map(Etiqueta::getNombre)
                .toList();

        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                categoriaNombre,
                nombresEtiquetas
        );
    }
}
