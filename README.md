# 🛡️ Authorization Module

Microservicio de alto rendimiento para la gestión de Identidades y Accesos (IAM), construido con **Spring Boot 4.0.0 y **Java 21**. Este módulo implementa una **Arquitectura Hexagonal (Ports & Adapters)** estricta para garantizar un desacoplamiento total entre las reglas de negocio, la capa de aplicación y la infraestructura.

## 🏗️ Stack Tecnológico
- **Core**: Java 21 & Spring Boot 4.0.0
- **Seguridad**: Spring Security 6 & JJWT (0.12.6)
- **Persistencia**: Spring Data JPA con PostgreSQL
- **Documentación**: SpringDoc OpenAPI 2.8.9 (Swagger UI)
- **Productividad**: Lombok & Jakarta Validation
- **Auditoría**: AOP (Aspect Oriented Programming) personalizada

## 📂 Estructura del Proyecto (Arquitectura Hexagonal)
La organización del código sigue estrictamente los principios de Ports & Adapters, dividiendo cada módulo en `domain`, `application` y `adapter`.

**Estructura General:**
```
src/main/java/com/autorization/autorization/
├── auth/                             # Módulo de Autenticación y Usuarios
│   ├── adapter/
│   │   ├── in/web/                   # REST Controllers (Input Adapters)
│   │   └── out/jpa/                  # JPA Repositories & Entities (Output Adapters)
│   ├── application/
│   │   └── services/                 # Implementación de Casos de Uso
│   └── domain/                       # Modelos del Dominio y Puertos (Interfaces)
├── audit/                            # Módulo de Auditoría (Refactorizado)
│   ├── adapter/
│   │   ├── in/aop/                   # Aspecto para intersección de eventos (@AuditLog)
│   │   ├── in/web/                   # REST Controller para consulta de logs
│   │   └── out/jpa/                  # Persistencia de logs (ActivityLogEntity)
│   ├── application/
│   │   └── services/                 # Lógica de negocio de auditoría
│   └── domain/                       # Modelo ActivityLog y Puertos (In/Out)
├── config/                           # Configuraciones Globales (Swagger, Cors)
└── security/                         # Infraestructura de Seguridad Transversal (JWT Filters)
```

## 🔐 Seguridad y Auditoría

### Control de Acceso (RBAC)
- **Roles y Permisos**: Modelo dinámico almacenado en base de datos.
- **JWT**: Tokens firmados que incluyen roles y permisos aplanados para validación rápida.
- **Protección**: Uso de `@PreAuthorize` en controladores.

### 🕵️ Auditoría Inteligente (@AuditLog)
El sistema cuenta con un módulo de auditoría desacoplado basado en AOP.
- **Anotación**: `@AuditLog(module = "USERS", action = "CREATE")`
- **Captura Automática**: Usuario, IP, Argumentos del método, Estado (Éxito/Error) y Timestamp.
- **Almacenamiento**: Persistencia asíncrona en PostgreSQL.
- **Consulta**: Endpoint REST con filtrado por fecha y módulo.

## 🚀 Configuración y Ejecución

### Requisitos Previos
- **JDK 21** instalado.
- **PostgreSQL** en puerto 5432.
- **Maven** (wrapper incluido).

### Variables de Entorno (PowerShell)
```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/autorization_db'
$env:SPRING_DATASOURCE_USERNAME = 'postgres'
$env:SPRING_DATASOURCE_PASSWORD = 'admin'
$env:JWT_SECRET = 'TU_SECRET_KEY_BASE64_MUY_LARGA_Y_SEGURA'
```

### Comandos Útiles
```bash
# Compilar sin tests
.\mvnw.cmd clean package -DskipTests

# Ejecutar aplicación
.\mvnw.cmd spring-boot:run
```

## 📖 Documentación de la API
Una vez iniciada la aplicación, accede a la documentación interactiva:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Endpoints Principales
- `POST /api/auth/login`: Obtención de Token.
- `GET /api/users`: Gestión de usuarios.
- `GET /api/audit-logs`: Consulta de historial de actividad (Filtros: `module`, `date`).

### ⚠️ Manejo de Errores
El API utiliza un formato estándar para todas las respuestas de error, facilitando la integración con clientes.

**Estructura de Respuesta (JSON):**
```json
{
  "timestamp": "2024-02-15T19:00:00Z",
  "requestId": "123e4567-e89b-12d3-a456-426614174000",
  "message": "Validation failed",
  "detail": "email: debe ser una dirección de correo electrónico con formato correcto"
}
```

**Códigos de Estado Comunes:**
- `400 Bad Request`: Datos de entrada inválidos.
- `401 Unauthorized`: Token JWT inválido o expirado.
- `403 Forbidden`: Acceso denegado por falta de permisos.
- `404 Not Found`: Recurso no existente.
- `409 Conflict`: Conflicto de datos (ej. email duplicado).
- `500 Internal Server Error`: Error inesperado (revisar logs con `requestId`).

## 🔧 Buenas Prácticas Implementadas
- **Clean Code**: Nombres descriptivos, métodos cortos.
- **SOLID**: Inyección de dependencias por constructor.
- **Hexagonal**: El dominio no depende de ningún framework (solo Java puro).
- **DTO Pattern**: Separación entre entidades de BD y objetos de transferencia.

---
**Desarrollado con ❤️ y Java 21**
