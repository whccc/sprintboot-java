package com.practica.crudpruebas.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Mismo patron que ClimaApiProperties: el secreto NUNCA hardcodeado de
// verdad -- viaja por variable de entorno (ver application.yaml).
// Si alguien obtiene este secreto, puede FIRMAR tokens validos como
// cualquier usuario -- es tan sensible como una clave de base de datos.
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expiracionMs) {
}
