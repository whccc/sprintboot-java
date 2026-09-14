package com.practica.crudpruebas.hexagonal.producto.presentacion;

import com.practica.crudpruebas.hexagonal.producto.application.ProductoRequest;
import com.practica.crudpruebas.hexagonal.producto.application.ProductoResponse;
import com.practica.crudpruebas.hexagonal.producto.application.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Test SIN Spring, sin base de datos -- prueba unicamente que el adaptador
// CLI traduce bien sus argumentos y llama al MISMO ProductoService que
// usaria ProductoController. No necesita Oracle para verificar esto.
@ExtendWith(MockitoExtension.class)
class ProductoCliRunnerTest {

    @Mock
    private ProductoService productoService;

    @Mock
    private ApplicationArguments args;

    @Test
    void run_noHaceNada_siNoVieneElFlagCrearProducto() {
        when(args.containsOption("crear-producto")).thenReturn(false);

        new ProductoCliRunner(productoService).run(args);

        verify(productoService, never()).crear(any());
    }

    @Test
    void run_parseaLosArgumentos_yLlamaAlMismoProductoServiceQueUsaElController() {
        when(args.containsOption("crear-producto")).thenReturn(true);
        when(args.getOptionValues("crear-producto")).thenReturn(List.of("Teclado,50.0,10"));
        when(productoService.crear(any())).thenReturn(
                new ProductoResponse(1L, "Teclado", 50.0, 10, null, List.of()));

        new ProductoCliRunner(productoService).run(args);

        // La prueba clave: se llamo exactamente al mismo metodo, con el
        // mismo tipo de DTO (ProductoRequest), que usa ProductoController.crear().
        verify(productoService).crear(new ProductoRequest("Teclado", 50.0, 10, null));
    }
}
