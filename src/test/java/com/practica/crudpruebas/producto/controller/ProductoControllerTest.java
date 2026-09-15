package com.practica.crudpruebas.producto.controller;

import com.practica.crudpruebas.common.security.JwtService;
import com.practica.crudpruebas.producto.dto.ProductoResponse;
import com.practica.crudpruebas.producto.exception.ProductoNoEncontradoException;
import com.practica.crudpruebas.producto.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest levanta SOLO la capa web: el DispatcherServlet, el Controller,
// los @RestControllerAdvice (como ManejadorDeErrores) -- pero NO escanea
// SecurityConfig (no es un Controller/Filter). En vez de importar la
// SecurityConfig REAL de produccion (que trae AuthenticationManager, el
// filtro JWT completo, etc. -- de mas para lo que este test necesita),
// definimos una config de seguridad MINIMA, solo con las reglas de
// autorizacion, para probar que el Controller las respeta.
//
// OJO: aunque no importamos SecurityConfig, @WebMvcTest SI detecta solo
// JwtAuthenticationFilter (implementa Filter) por su cuenta -- por eso
// igual hace falta mockear lo que ESE filtro necesita para poder construirse.
@WebMvcTest(ProductoController.class)
@Import(ProductoControllerTest.SeguridadDePrueba.class)
class ProductoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    // A diferencia de versiones previas de Spring Boot, aca ya no se aplica
    // solo con @Autowired MockMvc: hay que armarlo a mano con
    // .apply(springSecurity()) para que el SecurityContext que arma
    // @WithMockUser efectivamente viaje dentro del request simulado.
    // Sin esto, TODO request llega como anonimo al filtro de seguridad --
    // por eso antes daba 403 hasta en los tests "autenticados".
    @BeforeEach
    void configurarMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    void get_devuelve404_cuandoElServicioLanzaProductoNoEncontrado() throws Exception {
        when(productoService.buscarPorId(999L)).thenThrow(new ProductoNoEncontradoException(999L));

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe un producto con id 999"));
    }

    @Test
    @WithMockUser
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
    @WithMockUser
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
    @WithMockUser
    void get_devuelveElProducto_cuandoExiste() throws Exception {
        when(productoService.buscarPorId(anyLong()))
                .thenReturn(new ProductoResponse(1L, "Mouse Gamer", 25.0, 10, "Perifericos", List.of("Oferta")));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoriaNombre").value("Perifericos"))
                .andExpect(jsonPath("$.etiquetas[0]").value("Oferta"));
    }

    @Test
    void delete_devuelve401_sinAutenticar() throws Exception {
        // SIN @WithMockUser -- ni siquiera llega a evaluar el rol, porque
        // no hay usuario en absoluto.
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_devuelve403_siElUsuarioNoEsAdmin() throws Exception {
        // Autenticado, pero SIN el rol ADMIN que pide @PreAuthorize --
        // el Service ni se llega a invocar.
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_devuelve204_siElUsuarioEsAdmin() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    // Reglas identicas a SecurityConfig.securityFilterChain() (publico
    // /api/auth/**, autenticado el resto) -- pero SIN el filtro JWT ni el
    // AuthenticationManager, que este test no necesita: @WithMockUser
    // inyecta la autenticacion directo, sin pasar por ningun filtro.
    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity
    static class SeguridadDePrueba {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/auth/**").permitAll()
                            .anyRequest().authenticated())
                    .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
            return http.build();
        }
    }
}
