package com.practica.crudpruebas.clean.producto.usecases.buscarproducto;

import com.practica.crudpruebas.clean.producto.entities.Producto;
import com.practica.crudpruebas.clean.producto.entities.ProductoNoEncontradoException;
import com.practica.crudpruebas.clean.producto.usecases.boundary.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarProductoUseCaseTest {

    @Mock
    private ProductoRepository productoRepository;

    @Test
    void execute_devuelveElOutput_cuandoElProductoExiste() {
        BuscarProductoUseCase useCase = new BuscarProductoUseCase(productoRepository);
        when(productoRepository.buscarPorId(1L)).thenReturn(Optional.of(new Producto(1L, "Mouse", 25.0, 5)));

        BuscarProductoOutput output = useCase.execute(1L);

        assertThat(output.nombre()).isEqualTo("Mouse");
        assertThat(output.stock()).isEqualTo(5);
    }

    @Test
    void execute_lanzaProductoNoEncontrado_cuandoElRepositorioDevuelveVacio() {
        BuscarProductoUseCase useCase = new BuscarProductoUseCase(productoRepository);
        when(productoRepository.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(999L))
                .isInstanceOf(ProductoNoEncontradoException.class)
                .hasMessageContaining("999");
    }
}
