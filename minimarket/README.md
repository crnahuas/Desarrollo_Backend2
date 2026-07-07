# Minimarket Plus - Semana 7 Backend II

Proyecto Spring Boot usado para la actividad formativa de Semana 7: documentacion de endpoints REST con OpenAPI y Swagger UI sobre el backend Minimarket Plus trabajado en semanas anteriores.

## Stack

- Java 17
- Spring Boot 3.4.1
- Maven Wrapper
- Spring Web
- Spring Data JPA
- Spring Security
- H2 Database
- Springdoc OpenAPI
- JUnit 5
- Mockito
- Spring Security Test
- JaCoCo

## Documentacion OpenAPI

La documentacion interactiva se genera con `springdoc-openapi-starter-webmvc-ui`.

Endpoints de documentacion:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- JSON OpenAPI: `http://localhost:8080/v3/api-docs`
- JSON exportado para Postman: `docs/openapi-minimarket.json`

Controladores documentados con mayor detalle:

- `ProductoController`: `GET`, `POST`, `PUT` y `DELETE` sobre `/api/productos`.
- `CarritoController`: `GET`, `POST`, `PUT` y `DELETE` sobre `/api/carrito`.

El resto de controladores tambien aparece en el documento OpenAPI generado automaticamente por Springdoc.

## Roles configurados

- `ADMIN`: puede crear, actualizar y eliminar productos, inventario y usuarios.
- `CAJERO`: puede generar ventas.
- `CLIENTE`: puede consultar recursos autenticados, pero no modificar productos, inventario, usuarios ni generar ventas.

Las reglas estan en `src/main/java/com/minimarket/security/config/SecurityConfig.java`.

Swagger UI y `/v3/api-docs` quedan publicos para poder revisar la documentacion sin iniciar sesion. Los endpoints funcionales del backend mantienen las reglas de seguridad configuradas.

## Ejecucion local

Desde la carpeta del proyecto:

```bash
./mvnw spring-boot:run
```

Luego abrir:

```text
http://localhost:8080/swagger-ui.html
```

Para exportar nuevamente el contrato OpenAPI:

```bash
curl -s http://localhost:8080/v3/api-docs -o docs/openapi-minimarket.json
```

## Pruebas implementadas

- `CarritoServiceImplTest`: valida stock suficiente, cantidad valida, usuario/producto asociados y consultas del carrito.
- `InventarioServiceImplTest`: valida movimientos de entrada/salida, cantidad, fecha y producto asociado.
- `VentaServiceImplTest`: valida registro de venta, usuario existente, stock suficiente y calculo total.
- `UsuarioServiceImplTest`: valida datos obligatorios y permisos para registrar ventas.
- `CustomUserDetailsServiceTest`: valida autenticacion exitosa y error por usuario inexistente.
- `SecurityAuthorizationTest`: valida autorizacion por roles en endpoints de producto, inventario, venta y usuario.

## Ejecucion

```bash
./mvnw clean verify
```

Resultado validado despues de agregar OpenAPI:

- `Tests run: 49, Failures: 0, Errors: 0, Skipped: 0`
- `All coverage checks have been met.`
- `BUILD SUCCESS`

## Evidencia generada

- Reportes Surefire: `target/surefire-reports/`
- Reporte JaCoCo HTML: `target/site/jacoco/index.html`
- Reporte JaCoCo CSV/XML: `target/site/jacoco/jacoco.csv` y `target/site/jacoco/jacoco.xml`

Cobertura de clases clave:

- `SecurityConfig`: 100.00%
- `CustomUserDetailsService`: 100.00%
- `UsuarioServiceImpl`: 90.00%
- `VentaServiceImpl`: 91.43%
- `CarritoServiceImpl`: 84.21%
- `InventarioServiceImpl`: 81.82%
