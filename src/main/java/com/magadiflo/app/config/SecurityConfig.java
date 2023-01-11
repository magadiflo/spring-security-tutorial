package com.magadiflo.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    private final static List<UserDetails> APPLICATION_USERS = Arrays.asList(
            new User("admin@gmail.com", "12345", Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))),
            new User("user@gmail.com", "12345", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")))
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(this.authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(this.userDetailsService());
        authenticationProvider.setPasswordEncoder(this.passwordEncoder());

        return authenticationProvider;
    }

    /***
     * Este método authenticationManager() anotado con @Bean será usado en el controller
     * AuthenticationResource, para delegar la responsabilidad a Spring de continuar
     * con el proceso de autenticación.
     *
     * Por parámetro estamos recibiendo un AuthenticationConfiguration y
     * será Spring quien lo inyecte
     * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Implementando el UserDetailsService
     * ************************************
     * Como el UserDetailsService es una interfaz que se usa en varios lugares,
     * como por ejemplo en el JwtAuthFilter, es que necesitamos implementar dicha
     * interfaz en una clase concreta. Existe varias dos formas:
     * <p>
     * 1° Crear una clase que implemente la interfaz UserDetailsService
     * 2° Crear un @Bean que retorne un UserDetailsService
     */

    /**
     * Decirle a Spring Security que use este userDetailsService()
     * ***********************************************************
     * Debemos decirle a Spring Security que use este método userDetailsService()
     * en lugar de su propia implementación.
     * <p>
     * Para eso creamos un nuevo @Bean del tipo AuthenticationProvider en el
     * que le decimos que utilice nuestro this.userDetailsService(), además
     * de decirle también que use un codificador de contraseña this.passwordEncoder(),
     * mismo que será creado también como un @Bean.
     * <p>
     * Finalmente en el método securityFilterChain(...) le decimos que use
     * nuestro authenticationProvider() que a su vez contiene nuestro
     * userDetailsService()
     */

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                // En este método nos conectaremos a una BD para buscar los usuarios, pero por ahora
                // usaremos una lista estática o usuarios en memoria para nuestra aplicación
                return APPLICATION_USERS.stream()
                        .filter(userDetails -> userDetails.getUsername().equals(email))
                        .findFirst()
                        .orElseThrow(() -> new UsernameNotFoundException("No user was found!!!"));
            }
        };
    }

}
