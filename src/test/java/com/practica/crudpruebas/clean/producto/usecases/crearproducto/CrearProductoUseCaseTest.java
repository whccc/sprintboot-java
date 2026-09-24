package com.practica.crudpruebas.clean.producto.usecases.crearproducto;

import com.practica.crudpruebas.clean.producto.entities.Producto;
import com.practica.crudpruebas.clean.producto.usecases.boundary.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

// Sin @SpringBootTest, sin @WebMvcTest, sin ningun slice -- el Use Case
// solo depende de UNA interfaz (el boundary), asi que un @Mock alcanza.
// Esta es la ventaja concreta de "un caso de uso, una clase, una
// dependencia": el test no necesita saber nada de JPA, HTTP ni Spring.
@ExtendWith(MockitoExtension.class)
class CrearProductoUseCaseTest {

    @Mock
    private ProductoRepository productoRepository;

    @Test
    void execute_guardaElProductoYDevuelveElOutputConElIdAsignado() {
        CrearProductoUseCase useCase = new CrearProductoUseCase(productoRepository);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        when(productoRepository.guardar(any())).thenAnswer(invocacion -> {
            Producto recibido = invocacion.getArgument(0);
            return new Producto(1L, recibido.getNombre(), recibido.getPrecio(), recibido.getStock());
        });

        CrearProductoOutput output = useCase.execute(new CrearProductoInput("Teclado", 45.0, 10));

        verify(productoRepository).guardar(captor.capture());
        assertThat(captor.getValue().getNombre()).isEqualTo("Teclado");
        assertThat(output.id()).isEqualTo(1L);
        assertThat(output.nombre()).isEqualTo("Teclado");
        assertThat(output.precio()).isEqualTo(45.0);
        assertThat(output.stock()).isEqualTo(10);
    }

    @Test
    void execute_rechazaPrecioInvalido_antesDeTocarElRepositorio() {
        CrearProductoUseCase useCase = new CrearProductoUseCase(productoRepository);

        // La validacion de negocio vive en la Entity (Producto.nuevo()), no
        // en el Use Case -- por eso ni hace falta stubear
        // productoRepository.guardar() aca: explota antes de llegar a el.
        assertThatThrownBy(() -> useCase.execute(new CrearProductoInput("Teclado", -5.0, 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");

        verifyNoInteractions(productoRepository);
    }
}
