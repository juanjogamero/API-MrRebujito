# Arquitectura y reglas de MrRebujito

API REST de feria con **Java 17, Spring Boot 3.5, Spring Data JPA, PostgreSQL y Spring Security**.

## Entidades

- **Caseta**: titular de la caseta, aforo, tipo público o privado, relación con socios, productos y solicitudes.
- **Socio**: persona registrada que puede pertenecer a varias casetas.
- **Ayuntamiento**: entidad municipal con un número máximo de licencias aprobadas.
- **Administrador**: alta de otros administradores y ayuntamientos.
- **Producto**: comida o bebida, nombre y precio.
- **SolicitudLicencia**: ayuntamiento destinatario y estado PENDIENTE, APROBADA o RECHAZADA.

La aplicación conserva los paquetes de capas: entity, repository, service y controller, y añade DTOs para evitar publicar datos personales.

## Matriz de roles y permisos

| Endpoint / operación | Público | CASETA | SOCIO | AYUNTAMIENTO | ADMIN |
| --- | --- | --- | --- | --- | --- |
| POST /login | Sí | Sí | Sí | Sí | Sí |
| POST /caseta o POST /socio | Sí | Sí | Sí | Sí | Sí |
| GET /caseta, GET /caseta/{id} | Sí | Sí | Sí | Sí | Sí |
| GET /caseta/carta/{id}, GET /producto | Sí | Sí | Sí | Sí | Sí |
| GET /ayuntamiento | Sí | Sí | Sí | Sí | Sí |
| PUT/DELETE /caseta, GET /caseta/socios | No | Sí | No | No | No |
| POST/DELETE /caseta/socios/{idSocio} | No | Sí | No | No | No |
| POST /caseta/solicitud/{ayuntamientoId} | No | Sí | No | No | No |
| GET /solicitud/caseta | No | Sí | No | No | No |
| DELETE /solicitud/{id} | No | Sí | No | No | No |
| GET /socio/misCasetas, PUT/DELETE /socio | No | No | Sí | No | No |
| GET /solicitud/ayuntamiento | No | No | No | Sí | No |
| PUT /solicitud/aceptar/{id}, PUT /solicitud/rechazar/{id} | No | No | No | Sí | No |
| PUT/DELETE /ayuntamiento | No | No | No | Sí | No |
| POST /ayuntamiento, POST /administrador | No | No | No | No | Sí |

Las operaciones de lectura privada comprueban también que quien consulta sea titular de la caseta o ayuntamiento implicados.

## Reglas de negocio

1. **Límite de aforo.** Se pueden añadir socios mientras el número registrado sea inferior al aforo. La última plaza está permitida. No se permite registrar el mismo socio dos veces ni reducir el aforo por debajo de los miembros actuales.
2. **Solicitudes persistentes.** Cada solicitud se guarda con estado PENDIENTE y se relaciona con la caseta solicitante. Una caseta no puede tener a la vez una solicitud pendiente o aprobada para el mismo ayuntamiento.
3. **Transiciones.** Solo pueden resolverse solicitudes pendientes, y únicamente por el ayuntamiento destinatario. Una solicitud rechazada no puede aprobarse reutilizando ese registro.
4. **Cupo municipal.** La fila del ayuntamiento se bloquea transaccionalmente antes de aprobar. Si el número de licencias ya aprobadas alcanza el máximo, se rechaza la operación.
5. **Retirada.** Una caseta solo puede retirar solicitudes propias pendientes. La eliminación de una caseta con historial de licencias se bloquea para no perder referencias.
6. **Carta.** Una caseta solo puede crear, actualizar o eliminar productos de su propia carta.
7. **Credenciales.** El usuario no puede asignarse el rol a través del cuerpo del registro; el servidor lo determina. Las contraseñas se guardan con BCrypt y se excluyen de las respuestas.

## Autenticación

El formulario POST /login recibe username y password. Spring Security valida la contraseña con BCrypt y, en caso correcto, devuelve un JWT HS256 firmado con una clave externa al repositorio. La firma Base64 necesita al menos 32 bytes aleatorios, y el token expira por defecto a los 60 minutos.

El filtro JWT comprueba firma y expiración, recarga al usuario desde PostgreSQL y usa su rol de base de datos para autorizar las rutas. Los endpoints no contemplados quedan cerrados por defecto.

Configuración por variables de entorno:

| Variable | Descripción |
| --- | --- |
| JWT_SECRET | Obligatoria. Secreto aleatorio Base64, al menos 32 bytes |
| JWT_EXPIRATION_MS | Duración de token; por defecto 3600000 |
| DB_URL | Dirección JDBC PostgreSQL |
| DB_USER, DB_PASSWORD | Credenciales de la base |
| PORT | Puerto HTTP, por defecto 8080 |
| CORS_ALLOWED_ORIGINS | Orígenes permitidos para las peticiones web |
| APP_ADMIN_USER, APP_ADMIN_PASSWORD | Credenciales opcionales para crear el primer administrador local; contraseña 12+ caracteres |

No existe una cuenta de administrador por defecto con una contraseña pública.

## Códigos HTTP

- **200**: consulta o cambio correcto.
- **201**: recurso creado.
- **204**: eliminación/relación modificada sin respuesta.
- **400**: datos de entrada incorrectos.
- **401**: usuario no autenticado.
- **403**: rol insuficiente o recurso ajeno.
- **404**: recurso inexistente o no visible.
- **409**: aforo completo, licencia ya existente, cupo agotado u otras reglas incompatibles.

## Compatibilidad de datos anteriores

El proyecto original representaba los roles como valores ordinales y tenía un mapeo JPA distinto para la entidad común DomainEntity. Esta ampliación usa EnumType.STRING y MappedSuperclass. **No incorpora migraciones automáticas desde una base antigua**. Para comprobarla, usa una base PostgreSQL nueva; si vas a conservar datos, realiza copia de seguridad y una migración explícita antes del arranque. La actualización de esquema JPA no reemplaza Flyway/Liquibase en producción.

## Pruebas

- **JUnit + Mockito**: JWT, límite de aforo, creación persistente de solicitudes y cupo máximo municipal.
- **Spring Boot + MockMvc + H2**: arranque y permisos públicos/privados.
- **Node.js**: reglas de la demostración web (datos locales).
- **GitHub Actions**: verificación automática de Java y de la interfaz.

## Qué es la demo visual

La web de GitHub Pages no se conecta a la API Java ni comprueba tokens. Contiene datos ficticios y permite explorar escenarios desde distintos perfiles simulados. Se guarda localmente en el navegador y no emite licencias ni procesa datos municipales reales.

## Limitaciones y procedencia

El proyecto es académico; no está preparado para un despliegue público sin HTTPS, límites antiabuso, gestión de secretos, auditoría de cambios, migraciones SQL y revisión de seguridad. Es un fork del proyecto colaborativo [MrRebujito/MrRebujito-Backend](https://github.com/MrRebujito/MrRebujito-Backend), cuya autoría y evolución original se conservan.
