package com.practica.crudpruebas.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// @ConfigurationProperties toma todo lo que este bajo "app.clima-api" en el
// yaml y lo mete en este record, con los nombres ya convertidos
// (base-url -> baseUrl, api-key -> apiKey). Es tipado: si alguien pone un
// numero donde va texto, falla al ARRANCAR la app, no en medio de un request.
//
// Equivalente directo a definir una clase de opciones + IOptions<ClimaApi>
// en .NET, con el bind hecho por el framework, no a mano.
@ConfigurationProperties(prefix = "app.clima-api")
public record ClimaApiProperties(String baseUrl, String apiKey) {
}
