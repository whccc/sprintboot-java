package com.practica.crudpruebas.common.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

// WebClient es el cliente HTTP reactivo de Spring. Lo usamos aca DENTRO de
// una app que sigue siendo Spring MVC (Tomcat) -- no hace falta migrar todo
// el servidor a WebFlux para aprovechar un Mono puntual.
@RestController
public class ReactivoController {

    private final WebClient webClient = WebClient.create("https://jsonplaceholder.typicode.com");

    // El metodo del Controller devuelve Mono<...> directo, no un Map ya
    // resuelto. Spring MVC sabe esperar a que el Mono se complete antes de
    // escribir la respuesta HTTP -- nunca llamamos a .block() nosotros.
    @GetMapping("/api/config/reactivo/usuario/{id}")
    public Mono<Map<String, Object>> obtenerUsuario(@PathVariable int id) {
        long inicio = System.currentTimeMillis();
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(Map.class)
                .map(datos -> Map.of(
                        "nombre", datos.get("name"),
                        "email", datos.get("email"),
                        "ciudad", ((Map<?, ?>) datos.get("address")).get("city"),
                        "tiempoMs", System.currentTimeMillis() - inicio
                ));
    }

    // flatMap encadena 2 llamadas HTTP donde la segunda depende del
    // resultado de la primera -- el equivalente reactivo de lo que
    // armamos con CompletableFuture.thenApplyAsync() hace un tiempo:
    // "await A(); await B(resultadoDeA);", pero sin bloquear NADA.
    @GetMapping("/api/config/reactivo/usuario/{id}/posts-count")
    public Mono<Map<String, Object>> contarPostsDelUsuario(@PathVariable int id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(usuario -> webClient.get()
                        .uri("/posts?userId={id}", id)
                        .retrieve()
                        .bodyToMono(Object[].class)
                        .map(posts -> Map.of(
                                "usuario", usuario.get("name"),
                                "cantidadDePosts", posts.length
                        )));
    }
}
