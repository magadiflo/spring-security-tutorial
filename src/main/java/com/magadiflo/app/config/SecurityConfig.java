package com.magadiflo.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

/**
 * * @EnableWebSecurity
 * *********************
 * Anotación que habilita la seguridad web, por lo que
 * Spring sabrá que esta es una clase de configuración y
 * que contiene una configuración de seguridad.
 * <p>
 * La configuración que hagamos aquí sobreescribirá a la configuración
 * que trae por defecto Spring.
 * <p>
 * <p>
 * <p>
 * * @Bean
 * SecurityFilterChain
 * *******************
 * Creamos un método personalizado securityFilterChain() que retorna un tipo de interfaz
 * SecurityFilterChain y lo anotamos con @Bean para que sea registrado en el contenedor
 * de Spring y debido a que tiene el orden predeterminado de beans, Spring elegirá este
 * bean que estamos creando en lugar del securityFilterChain predeterminado que
 * Spring proporciona automáticamente.
 * <p>
 * La configuración que le daremos a nuestro securityFilterChain será similar a la que
 * Spring Security nos proporciona, con la diferencia de que en nuestro caso NO USAREMOS
 * un "http.formLogin()" es decir, NO USAREMOS UN FORMULARIO para autenticarnos, pero sí
 * requerimos de la autenticación básica "http.httpBasic()" tal como la configuración
 * por defecto la trae.
 * <p>
 * Al ejecutar la aplicación con nuestra configuración (donde le quitamos el http.formLogin() y
 * dejamos el .httpBasic()), al tratar de acceder a nuestros endpoints mediante el navegador, Spring nos
 * mostrará ahora un alert solicitándonos nuestro usuario y contraseña, que por el momento
 * serán similares a cómo lo proporcionamos anteriormente:
 * <p>
 * username: user
 * password: 334c46e7-b593-4813-bbe2-3ec027593431 (extraída de la consola del ide)
 */

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
