package com.practica.crudpruebas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
// Habilita que Spring busque clases @ConfigurationProperties en todo el
// paquete base y las registre solas como beans (sin @Component en cada una).
@ConfigurationPropertiesScan
public class CrudpruebasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudpruebasApplication.class, args);
	}

}
