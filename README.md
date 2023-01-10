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
