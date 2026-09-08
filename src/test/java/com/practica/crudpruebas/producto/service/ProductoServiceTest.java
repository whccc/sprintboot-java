package com.practica.crudpruebas.producto.service;

import com.practica.crudpruebas.categoria.model.Categoria;
import com.practica.crudpruebas.categoria.repository.CategoriaRepository;
import com.practica.crudpruebas.etiqueta.model.Etiqueta;
import com.practica.crudpruebas.etiqueta.repository.EtiquetaRepository;
import com.practica.crudpruebas.producto.dto.ProductoRequest;
import com.practica.crudpruebas.producto.dto.ProductoResponse;
import com.practica.crudpruebas.producto.exception.ProductoNoEncontradoException;
import com.practica.crudpruebas.producto.model.Producto;
import com.practica.crudpruebas.producto.repository.DetalleProductoRepository;
import com.practica.crudpruebas.producto.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// @ExtendWith(MockitoExtension.class) le dice a JUnit que active Mockito.
// NO hay @SpringBootTest -- no se levanta contexto de Spring, ni base de
// datos, ni nada. Esto corre en milisegundos.
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    // @Mock crea un doble falso -- un objeto que "parece" un ProductoRepository
    // pero no toca ninguna base de datos. Vos le decis que devolver.
    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private EtiquetaRepository etiquetaRepository;

    @Mock
    private DetalleProductoRepository detalleProductoRepository;

    // Mockito arma un ProductoService de verdad, pero le inyecta los 4 mocks
    // de arriba en el constructor -- exactamente lo que hace Spring en runtime,
    // pero sin levantar Spring.
    private ProductoService productoService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        productoService = new ProductoService(
                productoRepository, categoriaRepository, etiquetaRepository, detalleProductoRepository);
    }

    @Test
    void crear_asignaLaCategoria_cuandoVieneEnElRequest() {
        ProductoRequest request = new ProductoRequest("Mouse Gamer", 25.0, 10, 1L);
        Categoria categoria = new Categoria(1L, "Perifericos");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        // save() de un mock, por defecto, devuelve null -- le decimos que
        // devuelva el mismo objeto que recibio (simulando que la BD lo guardo).
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse respuesta = productoService.crear(request);

        assertThat(respuesta.nombre()).isEqualTo("Mouse Gamer");
        assertThat(respuesta.categoriaNombre()).isEqualTo("Perifericos");
    }

    @Test
    void crear_lanzaExcepcion_cuandoLaCategoriaNoExiste() {
        ProductoRequest request = new ProductoRequest("Mouse Gamer", 25.0, 10, 99L);
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.crear(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");

        // Ademas de la excepcion, verificamos que NUNCA se llego a guardar nada.
        verify(productoRepository, never()).save(any());
    }

    @Test
    void buscarPorId_lanzaProductoNoEncontrado_cuandoNoExiste() {
        when(productoRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.buscarPorId(404L))
                .isInstanceOf(ProductoNoEncontradoException.class)
                .hasMessageContaining("404");
    }

    @Test
    void transferirStock_lanzaExcepcion_yNuncaConsultaElDestino_siElOrigenNoTieneStock() {
        Producto origen = new Producto(1L, "Mouse Gamer", 25.0, 2); // solo 2 unidades
        when(productoRepository.findById(1L)).thenReturn(Optional.of(origen));

        assertThatThrownBy(() -> productoService.transferirStock(1L, 2L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock insuficiente");

        // La prueba clave: como el chequeo de stock corta el flujo antes,
        // el metodo nunca deberia haber intentado buscar el producto destino.
        verify(productoRepository, never()).findById(2L);
        verify(productoRepository, never()).save(any());
    }

    @Test
    void agregarEtiqueta_agregaLaEtiquetaAlProducto() {
        Producto producto = new Producto(1L, "Mouse Gamer", 25.0, 10);
        Etiqueta etiqueta = new Etiqueta(1L, "Oferta");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(etiquetaRepository.findById(1L)).thenReturn(Optional.of(etiqueta));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse respuesta = productoService.agregarEtiqueta(1L, 1L);

        assertThat(respuesta.etiquetas()).containsExactly("Oferta");
    }
}
