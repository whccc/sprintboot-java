package com.practica.crudpruebas.producto.controller;

import com.practica.crudpruebas.producto.dto.ProductoResponse;
import com.practica.crudpruebas.producto.exception.ProductoNoEncontradoException;
import com.practica.crudpruebas.producto.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest levanta SOLO la capa web: el DispatcherServlet, el Controller,
// y los @RestControllerAdvice (como ManejadorDeErrores) -- pero NO el
// Service real, ni JPA, ni la base de datos. Por eso el Service se mockea:
// no nos interesa probar su logica aca, solo que el Controller traduzca
// bien HTTP <-> lo que el Service devuelve o lanza.
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void get_devuelve404_cuandoElServicioLanzaProductoNoEncontrado() throws Exception {
        when(productoService.buscarPorId(999L)).thenThrow(new ProductoNoEncontradoException(999L));

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe un producto con id 999"));
    }

    @Test
    void post_devuelve400_cuandoElBodyNoPasaValidacion() throws Exception {
        // nombre en blanco (viola @NotBlank) y precio negativo (viola @Positive)
        String bodyInvalido = """
                { "nombre": "", "precio": -5, "stock": 3 }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType("application/json")
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").exists())
                .andExpect(jsonPath("$.precio").exists());
    }

    @Test
    void post_devuelve201_yElJsonDelProducto_cuandoElBodyEsValido() throws Exception {
        ProductoResponse respuestaSimulada =
                new ProductoResponse(1L, "Mouse Gamer", 25.0, 10, null, List.of());
        when(productoService.crear(org.mockito.ArgumentMatchers.any())).thenReturn(respuestaSimulada);

        String bodyValido = """
                { "nombre": "Mouse Gamer", "precio": 25.0, "stock": 10 }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType("application/json")
                        .content(bodyValido))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"));
    }

    @Test
    void get_devuelveElProducto_cuandoExiste() throws Exception {
        when(productoService.buscarPorId(anyLong()))
                .thenReturn(new ProductoResponse(1L, "Mouse Gamer", 25.0, 10, "Perifericos", List.of("Oferta")));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoriaNombre").value("Perifericos"))
                .andExpect(jsonPath("$.etiquetas[0]").value("Oferta"));
    }
}
