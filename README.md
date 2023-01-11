# [Spring Security Tutorial - NEW 2023](https://www.youtube.com/watch?v=b9O9NI-RJ3o&lc=UgxtDd0vRqwrUqrS5oJ4AaABAg.9j6tAENyxKl9jfAPcgkOKE)
Tomado del canal de Amigoscode

# Creando una API
Creamos un api rest al que podemos acceder desde el navegador
a la ruta definida en dichos recursos. El acceso es con total 
libertad, ya que hasta ahora no hemos agregado Spring Security.
```
http://localhost:8080/api/v1/greetings
http://localhost:8080/api/v1/greetings/say-good-bye
```

# Agregando dependencia de Spring Security
Se agregó la siguiente dependencia
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
Ahora, únicamente agregando la dependencia de Spring Security, al 
querer acceder nuevamente a los endpoints creados, Spring Security 
nos **redirige a un formulario** en la ruta de login, para autenticarnos
con un usuario y una contraseña.

```
http://localhost:8080/login
```

El usuario por defecto es **user** y la contraseña la genera Spring Security
mostrándonos en el log al iniciar el proyecto.

Ejemplo:
```
username: user
password: 8f590407-0704-4b75-8d77-de1b15aa21b6
```
Una vez ingresamos las credenciales, le damos el login y podremos
acceder correctamente al recurso solicitado como lo hacíamos al inicio.

Ahora, si queremos desloguearnos, podemos ir a la siguiente ruta:
```
http://localhost:8080/logout
```

# Explicación del por qué al acceder al endpoint muestra un formulario
Cuando se agrega la dependencia de Spring Security, al iniciar la aplicación
Spring busca un objeto o bean del tipo de interfaz SecurityFilterChain, una vez 
lo encuentra, muestra las configuraciones establecidas en él. 

Por defecto Spring Security tiene un @Bean anotado en un método 
llamado defaultSecurityFilterChain(HttpSecurity http) e internamente tiene 
configuraciones para mostrar por defecto un Formulario y una autenticación básica, 
precisamente esa es la configuración de seguridad que por defecto 
Spring Security lanza al agregar la dependencia en el pom.xml, además de 
darle un @Order(2147483642) a dicho bean, que por defecto tiene el orden más bajo.

El bean mencionado se encuentra en la clase siguiente:
```
.../SpringBootWebSecurityConfiguration.class

@Bean
@Order(2147483642)
SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    ((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http.authorizeRequests().anyRequest()).authenticated();
    http.formLogin();
    http.httpBasic();
    return (SecurityFilterChain)http.build();
}
```
# Creando nuestra autenticación básica - Basic Auth
A la configuración que trae por defecto le quitamos el .formLogin() y 
lo dejamos con lo demás, incluido el .httpBasic()
```
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic();
    return http.build();
}
```
# Accediendo desde el navegador 
Como creamos nuestro @Bean personalizado para autenticarnos, al 
acceder al navegador ya no nos mostrará un formulario sino más bien 
un alert solicitándonos nuestro username y password, que serán las
mismas que usamos cuando agregamos la dependencia.

# Accediendo desde postman
Al acceder a nuestro endpoint vía postman, si ninguna otra configuración, 
nos mostrará como respuesta el **código 401 Unauthorized**. Esto es porque
no le hemos proporcionado las credenciales username y password.
```
[GET] http://localhost:8080/api/v1/greetings

Status: 401 Unauthorized
```
Para proporcionar las credenciales debemos irnos a la opción de:
```
Authorization *
Type: Basic Auth
Username: user
Password: 5a170c34-257b-4849-ab97-6db57c04e233 (la que viene en consola)
```
Ahora, si vamos a la opción de los **Headers** veremos el siguiente key=value,
es decir, nuestras credenciales se codificaron en Base64
```
Key: Authorization
Value: Basic dXNlcjo1YTE3MGMzNC0yNTdiLTQ4NDktYWI5Ny02ZGI1N2MwNGUyMzM=
```
Ahora, si le damos en enviar, veremos que ya nos hemos autenticado y 
podremos acceder a nuestro endpoint.

**NOTA:** La autenticación básica tiene vulnerabilidades de seguridad inherentes. 
Hay una mejor forma de proteger nuestras API de back-end y esto es usando JWT.