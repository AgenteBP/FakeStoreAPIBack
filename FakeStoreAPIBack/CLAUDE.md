# CLAUDE.md — Backend Tienda Online

## 1. Descripción del proyecto
API REST para una tienda online. Gestiona usuarios, productos y compras.
El frontend es una app en React/Vite ya existente y consume esta API.

Objetivo: [ej. "proyecto personal para practicar backend y eventualmente ofrecerlo a un cliente"]

## 2. Stack tecnológico
- Lenguaje: Java 21
- Framework: Spring Boot 4.0.0
- Gestor de dependencias: Maven
- Base de datos: MySQL
- ORM: Spring Data JPA + Hibernate
- Migraciones: pendiente
- Autenticación: Spring Security + JWT

## 3. Estructura de carpetas
src/main/java/com/[paquete]/
├── controller/     # Endpoints REST
├── service/        # Lógica de negocio
├── repository/     # Interfaces JPA
├── models/  # Entidades JPA
├── DTO/            # Objetos de transferencia (request/response)
├── config/         # Configuración ( CORS, etc.)
├── Enums/         # Enumerado (roles, estados, etc)
├── Security/         # Configuración de seguridad
└── exception/      # Manejo de excepciones personalizadas

## 4. Convenciones de código
- Nombres de clases: PascalCase (ej. `UsuarioService`, `ProductoController`)
- Nombres de métodos y variables: camelCase
- Los controllers NO contienen lógica de negocio, solo delegan al service
- Los repositories solo acceden a datos, sin lógica
- Usar DTOs para entrada/salida de la API — nunca exponer las entidades JPA directamente en los endpoints
- Manejo de errores centralizado con `@ControllerAdvice`
- Todo endpoint nuevo debe devolver códigos HTTP correctos (200, 201, 400, 404, 409, etc.)
- Código simple, con comentarios solo donde la intención no sea obvia.
- Si un pedido choca con estas reglas o con los skills del proyecto, avisar en vez de ignorarlo en silencio.

## 5. Convenciones de base de datos (MySQL)
- Nombres de tablas: snake_case, en plural (ej. `usuarios`, `detalle_compra`)
- Nombres de columnas: snake_case (ej. `fecha_creacion`, `usuario_id`)
- Toda tabla tiene: `id` (PK autoincremental), `fecha_creacion`, `fecha_actualizacion`
- Las FKs no se borran en cascada salvo que se indique explícitamente — por defecto, prevenir el borrado o desactivar registros (soft delete) en vez de eliminarlos
- El precio en `detalle_compra` es histórico: no se actualiza si cambia el precio del producto
- Antes de generar o modificar el schema (nuevas tablas, columnas, migraciones), mostrar el SQL propuesto y esperar confirmación
- Toda consulta personalizada en los repositories (@Query) debe escribirse en SQL nativo de MySQL (nativeQuery = true), nunca en JPQL. Ejemplo: @Query(value = "SELECT * FROM usuarios WHERE activo = true", nativeQuery = true) en vez de @Query("SELECT u FROM Usuario u WHERE u.activo = true")

## 6. Comandos frecuentes
```powershell
# Levantar el proyecto
./mvnw spring-boot:run

# Correr tests
./mvnw test

# Compilar
./mvnw clean install

# Conectarse a MySQL local
mysql -u [root] -p [DB_FAKE_STORE]
```

## 7. Flujo de Git
- Ramas: `feature/[nombre-corto]`, `fix/[nombre-corto]`
- Commits pequeños y descriptivos, en español
- No hacer `git push` sin que yo lo confirme antes
- No mezclar cambios de backend y frontend en el mismo commit

## 8. Qué NO hacer sin preguntar antes
- No modificar el schema de la base de datos sin mostrar el SQL antes
- No agregar dependencias nuevas al proyecto sin explicar por qué
- No borrar código existente "para limpiar" sin avisar
- No asumir reglas de negocio no explicadas (ej. si un producto se puede comprar con stock 0, permisos de admin, etc.) — preguntar si hay dudas

## 9. Contexto adicional
- Vengo de un perfil backend Java/Spring Boot, pero también trabajo con React/Vite en el frontend de este mismo proyecto
- Prefiero explicaciones paso a paso, con el "por qué" detrás de cada decisión técnica
- Dirigirse al usuario por su nombre, Braian, en cada respuesta.
