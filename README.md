<div align="center">
  <img src="./assets/ferreteria-icon.svg" width="120" alt="Ferretería API" />

  # Ferretería API
</div>

API REST para la gestión de inventario de una ferretería — alta, consulta, actualización
y baja de herramientas. Construida con **Spring Boot** siguiendo una arquitectura en
capas (Controller → Service → Repository) con separación estricta entre entidades de
persistencia y contrato de API mediante **DTOs**.

Proyecto formativo: nació como ejercicio / prueba técnica y fue mi base de práctica para ir
sumando buenas prácticas (manejo de excepciones, programación funcional, Docker). Hoy se
ejecuta en local; el despliegue en la nube se dio de baja porque
[Bazar](https://github.com/mdev-repos/api_bazar__ejercicio_curso_todocode) retoma la idea con
un modelo más complejo y es el que mantiene el despliegue de punta a punta.

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-brightgreen?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-build-C71A36?logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-prod-336791?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![Error Handling](https://img.shields.io/badge/Error%20Handling-RFC%207807-blueviolet)
![Status](https://img.shields.io/badge/status-proyecto%20formativo-blue)

> **Despliegue dado de baja.** La API y su base de datos en la nube ya no están activas. El
> proyecto se levanta en local en pocos minutos, con Docker Compose o a mano — ver
> [Cómo correrlo en local](#cómo-correrlo-en-local).

---

## Índice

- [Contexto del ejercicio](#contexto-del-ejercicio)
- [Stack técnico](#stack-técnico)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [Endpoints](#endpoints)
- [Cómo correrlo en local](#cómo-correrlo-en-local)
- [Perfiles y bases de datos](#perfiles-y-bases-de-datos)
- [Configuración](#configuración)
- [Proyecto relacionado](#proyecto-relacionado)
- [Roadmap](#roadmap)
- [Autor](#autor)

---

## Contexto del ejercicio

> Prueba técnica — TodoCode Academy

Una ferretería necesita una API REST que le permita administrar las herramientas y
productos disponibles en su negocio, con miras a que en una etapa posterior un
frontend independiente consuma estos servicios. Cada herramienta se registra con
nombre, marca, categoría, precio, stock disponible y descripción, exponiendo el CRUD
completo mediante una API REST con persistencia en una base de datos relacional.

## Stack técnico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4 (Spring Web, Spring Data JPA) |
| ORM | Hibernate |
| Base de datos | MySQL (perfil `dev`, el que se usa en local) · PostgreSQL (perfil `prod`, sin despliegue activo) — ver [Perfiles y bases de datos](#perfiles-y-bases-de-datos) |
| Validación | Jakarta Bean Validation |
| Manejo de errores | `ProblemDetail` (RFC 7807) + `@RestControllerAdvice` centralizado |
| Build | Maven |
| Reducción de boilerplate | Lombok |

## Arquitectura

Arquitectura en capas, con **DTOs (`record`) + Mapper** como frontera entre el
contrato público de la API y el modelo de persistencia — las entidades JPA nunca se
exponen directamente en request ni response.

```
Cliente (JSON)
   │
   ▼
Controller        → recibe/devuelve DTOs, gestiona status codes (ResponseEntity)
   │
   ▼
Service            → lógica de negocio, orquesta Mapper + Repository
   │
   ├─▶ Mapper       → traduce Entity ⇄ DTO (con Streams para las colecciones)
   │
   ▼
Repository (Spring Data JPA) → persistencia
   │
   ▼
MySQL (dev) / PostgreSQL (prod)
```

Cualquier excepción lanzada en Service o Repository es interceptada por un
`GlobalExceptionHandler` (`@RestControllerAdvice`) que la traduce a una respuesta
`ProblemDetail` (RFC 7807) con el status code correspondiente — el Controller
nunca maneja errores directamente.

```
src/main/java/com/mdev/ferreteria
├── config          # configuración transversal (CORS, etc.)
├── controller       # capa REST — solo DTOs y ResponseEntity
├── dto
│   ├── request        # contratos de entrada (records, con validación)
│   └── response         # contratos de salida (records)
├── exception          # excepciones de negocio + GlobalExceptionHandler
├── mapper             # Entity ⇄ DTO
├── model
│   └── enums
├── repository         # Spring Data JPA
└── service
    └── impl
```

## Modelo de datos

**Tool**

| Campo | Tipo | Notas |
|---|---|---|
| `id` | `Long` | autogenerado (`SEQUENCE`) |
| `name` | `String` | obligatorio |
| `brand` | `String` | obligatorio |
| `category` | `ToolCategory` (enum) | `HAND_TOOL`, `POWER_TOOL`, `BATTERY_TOOL`, `MEASURING_TOOL` |
| `price` | `double` | > 0 |
| `stock` | `int` | ≥ 0 |
| `description` | `String` | opcional |

## Endpoints

Base path: `/tools`

| Método | Ruta | Descripción | Éxito | Errores |
|---|---|---|---|---|
| `POST` | `/create` | Crea una herramienta | `201 Created` | `400 Bad Request` |
| `GET` | `/{id}` | Obtiene una herramienta por id | `200 OK` | `404 Not Found` |
| `GET` | `/all` | Lista todas las herramientas | `200 OK` | — |
| `PATCH` | `/{id}` | Actualiza parcialmente una herramienta | `200 OK` | `400`, `404` |
| `DELETE` | `/{id}` | Elimina una herramienta | `204 No Content` | `404 Not Found` |

<details>
<summary><strong>Ejemplo — crear herramienta</strong></summary>

`POST /tools/create`

```json
{
  "name": "Taladro percutor",
  "brand": "Bosch",
  "category": "POWER_TOOL",
  "price": 45000.0,
  "stock": 12,
  "description": "Taladro percutor 650W, incluye maletín"
}
```

`201 Created`

```json
{
  "id": 1,
  "name": "Taladro percutor",
  "brand": "Bosch",
  "category": "POWER_TOOL",
  "price": 45000.0,
  "stock": 12,
  "description": "Taladro percutor 650W, incluye maletín"
}
```

</details>

<details>
<summary><strong>Ejemplo — actualización parcial</strong></summary>

`PATCH /tools/1`

```json
{
  "stock": 8
}
```

Solo el campo enviado se modifica; el resto de la herramienta queda intacto.

</details>

<details>
<summary><strong>Ejemplo — error de validación</strong></summary>

`POST /tools/create`

```json
{
  "name": "",
  "brand": "",
  "price": -5,
  "stock": -1
}
```

`400 Bad Request`

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Error de validación",
  "instance": "/tools/create",
  "errores": {
    "name": "no debe estar vacío",
    "brand": "no debe estar vacío",
    "price": "debe ser mayor que 0",
    "stock": "debe ser mayor que o igual a 0",
    "category": "no debe ser nulo"
  }
}
```

Todos los errores de la API (validación, recurso no encontrado, JSON malformado, etc.)
siguen el formato estándar [`ProblemDetail`](https://www.rfc-editor.org/rfc/rfc7807) —
mismo contrato de error en toda la aplicación.

</details>

## Cómo correrlo en local

Hay dos formas: con Docker Compose (la más simple, no requiere instalar nada más) o a mano.
En ambas la API queda en **`http://localhost:8080`** y arranca con el perfil `dev` (MySQL).

### Opción A — Docker Compose (recomendado)

**Prerrequisitos**: Docker con el plugin Compose.

```bash
cd ferreteria
cp .env.example .env     # define DB_PASSWORD y CORS_ALLOWED_ORIGINS; cambiá la contraseña
docker compose up -d --build
```

Levanta la API **y** su base de datos MySQL juntas, ya conectadas entre sí:

| Servicio | Puerto en tu máquina | Detalle |
|---|---|---|
| `api` | `8080` | Se construye con el `Dockerfile` multi-stage (compila con Maven y corre sobre una imagen JRE) |
| `mysql` | `3307` | Base `todocode_ferreteria`; los datos persisten en un volumen de Docker |

La API espera a que MySQL pase su *healthcheck* antes de iniciar, por eso la primera
vez puede tardar un minuto. Para ver los logs: `docker compose logs -f api`.

Para detenerlo: `docker compose down` (conserva los datos) o `docker compose down -v`
(borra también el volumen y arranca de cero la próxima vez).

### Opción B — manual, sin Docker

**Prerrequisitos**: JDK 25, Maven (o el wrapper `./mvnw` incluido) y MySQL corriendo en
`localhost:3306`.

```bash
# 1. Crear la base de datos
mysql -u root -p -e "CREATE DATABASE todocode_ferreteria;"

# 2. Si tu MySQL tiene contraseña, exportala (los defaults están en la sección Configuración)
export DB_PASSWORD=tu_contraseña

# 3. Levantar la aplicación
cd ferreteria
./mvnw spring-boot:run
```

### Probar que funciona

Con cualquiera de las dos opciones, crear y listar herramientas:

```bash
curl -X POST http://localhost:8080/tools/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Taladro percutor","brand":"Bosch","category":"POWER_TOOL","price":45000,"stock":12,"description":"650W"}'

curl http://localhost:8080/tools/all
```

El repositorio incluye también una colección de Postman
([`FerreteriaAPI.postman_collection.json`](FerreteriaAPI.postman_collection.json)) con todos
los endpoints, para importarla y probarlos desde ahí.

## Perfiles y bases de datos

El proyecto corre sobre **dos motores de base de datos distintos**, según el perfil de Spring activo (`spring.profiles.active`), sin ningún cambio de código entre uno y otro — solo configuración:

| Perfil | Motor | Uso previsto | `ddl-auto` |
|---|---|---|---|
| `dev` (default) | MySQL | Desarrollo local, con o sin Docker | `update` |
| `prod` | PostgreSQL | Despliegue (hoy sin instancia activa) | `validate` |

Ambos drivers JDBC (`mysql-connector-j` y `postgresql`) conviven en el `pom.xml` sin conflicto — Spring Boot resuelve cuál usar según el prefijo de la URL de conexión (`jdbc:mysql://` o `jdbc:postgresql://`), no según qué esté instalado.

Para activar el perfil de producción manualmente (por ejemplo, para probarlo en local antes de deployar):

```bash
SPRING_PROFILES_ACTIVE=prod DB_URL=jdbc:postgresql://localhost:5432/todocode_ferreteria DB_USERNAME=... DB_PASSWORD=... mvn spring-boot:run
```

## Configuración

`application.properties` no contiene secretos: cada valor sensible se lee primero de
una variable de entorno y cae a un default de desarrollo si no la encuentra
(`${VARIABLE:default}`) — así el mismo archivo sirve para local y para producción,
sin necesidad de gitignorearlo.

| Propiedad | Variable de entorno | Default (perfil `dev`) |
|---|---|---|
| `spring.profiles.active` | `SPRING_PROFILES_ACTIVE` | `dev` |
| `spring.datasource.url` | `DB_URL` | `jdbc:mysql://localhost:3306/todocode_ferreteria...` |
| `spring.datasource.username` | `DB_USERNAME` | `root` |
| `spring.datasource.password` | `DB_PASSWORD` | *(vacío)* |
| `app.cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `http://localhost:5500,http://127.0.0.1:5500,https://mdev-repos.github.io` |

El perfil `prod` no define defaults para estas variables a propósito: si falta alguna, la aplicación falla al arrancar en vez de conectarse silenciosamente a un lugar equivocado.

Para Docker Compose, estos valores se completan en un archivo `.env` local (gitignoreado, nunca se sube) — ver `.env.example` como plantilla.

En despliegue, estas variables se setean en el entorno real (servidor / contenedor
Docker) con los valores de producción — sin tocar código ni el archivo versionado.

## Proyecto relacionado

Frontend de práctica (vanilla HTML/CSS/JS) que consume esta API:
[`front_ferreteria__prueba_tecnica_todocode`](https://github.com/mdev-repos/front_ferreteria__prueba_tecnica_todocode).
Para usarlo, levantá esta API en local (ver [Cómo correrlo en local](#cómo-correrlo-en-local)),
clonar el repo del front y servirlo con un servidor estático propio (su README explica
cómo): detecta que corre en `localhost` y consume automáticamente el backend local.

## Roadmap

- [x] CRUD completo con arquitectura en capas
- [x] DTOs con `record` + validación (Jakarta Bean Validation)
- [x] `ResponseEntity` con status codes semánticamente correctos
- [x] CORS configurado por ambiente
- [x] Dockerización (`Dockerfile` multi-stage + `docker-compose.yml`)
- [x] Perfiles por ambiente — MySQL en desarrollo, PostgreSQL en producción
- [x] Deploy de punta a punta (Render + PostgreSQL gestionado + GitHub Pages) — dado de baja, el proyecto se ejecuta en local
- [x] Manejo centralizado de excepciones (`@ControllerAdvice`)
- [x] Refactor con programación funcional
- [ ] Tests unitarios e de integración

## Autor

**Matías Mazzitelli**
_Backend Developer — Java / Spring Boot_

[GitHub] https://github.com/mdev-repos · [LinkedIn] https://www.linkedin.com/in/mnm-dev