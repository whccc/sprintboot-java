package com.practica.crudpruebas.common.config;

import com.practica.crudpruebas.categoria.repository.CategoriaRepository;
import com.practica.crudpruebas.producto.dto.ProductoResponse;
import com.practica.crudpruebas.producto.repository.ProductoRepository;
import com.practica.crudpruebas.producto.service.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class ParaleloController {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoService productoService;

    public ParaleloController(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
                               ProductoService productoService) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoService = productoService;
    }

    // Cada supplyAsync corre en SU PROPIO hilo (del pool comun de la JVM).
    // Spring Data JPA le da a cada hilo su propio EntityManager -- no hay
    // conflicto, aunque las dos consultas toquen la base de datos "al mismo
    // tiempo".
    @GetMapping("/api/config/paralelo")
    public Map<String, Object> consultasEnParalelo() {
        long inicio = System.currentTimeMillis();

        CompletableFuture<Integer> productosFuturo = CompletableFuture.supplyAsync(() -> {
            simularLatenciaDeRed();
            return productoRepository.findAll().size();
        });

        CompletableFuture<Integer> categoriasFuturo = CompletableFuture.supplyAsync(() -> {
            simularLatenciaDeRed();
            return categoriaRepository.findAll().size();
        });

        CompletableFuture.allOf(productosFuturo, categoriasFuturo).join();

        long duracionMs = System.currentTimeMillis() - inicio;
        return Map.of(
                "cantidadProductos", productosFuturo.join(),
                "cantidadCategorias", categoriasFuturo.join(),
                "duracionMs", duracionMs,
                "modo", "PARALELO"
        );
    }

    // La MISMA logica, pero esperando una consulta antes de arrancar la otra.
    @GetMapping("/api/config/secuencial")
    public Map<String, Object> consultasEnSecuencia() {
        long inicio = System.currentTimeMillis();

        simularLatenciaDeRed();
        int cantidadProductos = productoRepository.findAll().size();

        simularLatenciaDeRed();
        int cantidadCategorias = categoriaRepository.findAll().size();

        long duracionMs = System.currentTimeMillis() - inicio;
        return Map.of(
                "cantidadProductos", cantidadProductos,
                "cantidadCategorias", cantidadCategorias,
                "duracionMs", duracionMs,
                "modo", "SECUENCIAL"
        );
    }

    // FLUJO ENCADENADO: el paso 2 NECESITA el resultado del paso 1
    // (la categoria del producto) para poder ejecutarse. Es la traduccion de:
    //
    //   var producto  = await BuscarProductoAsync(id);
    //   var hermanos  = await BuscarPorCategoriaAsync(producto.CategoriaNombre);
    //
    // .thenApplyAsync() = "cuando el paso anterior termine, con SU resultado,
    // segui con esto" -- nunca se bloquea un hilo esperando; el paso 2 se
    // programa para ejecutar recien cuando el paso 1 avisa que termino.
    @GetMapping("/api/config/encadenado/{productoId}")
    public Map<String, Object> flujoEncadenado(@PathVariable Long productoId) {
        long inicio = System.currentTimeMillis();

        CompletableFuture<Map<String, Object>> resultado = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("[PASO 1] hilo=" + Thread.currentThread().getName()
                            + " -- buscando producto " + productoId);
                    simularLatenciaDeRed();
                    return productoService.buscarPorId(productoId);
                })
                .thenApplyAsync(productoEncontrado -> {
                    System.out.println("[PASO 2] hilo=" + Thread.currentThread().getName()
                            + " -- producto=" + productoEncontrado.nombre()
                            + ", ahora busco otros de su categoria: " + productoEncontrado.categoriaNombre());
                    simularLatenciaDeRed();
                    List<ProductoResponse> hermanos =
                            productoService.buscarPorCategoriaConFetch(productoEncontrado.categoriaNombre());

                    Map<String, Object> r = new HashMap<>();
                    r.put("producto", productoEncontrado.nombre());
                    r.put("categoria", productoEncontrado.categoriaNombre());
                    r.put("otrosProductosEnLaMismaCategoria", hermanos.stream().map(ProductoResponse::nombre).toList());
                    return r;
                });

        Map<String, Object> valor = resultado.join();
        valor.put("duracionMs", System.currentTimeMillis() - inicio);
        return valor;
    }

    // LA MISMA idea (paso 2 depende del paso 1) pero SIN CompletableFuture,
    // SIN lambdas anidadas -- solo codigo Java normal, de arriba a abajo.
    // En Spring MVC (el modelo que usamos: Tomcat con hilos bloqueantes),
    // esto es TODO lo que necesitas el 90% de las veces: cada request HTTP
    // ya tiene su propio hilo dedicado, asi que "esperar A y seguir con B"
    // no bloquea a NADIE mas -- no hay ningun otro trabajo compitiendo por
    // ese hilo en particular.
    @GetMapping("/api/config/encadenado-simple/{productoId}")
    public Map<String, Object> flujoEncadenadoSimple(@PathVariable Long productoId) {
        long inicio = System.currentTimeMillis();

        // "await BuscarProductoAsync(id)" -- en Java bloqueante, es solo esto:
        simularLatenciaDeRed();
        ProductoResponse productoEncontrado = productoService.buscarPorId(productoId);

        // "await BuscarPorCategoriaAsync(producto.CategoriaNombre)"
        simularLatenciaDeRed();
        List<ProductoResponse> hermanos =
                productoService.buscarPorCategoriaConFetch(productoEncontrado.categoriaNombre());

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("producto", productoEncontrado.nombre());
        resultado.put("categoria", productoEncontrado.categoriaNombre());
        resultado.put("otrosProductosEnLaMismaCategoria", hermanos.stream().map(ProductoResponse::nombre).toList());
        resultado.put("duracionMs", System.currentTimeMillis() - inicio);
        return resultado;
    }

    // Simula una consulta "lenta" (una API externa, una query pesada, etc.)
    private void simularLatenciaDeRed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
