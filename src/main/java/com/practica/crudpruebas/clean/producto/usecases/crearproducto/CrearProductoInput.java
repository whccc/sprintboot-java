package com.practica.crudpruebas.clean.producto.usecases.crearproducto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

// El Input es PROPIO de este caso de uso -- no se comparte con
// BuscarProductoUseCase ni con ningun otro (a diferencia de Hexagonal,
// donde un unico ProductoRequest sirve para crear Y actualizar). Es mas
// ceremonia, a cambio de que cambiar este caso de uso nunca rompe otro
// por accidente -- el mismo tradeoff que ya charlamos entre "Service con
// metodos" y "carpeta por caso de uso".
public record CrearProductoInput(
        @NotBlank String nombre,
        @Positive double precio,
        @PositiveOrZero int stock
) {
}
