package com.practica.crudpruebas.hexagonal.producto.domain;

// Record de dominio PURO -- a diferencia de ProductoResumen en el feature
// original (que era una interfaz de projection de Spring Data, atada al
// framework), este es un record de Java comun, construido directo por
// JPQL con "SELECT new ...(...)" en el adaptador de persistencia.
public record ResumenProducto(String nombre, double precio, String categoriaNombre) {
}
