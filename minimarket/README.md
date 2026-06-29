# Minimarket Plus - Semana 6 Backend II

Proyecto Spring Boot usado para la actividad sumativa de Semana 6: aplicacion de autenticacion, control de acceso por roles y validacion mediante pruebas unitarias con JUnit, Mockito, Spring Security Test y JaCoCo.

## Stack

- Java 17
- Spring Boot 3.4.1
- Maven Wrapper
- Spring Web
- Spring Data JPA
- Spring Security
- H2 Database
- JUnit 5
- Mockito
- Spring Security Test
- JaCoCo

## Roles configurados

- `ADMIN`: puede crear, actualizar y eliminar productos, inventario y usuarios.
- `CAJERO`: puede generar ventas.
- `CLIENTE`: puede consultar recursos autenticados, pero no modificar productos, inventario, usuarios ni generar ventas.

Las reglas estan en `src/main/java/com/minimarket/security/config/SecurityConfig.java`.

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

Resultado validado:

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
