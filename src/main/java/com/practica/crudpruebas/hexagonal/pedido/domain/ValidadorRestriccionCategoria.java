package com.practica.crudpruebas.hexagonal.pedido.domain;

import com.practica.crudpruebas.hexagonal.categoria.domain.Categoria;
import com.practica.crudpruebas.hexagonal.producto.domain.Producto;

// SERVICIO DE DOMINIO: la regla "categoria restringida -> maximo 3 por
// linea" no le pertenece a Producto (Producto no sabe nada de Pedido) ni a
// Categoria (Categoria no sabe nada de limites de compra) -- pertenece a la
// RELACION entre ambos, en el contexto de armar un pedido. Por eso no vive
// como metodo de ninguna de las 2 entidades.
//
// A PROPOSITO no tiene @Component: el dominio tiene CERO anotaciones de
// framework, sin excepciones -- igual que Producto/Categoria/Etiqueta. Como
// no depende de nada (sin Repositorio, sin estado), no necesita que Spring
// lo administre -- se instancia con "new" directo en quien lo use.
// Por eso se puede testear asi, sin ningun mock:
//
//   new ValidadorRestriccionCategoria().validar(producto, categoria, 5);
public class ValidadorRestriccionCategoria {

    private static final String NOMBRE_CATEGORIA_RESTRINGIDA = "Restringido";
    private static final int MAXIMO_POR_LINEA_SI_ES_RESTRINGIDA = 3;

    public void validar(Producto producto, Categoria categoria, int cantidadPedida) {
        if (categoria == null || !esRestringida(categoria)) {
            return;
        }
        if (cantidadPedida > MAXIMO_POR_LINEA_SI_ES_RESTRINGIDA) {
            throw new IllegalArgumentException(
                    "No se pueden pedir mas de " + MAXIMO_POR_LINEA_SI_ES_RESTRINGIDA
                            + " unidades de \"" + producto.getNombre()
                            + "\" (categoria restringida)");
        }
    }

    private boolean esRestringida(Categoria categoria) {
        return NOMBRE_CATEGORIA_RESTRINGIDA.equalsIgnoreCase(categoria.getNombre());
    }
}
