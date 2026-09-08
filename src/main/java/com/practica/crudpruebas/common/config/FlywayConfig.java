package com.practica.crudpruebas.common.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

// Spring Boot (en esta version) YA NO trae auto-configuracion de Flyway --
// antes bastaba con agregar la dependencia y listo. Lo conectamos a mano:
// un BeanPostProcessor que intercepta el bean DataSource justo despues de
// que Spring termina de construirlo, y corre las migraciones ahi mismo.
//
// Por que ESTE punto y no otro: el EntityManagerFactory (Hibernate) depende
// del mismo bean DataSource -- Spring no puede crear el EntityManagerFactory
// hasta que el DataSource este completo. Como nuestra migracion corre DENTRO
// de la construccion del DataSource, queda garantizado que las tablas ya
// existen antes de que Hibernate intente validar el esquema.
@Configuration
public class FlywayConfig {

    @Bean
    public static BeanPostProcessor flywayMigrationPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof DataSource dataSource) {
                    Flyway.configure()
                            .dataSource(dataSource)
                            .load()
                            .migrate();
                }
                return bean;
            }
        };
    }
}
