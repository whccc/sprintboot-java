package com.practica.crudpruebas.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

// JwtService no depende de ningun puerto ni de Spring -- se puede instanciar
// con "new" directo, igual que un Domain Service. Por eso este test no
// necesita ni @SpringBootTest ni base de datos.
class JwtServiceTest {

    private final JwtProperties jwtProperties =
            new JwtProperties("un-secreto-de-prueba-que-tiene-mas-de-32-caracteres-1234", 3_600_000);
    private final JwtService jwtService = new JwtService(jwtProperties);

    @Test
    void generaUnToken_yPuedeExtraerElUsernameDeVuelta() {
        String token = jwtService.generarToken("wilson");

        assertThat(token).isNotBlank();
        assertThat(jwtService.extraerUsername(token)).isEqualTo("wilson");
    }

    @Test
    void esValido_devuelveTrue_siElUsernameDelTokenCoincideConElUserDetails() {
        String token = jwtService.generarToken("wilson");
        UserDetails userDetails = User.withUsername("wilson").password("x").authorities("ROLE_USER").build();

        assertThat(jwtService.esValido(token, userDetails)).isTrue();
    }

    @Test
    void esValido_devuelveFalse_siElTokenEsDeOtroUsuario() {
        String token = jwtService.generarToken("wilson");
        UserDetails otroUsuario = User.withUsername("otro-usuario").password("x").authorities("ROLE_USER").build();

        assertThat(jwtService.esValido(token, otroUsuario)).isFalse();
    }
}
