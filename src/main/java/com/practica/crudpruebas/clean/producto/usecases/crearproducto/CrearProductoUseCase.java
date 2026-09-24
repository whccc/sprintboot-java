package com.practica.crudpruebas.clean.producto.usecases.crearproducto;

import com.practica.crudpruebas.clean.producto.entities.Producto;
import com.practica.crudpruebas.clean.producto.usecases.boundary.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// UN caso de uso, UNA clase, UNA responsabilidad -- a diferencia de
// ProductoService (Hexagonal), que agrupa crear/buscar/listar/eliminar
// en un solo archivo. Esta clase inyecta SOLO lo que este caso puntual
// necesita (el ProductoRepository) -- nada de otro caso de uso que no
// le compete.
@Component
public class CrearProductoUseCase {

    // Logger por CLASE (no uno global compartido) -- el nombre del logger
    // ES el nombre completo de la clase, asi el "%logger" del pattern de
    // abajo te dice exacto de donde salio cada linea. Convencion: siempre
    // "private static final", nunca una instancia por objeto.
    private static final Logger log = LoggerFactory.getLogger(CrearProductoUseCase.class);

    private final ProductoRepository productoRepository;

    public CrearProductoUseCase(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public CrearProductoOutput execute(CrearProductoInput input) {
        // Placeholders "{}" en vez de concatenar con "+" -- si el nivel INFO
        // estuviera apagado, SLF4J ni arma el String (evita el costo de
        // formatear un log que nadie va a ver). Nunca loguees el objeto
        // completo si tuviera datos sensibles -- aca no aplica, pero es la
        // misma regla que "nunca loguees el password" en AuthController.
        log.info("Creando producto: nombre={}, precio={}, stock={}", input.nombre(), input.precio(), input.stock());

        Producto producto = Producto.nuevo(input.nombre(), input.precio(), input.stock());
        Producto guardado = productoRepository.guardar(producto);

        log.info("Producto creado: id={}", guardado.getId());
        return new CrearProductoOutput(guardado.getId(), guardado.getNombre(), guardado.getPrecio(), guardado.getStock());
    }
}
