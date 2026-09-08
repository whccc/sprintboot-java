package com.practica.crudpruebas.common.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ConfiguracionController {

    private final ClimaApiProperties climaApiProperties;

    // Spring inyecta ClimaApiProperties como cualquier otro bean --
    // ya viene armado con los valores del yaml/variables de entorno.
    public ConfiguracionController(ClimaApiProperties climaApiProperties) {
        this.climaApiProperties = climaApiProperties;
    }

    @GetMapping("/api/config/clima-api")
    public Map<String, String> verConfiguracion() {
        return Map.of(
                "baseUrl", climaApiProperties.baseUrl(),
                "apiKeyEnmascarada", enmascarar(climaApiProperties.apiKey())
        );
    }

    // Demo de DEPENDENCY INJECTION + bean scope por defecto (singleton).
    // identityHashCode identifica el objeto en memoria -- si dos requests
    // HTTP distintos devuelven el MISMO numero, es literalmente el mismo
    // objeto Java reutilizado, no uno nuevo creado por request.
    @GetMapping("/api/config/identidad-del-bean")
    public Map<String, String> identidadDelBean() {
        return Map.of(
                "controllerHashCode", String.valueOf(System.identityHashCode(this)),
                "propertiesHashCode", String.valueOf(System.identityHashCode(climaApiProperties))
        );
    }

    // Demo de VIRTUAL THREADS. El nombre/tipo del hilo actual delata si Tomcat
    // esta usando threads tradicionales ("http-nio-8080-exec-N") o virtuales
    // ("VirtualThread[#N]/runnable@ForkJoinPool..."). Sin tocar nada de este
    // codigo, un solo flag en el yaml cambia la respuesta.
    @GetMapping("/api/config/hilo-actual")
    public Map<String, String> hiloActual() {
        Thread hiloActual = Thread.currentThread();
        return Map.of(
                "descripcion", hiloActual.toString(),
                "esVirtual", String.valueOf(hiloActual.isVirtual())
        );
    }

    // Nunca se devuelve (ni se loguea) una API key completa, ni siquiera en un
    // endpoint de diagnostico interno.
    private String enmascarar(String valor) {
        if (valor == null || valor.length() <= 4) {
            return "****";
        }
        return "****" + valor.substring(valor.length() - 4);
    }
}
