# 🔧 Ferretería API

API REST para la gestión de inventario de una ferretería — alta, consulta, actualización
y baja de herramientas. Construida con **Spring Boot** siguiendo una arquitectura en
capas (Controller → Service → Repository) con separación estricta entre entidades de
persistencia y contrato de API mediante **DTOs**.

Proyecto en evolución continua: nació como ejercicio / prueba técnica y se usa como base de
práctica personal para ir sumando buenas prácticas (seguridad, testing, Docker,
deploy) a medida que se incorporan a mi formación.

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-brightgreen?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-build-C71A36?logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-prod-336791?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![Status](https://img.shields.io/badge/status-en%20desarrollo-yellow)

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
| Base de datos | MySQL (desarrollo) · PostgreSQL (producción) — ver [Perfiles y bases de datos](#perfiles-y-bases-de-datos) |
| Validación | Jakarta Bean Validation |
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
   ├─▶ Mapper       → traduce Entity ⇄ DTO
   │
   ▼
Repository (Spring Data JPA) → persistencia
   │
   ▼
MySQL (dev) / PostgreSQL (prod)
```

```
src/main/java/com/mdev/ferreteria
├── config          # configuración transversal (CORS, etc.)
├── controller       # capa REST — solo DTOs y ResponseEntity
├── dto
│   ├── request        # contratos de entrada (records, con validación)
│   └── response         # contratos de salida (records)
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

## Cómo correrlo en local

### Opción A — Docker Compose (recomendado)

**Prerrequisitos**: Docker.

```bash
cd ferreteria
cp .env.example .env   # completar valores si hace falta
docker compose up -d --build
```

Levanta la API **y** su base de datos (MySQL) juntas, con las variables de entorno ya resueltas. La API queda disponible en `http://localhost:8080`.

### Opción B — manual, sin Docker

Para quien prefiera (o necesite) correrlo "a la antigua", sin contenedores.

**Prerrequisitos**: JDK 25, Maven, MySQL corriendo en `localhost:3306`.

```bash
# 1. Crear la base de datos
mysql -u root -p -e "CREATE DATABASE todocode_ferreteria;"

# 2. Configurar credenciales (ver sección Configuración)

# 3. Levantar la aplicación
cd ferreteria
mvn spring-boot:run
```

Ambas opciones arrancan con el perfil `dev` (MySQL) por defecto — ver la sección siguiente.

## Perfiles y bases de datos

El proyecto corre sobre **dos motores de base de datos distintos**, según el perfil de Spring activo (`spring.profiles.active`), sin ningún cambio de código entre uno y otro — solo configuración:

| Perfil | Motor | Uso previsto | `ddl-auto` |
|---|---|---|---|
| `dev` (default) | MySQL | Desarrollo local, con o sin Docker | `update` |
| `prod` | PostgreSQL | Despliegue | `validate` |

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
| `app.cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `http://localhost:5500,http://127.0.0.1:5500` |

El perfil `prod` no define defaults para estas variables a propósito: si falta alguna, la aplicación falla al arrancar en vez de conectarse silenciosamente a un lugar equivocado.

Para Docker Compose, estos valores se completan en un archivo `.env` local (gitignoreado, nunca se sube) — ver `.env.example` como plantilla.

En despliegue, estas variables se setean en el entorno real (servidor / contenedor
Docker) con los valores de producción — sin tocar código ni el archivo versionado.

## Proyecto relacionado

Frontend de práctica (vanilla HTML/CSS/JS) que consume esta API:
[`front_ferreteria__prueba_tecnica_todocode`](../front_ferreteria__prueba_tecnica_todocode).
Detecta solo desde dónde se lo sirve y elige el backend correspondiente, sin ninguna
configuración manual — dos formas de usarlo:

- **En local**: clonar el repo del front y levantarlo con un servidor estático propio
  (ver su README) junto con esta API corriendo en la misma máquina (Docker Compose o
  manual, ver [Cómo correrlo en local](#cómo-correrlo-en-local)) → el front va a
  consumir ese backend local automáticamente.
- **Ya funcionando, sin instalar nada**:
  **https://mdev-repos.github.io/front_ferreteria__prueba_tecnica_todocode/** → el
  front consume directamente el backend ya desplegado en producción (Render).


## Roadmap

- [x] CRUD completo con arquitectura en capas
- [x] DTOs con `record` + validación (Jakarta Bean Validation)
- [x] `ResponseEntity` con status codes semánticamente correctos
- [x] CORS configurado por ambiente
- [x] Dockerización (`Dockerfile` multi-stage + `docker-compose.yml`)
- [x] Perfiles por ambiente — MySQL en desarrollo, PostgreSQL en producción
- [ ] Deploy (backend + frontend)
- [ ] Manejo centralizado de excepciones (`@ControllerAdvice`)
- [ ] Refactor con programación funcional
- [ ] Spring Security (autenticación/autorización)
- [ ] Tests unitarios e de integración

## Autor

**Matías Mazzitelli**
_Backend Developer — Java / Spring Boot_

[GitHub] https://github.com/mdev-repos · [LinkedIn] https://www.linkedin.com/in/mnm-dev