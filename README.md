# Backend Users App

API REST construida con Spring Boot para gestión de usuarios, validaciones,
manejo global de errores, documentación OpenAPI (Swagger UI), integración con
MySQL y endpoint de saludo asistido por IA (LangChain4j).

## Tabla de contenido

- [Tecnologías](#tecnologías)
- [Arquitectura y componentes](#arquitectura-y-componentes)
- [Requisitos previos](#requisitos-previos)
- [Variables de entorno](#variables-de-entorno)
- [Ejecución local](#ejecución-local)
- [Docker y base de datos](#docker-y-base-de-datos)
- [Perfiles de Spring](#perfiles-de-spring)
- [Documentación Swagger](#documentación-swagger)
- [API Endpoints](#api-endpoints)
- [Validaciones y manejo de errores](#validaciones-y-manejo-de-errores)
- [Datos semilla](#datos-semilla)
- [Colección Postman](#colección-postman)
- [Pruebas](#pruebas)
- [Troubleshooting](#troubleshooting)

## Tecnologías

- Java 21
- Spring Boot 4.0.3
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- Spring Actuator
- Springdoc OpenAPI + Swagger UI (`springdoc-openapi-starter-webmvc-ui` 2.8.14)
- MySQL
- Docker Compose
- LangChain4j (`langchain4j-open-ai-spring-boot-starter`,
  `langchain4j-spring-boot-starter`)
- Maven Wrapper (`mvnw`, `mvnw.cmd`)

## Arquitectura y componentes

### Capas principales

- **Controller**: expone endpoints REST
  - `UserController`: CRUD de usuarios
  - `HelloController`: endpoint de saludo generado por IA
- **Service**:
  - `UserService` / `UserServiceImp`: lógica de negocio de usuarios
  - `GreetingAiService`: servicio IA vía LangChain4j
- **Repository**:
  - `UserRepository` (`CrudRepository<User, Long>`)
- **Entity**:
  - `User`: mapeo JPA + validaciones Bean Validation
- **Exception Handling**:
  - `RestExceptionHandler`: manejo global de errores (`400`, `404`, `409`,
    `500`)

### Context path

La API se publica bajo:

`/backend-usersapp/api`

Por ejemplo:

- `GET http://localhost:8080/backend-usersapp/api/users`

## Requisitos previos

- Java 21 instalado
- Docker Desktop (si usarás MySQL con contenedor)
- Puerto `8080` libre para la API
- Puerto `3306` libre para MySQL (si usas Docker)

## Variables de entorno

El proyecto usa variables de entorno para la conexión a base de datos
(configuradas en `application.properties` con `${env.*}`), además de soporte
para `.env` (`spring-dotenv`).

### Variables requeridas

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### Variables recomendadas para Docker MySQL

- `MYSQL_ROOT_PASSWORD`
- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`

### Variables opcionales (IA)

Puedes sobreescribir el valor de `langchain4j.open-ai.chat-model.api-key` por
entorno:

- `LANGCHAIN4J_OPEN_AI_CHAT_MODEL_API_KEY`

> Actualmente en `application.properties` hay un valor por defecto de desarrollo
> (`demo`).

### Ejemplo de archivo `.env`

```env
# MySQL (docker-compose)
MYSQL_ROOT_PASSWORD=passwordRootDb
MYSQL_DATABASE=db_users_springboot
MYSQL_USER=userDb
MYSQL_PASSWORD=passwdDb

# Datasource Spring
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/db_users_springboot
SPRING_DATASOURCE_USERNAME=userDb
SPRING_DATASOURCE_PASSWORD=passwdDb

# Opcional IA
LANGCHAIN4J_OPEN_AI_CHAT_MODEL_API_KEY=demo
```

## Ejecución local

### Opción 1: Maven Wrapper

En Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

La app arranca por defecto con perfil `dev` (`spring.profiles.active=dev`).

### Opción 2: VS Code Run and Debug

Se incluyen perfiles de ejecución en `.vscode/launch.json`:

- `BackendUsersappApplication (JMX 9010)`
- `BackendUsersappApplication (JMX 9011)`

Incluyen verificación previa de puertos con tareas en `.vscode/tasks.json`:

- `check-jmx-9010-free`
- `check-jmx-9011-free`

## Docker y base de datos

`docker-compose.yml` define un servicio MySQL:

- Imagen: `mysql:latest`
- Contenedor: `mysql-container`
- Puerto: `3306:3306`
- Volumen persistente: `mysql_data`
- Variables tomadas del entorno (`MYSQL_*`)

### Levantar MySQL manualmente

```powershell
docker compose up -d
```

### Detener MySQL

```powershell
docker compose down
```

> En perfil `dev`, Spring Boot tiene integración con Docker Compose habilitada
> (`spring.docker.compose.enabled=true`) y puede gestionar el ciclo de vida del
> compose.

## Perfiles de Spring

### `application.properties` (base)

- `spring.profiles.active=dev`
- `server.servlet.context-path=/backend-usersapp/api`
- `spring.jmx.enabled=true`
- `spring.jpa.hibernate.ddl-auto=update`
- SQL log habilitado

### `application-dev.properties`

- `server.port=8080`
- Docker Compose habilitado
- Inicialización de `data.sql` habilitada
- Logs de LangChain4j habilitados

### `application-prod.properties`

- `server.port=8080`
- Docker Compose deshabilitado
- Swagger UI deshabilitado (`springdoc.swagger-ui.enabled=false`)

## Documentación Swagger

Con la app corriendo en local:

- Swagger UI: `http://localhost:8080/backend-usersapp/api/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/backend-usersapp/api/v3/api-docs`

Los controladores `UserController` y `HelloController` están documentados con
anotaciones OpenAPI (`@Tag`, `@Operation`, `@ApiResponse`).

## API Endpoints

Base URL local:

`http://localhost:8080/backend-usersapp/api`

### Users

- `GET /users` → lista todos los usuarios
- `GET /users/{id}` → obtiene un usuario por id
- `POST /users` → crea usuario
- `PUT /users/{id}` → actualiza usuario
- `DELETE /users/{id}` → elimina usuario

### Hello

- `GET /hello` → retorna saludo en texto plano generado por IA

## Validaciones y manejo de errores

### Validaciones de `User`

En la entidad `User`:

- `username`
  - `@NotBlank(message = "El username es obligatorio")`
  - `@Size(max = 40, ...)`
- `email`
  - `@NotBlank(message = "El email es obligatorio")`
  - `@Email(message = "El email no es válido")`
  - `@Size(max = 100, ...)`
- `password`
  - `@NotBlank(message = "El password es obligatorio")`
  - `@Size(min = 6, max = 60, ...)`

### Respuestas de error

`RestExceptionHandler` centraliza excepciones:

- `409 Conflict`
  - `user-already-exists`
  - `email-already-exists`
- `404 Not Found`
  - `user-not-found`
- `400 Bad Request`
  - `validation-error` con lista ordenada de errores por campo
- `500 Internal Server Error`
  - `unknown-error`

#### Ejemplo error de usuario no encontrado

```json
{
    "type": "user-not-found",
    "message": "User not found with id: 100"
}
```

#### Ejemplo error de validación

```json
{
    "type": "validation-error",
    "message": "Se encontraron errores de validación",
    "errors": [
        {
            "field": "email",
            "message": "El email no es válido"
        },
        {
            "field": "username",
            "message": "El username es obligatorio"
        }
    ]
}
```

## Datos semilla

En perfil `dev`, `src/main/resources/data.sql` inserta datos iniciales con
`INSERT IGNORE` en la tabla `users`.

Usuarios precargados incluyen: `admin`, `juan`, `maria`, `pedro`, `sofia`,
`lucas`, `valentina`, `andres`, `camila`, `laura`.

## Colección Postman

Se incluyen archivos listos para importar en Postman:

- `docs/backend-usersapp.postman_collection.json`
- `docs/backend-usersapp.postman_environment.json`

### Cómo importar

1. Abre Postman.
2. Haz clic en **Import**.
3. Importa primero `docs/backend-usersapp.postman_environment.json`.
4. Importa después `docs/backend-usersapp.postman_collection.json`.
5. Selecciona el environment **Backend Users App - Local**.
6. Ajusta `baseUrl` si tu API corre en otro host/puerto.

Incluye requests para:

- CRUD de `users`
- Casos de error `400`, `404`, `409`
- `GET /hello`

## Pruebas

Ejecutar pruebas:

```powershell
.\mvnw.cmd test
```

Actualmente existe prueba base de contexto (`BackendUsersappApplicationTests`).

## Troubleshooting

### 1) Swagger muestra `{}` en respuestas

Se corrigió documentando `ResponseEntity` tipado y `@ApiResponse` con
`@Schema(implementation = ...)`. Si no ves cambios, recarga el navegador con
`Ctrl+F5`.

### 2) Error JMX en VS Code (`Failed to refresh live data ... 9010`)

Usa el perfil `BackendUsersappApplication (JMX 9011)` o libera el puerto `9010`.
El proyecto ya incluye tareas de verificación de puertos.

### 3) Error de conexión a MySQL

- Verifica que MySQL esté levantado (`docker compose ps`)
- Confirma variables `SPRING_DATASOURCE_*`
- Revisa credenciales `MYSQL_*` y puerto `3306`
