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
![Status](https://img.shields.io/badge/status-en%20desarrollo-yellow)

---

## Índice

- [Contexto del ejercicio](#contexto-del-ejercicio)
- [Stack técnico](#stack-técnico)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [Endpoints](#endpoints)
- [Cómo correrlo en local](#cómo-correrlo-en-local)
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
| Base de datos | MySQL |
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
MySQL
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

**Prerrequisitos**: JDK 25, Maven, MySQL corriendo en `localhost:3306`.

```bash
# 1. Crear la base de datos
mysql -u root -p -e "CREATE DATABASE todocode_ferreteria;"

# 2. Configurar credenciales (ver sección Configuración)

# 3. Levantar la aplicación
cd ferreteria
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Configuración

`application.properties` no contiene secretos: cada valor sensible se lee primero de
una variable de entorno y cae a un default de desarrollo si no la encuentra
(`${VARIABLE:default}`) — así el mismo archivo sirve para local y para producción,
sin necesidad de gitignorearlo.

| Propiedad | Variable de entorno | Default (local) |
|---|---|---|
| `spring.datasource.url` | `DB_URL` | `jdbc:mysql://localhost:3306/todocode_ferreteria...` |
| `spring.datasource.username` | `DB_USERNAME` | `root` |
| `spring.datasource.password` | `DB_PASSWORD` | *(vacío)* |
| `app.cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` | `http://localhost:5500,http://127.0.0.1:5500` |

En despliegue, estas variables se setean en el entorno real (servidor / contenedor
Docker) con los valores de producción — sin tocar código ni el archivo versionado.

## Proyecto relacionado

Frontend de práctica (vanilla HTML/CSS/JS) que consume esta API:
[`front_ferreteria__prueba_tecnica_todocode`](../front_ferreteria__prueba_tecnica_todocode)

Con el proyecto corriendo correctamente en local, se puede usar la pagina desplegada en pages para interactuar con la API

#### https://mdev-repos.github.io/front_ferreteria__prueba_tecnica_todocode/


## Roadmap

- [x] CRUD completo con arquitectura en capas
- [x] DTOs con `record` + validación (Jakarta Bean Validation)
- [x] `ResponseEntity` con status codes semánticamente correctos
- [x] CORS configurado por ambiente
- [ ] Dockerización
- [ ] Deploy (backend + frontend)
- [ ] Manejo centralizado de excepciones (`@ControllerAdvice`)
- [ ] Refactor con programación funcional
- [ ] Spring Security (autenticación/autorización)
- [ ] Tests unitarios e de integración

## Autor

**Matías Mazzitelli**
_Backend Developer — Java / Spring Boot_

[GitHub] https://github.com/mdev-repos · [LinkedIn] https://www.linkedin.com/in/mnm-dev