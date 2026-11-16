<div align="center">
<a href="https://juanjogamero.github.io/API-MrRebujito/"><img src="docs/readme-cover.svg" width="100%" alt="MrRebujito: gestión de feria, casetas y licencias"></a>

# MrRebujito · Sistema de Gestión de Feria

**Una API REST para coordinar casetas, socios, cartas de productos y licencias municipales.**

[**Abrir plataforma interactiva**](https://juanjogamero.github.io/API-MrRebujito/) · [**Ver código Java**](src/main/java/mrRebujito/MrRebujito) · [**Arquitectura y reglas**](Documentacion/ARQUITECTURA.md)

Java 17 · Spring Boot · PostgreSQL · JWT · GitHub Pages
</div>

---

## Descubre el proyecto

**MrRebujito** nació como proyecto colaborativo de segundo curso de DAM, con el objetivo de digitalizar la gestión de casetas de feria. La API permite gestionar casetas públicas y privadas, socios, comidas y bebidas, así como solicitudes de licencia ante un ayuntamiento.

La **plataforma interactiva** permite explorar casetas, abrir fichas, consultar cartas, añadir socios dentro del aforo y simular aprobaciones o rechazos según el cupo municipal.

> **Importante:** la web de GitHub Pages funciona de forma independiente. Sus datos son ficticios y los cambios se conservan únicamente en el navegador. No utiliza PostgreSQL, no autentica usuarios reales ni emite licencias oficiales.

## Funcionalidades principales

| Módulo | Funcionalidad |
| --- | --- |
| Casetas | Registro y consulta, aforo y carácter público/privado |
| Socios | Asociación y retirada de miembros, con control de capacidad |
| Productos | Carta de comidas y bebidas con precios |
| Licencias | Solicitudes, aprobación, rechazo y control de cupos |
| Ayuntamientos | Gestión de licencias disponibles y solicitudes recibidas |
| Seguridad | JWT y cuatro roles: CASETA, SOCIO, AYUNTAMIENTO y ADMIN |

## Ejecutar el backend

Requisitos: **Java 17** y **Docker**.

**macOS / Linux**

~~~bash
docker compose up -d
export JWT_SECRET="$(openssl rand -base64 48)"
./mvnw spring-boot:run
~~~

**Windows PowerShell**

~~~powershell
docker compose up -d
$env:JWT_SECRET = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(48))
.\mvnw.cmd spring-boot:run
~~~

El servicio arranca en **http://localhost:8080** y la documentación se encuentra en [**Swagger UI**](http://localhost:8080/swagger-ui.html).

Registra una caseta o socio desde **POST /caseta** o **POST /socio**, inicia sesión mediante **POST /login** e introduce el token JWT en la cabecera Authorization con el formato Bearer TOKEN.

Para crear el **primer administrador local** sin una contraseña predeterminada, proporciona las variables de entorno APP_ADMIN_USER y APP_ADMIN_PASSWORD (12 caracteres como mínimo) antes del arranque. Solo el administrador puede registrar ayuntamientos.

## Pruebas y calidad

[![Java API](https://github.com/juanjogamero/API-MrRebujito/actions/workflows/java.yml/badge.svg)](https://github.com/juanjogamero/API-MrRebujito/actions/workflows/java.yml)
[![Demo web](https://github.com/juanjogamero/API-MrRebujito/actions/workflows/demo.yml/badge.svg)](https://github.com/juanjogamero/API-MrRebujito/actions/workflows/demo.yml)

- **JUnit y Mockito:** reglas de aforo, licencias y validación de JWT.
- **MockMvc y H2:** arranque de Spring Boot y autorización de endpoints.
- **Node.js:** reglas de negocio de la demo, formularios y datos locales.
- **GitHub Actions:** compilación y pruebas por cada cambio relevante.

~~~bash
./mvnw verify
node scripts/check-demo.mjs
~~~

## Estructura del repositorio

~~~text
src/main/java/mrRebujito/MrRebujito/
  controller/    Rutas HTTP
  service/       Reglas de negocio y transacciones
  repository/    Consultas JPA
  entity/        Modelo de feria
  security/      Spring Security y JWT
  dto/           Respuestas sin datos sensibles
src/test/        Pruebas
docs/            Interfaz estática de GitHub Pages
Documentacion/   Arquitectura y reglas de acceso
docker-compose.yml
~~~

**Compatibilidad:** la ampliación cambia el mapeo de herencia JPA y el formato de los roles. Si tienes una base PostgreSQL antigua, debes realizar una copia y una migración antes de utilizarla. La configuración automática del esquema es solo para desarrollo local.

## Un proyecto en equipo

Este repositorio es un **fork de [MrRebujito/MrRebujito-Backend](https://github.com/MrRebujito/MrRebujito-Backend)**. La versión original se desarrolló como trabajo académico colaborativo; esta ampliación mantiene el dominio y añade pruebas, seguridad, documentación y una demostración visual para portfolio.

---

<div align="center">

**[Explorar MrRebujito en el navegador](https://juanjogamero.github.io/API-MrRebujito/)**

<sub>Plataforma de ejemplo · Sin gestiones municipales reales</sub>

</div>
