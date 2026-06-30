# Informe tecnico Semana 6 - Minimarket Plus

## Introducción

Durante esta semana se tomo como base el backend de Minimarket Plus trabajado en semanas anteriores y se adapto para validar mecanismos de autenticacion y autorizacion mediante pruebas automatizadas. El avance previo permitia trabajar con entidades clave como Producto, Inventario, Venta, Usuario y Carrito. Sobre esa base se incorporaron reglas de acceso por rol para proteger operaciones sensibles: administracion de productos, movimientos de inventario, eliminacion/modificacion de usuarios y generacion de ventas.

El proyecto quedo configurado con Maven, Spring Boot, Spring Security, JUnit 5, Mockito, Spring Security Test y JaCoCo. Las pruebas se ubicaron en `src/test/java`, siguiendo la estructura estandar del proyecto, y se ejecutaron con Maven Wrapper mediante el comando `./mvnw clean verify`.

## Configuracion del entorno de pruebas

El entorno de pruebas se configuro en `pom.xml` usando `spring-boot-starter-test` para JUnit 5 y Mockito, `spring-security-test` para simular usuarios autenticados con roles, y `jacoco-maven-plugin` para generar reportes de cobertura y exigir un minimo de 80% en clases criticas del servicio y seguridad.

Tambien se corrigio la codificacion de `src/main/resources/application.properties`, ya que el archivo estaba en ISO-8859-1 y Maven fallaba antes de ejecutar las pruebas. Luego se dejo el Maven Wrapper con permiso de ejecucion para poder validar el proyecto desde terminal.

## Reglas de autenticacion y autorizacion

Las reglas quedaron centralizadas en `SecurityConfig`. El sistema permite acceso publico solo a `/public/**`; el resto de rutas requiere autenticacion. Ademas, se agregaron restricciones por rol:

- `ADMIN`: puede crear, modificar y eliminar productos.
- `ADMIN`: puede registrar, modificar y eliminar movimientos de inventario.
- `CAJERO`: puede generar ventas.
- `ADMIN`: puede modificar y eliminar usuarios.
- `CLIENTE`: no puede ejecutar operaciones administrativas ni generar ventas.

Con esto se evita que usuarios autenticados sin permisos ejecuten operaciones criticas del negocio.

## Diseno de pruebas unitarias

Las pruebas se organizaron por responsabilidad:

- Producto: se valido que solo un usuario con rol `ADMIN` pueda modificar productos mediante el endpoint `/api/productos/{id}`.
- Inventario: se valido que solo `ADMIN` pueda registrar movimientos y que los movimientos tengan producto, cantidad positiva, fecha y tipo valido (`Entrada` o `Salida`).
- Venta: se valido que solo `CAJERO` pueda generar ventas y que la venta tenga usuario existente y stock suficiente.
- Usuario: se valido la busqueda de usuario para autenticacion, datos obligatorios y permisos de rol para registrar ventas.

Tambien se agregaron casos de error: cliente intentando modificar producto, cajero intentando registrar inventario, cliente intentando generar venta, usuario inexistente en autenticacion y ventas sin stock suficiente.

## Resultados obtenidos

La ejecucion se realizo con:

```bash
./mvnw clean verify
```

El resultado fue exitoso:

```text
Tests run: 49, Failures: 0, Errors: 0, Skipped: 0
All coverage checks have been met.
BUILD SUCCESS
```

Los reportes quedaron en:

- `target/surefire-reports/`
- `target/site/jacoco/index.html`
- `target/site/jacoco/jacoco.csv`
- `target/site/jacoco/jacoco.xml`

Cobertura de clases relevantes:

- `SecurityConfig`: 100.00%
- `CustomUserDetailsService`: 100.00%
- `UsuarioServiceImpl`: 90.00%
- `VentaServiceImpl`: 91.43%
- `CarritoServiceImpl`: 84.21%
- `InventarioServiceImpl`: 81.82%

## Analisis de resultados

Los resultados muestran que las reglas principales de autenticacion y autorizacion funcionan correctamente. Los endpoints protegidos rechazan con `403 Forbidden` cuando el usuario autenticado no tiene el rol requerido. Los casos exitosos permiten la operacion solo con el rol correcto, como `ADMIN` para productos e inventario y `CAJERO` para ventas.

Durante la preparacion se detectaron dos problemas. Primero, el proyecto no podia ejecutar Maven porque `application.properties` tenia una codificacion incompatible. Se corrigio el archivo a UTF-8. Segundo, la seguridad original solo exigia autenticacion general, por lo que cualquier usuario autenticado podia acceder a operaciones criticas. Se corrigio agregando reglas por metodo HTTP, ruta y rol en `SecurityConfig`.

La cobertura confirma que las clases centrales para la actividad quedaron cubiertas sobre el minimo requerido. JaCoCo aprobo la regla de cobertura y Surefire genero reportes XML/TXT para respaldar la ejecucion.

## Como las pruebas aportan a la calidad

Las pruebas reducen el riesgo de regresiones porque validan comportamiento funcional y reglas de seguridad antes de ejecutar el backend en un entorno real. Las pruebas de servicio revisan reglas de negocio como stock, datos obligatorios y movimientos de inventario. Las pruebas de seguridad revisan que los roles no autorizados no puedan modificar informacion critica. Esto permite detectar errores de configuracion o cambios inseguros en el codigo antes de entregar el sistema.

## Recomendaciones de mejora

Como mejora futura se recomienda implementar autenticacion por JWT para evitar depender de login por formulario en un backend REST, agregar pruebas de integracion con base H2 cargando usuarios y roles reales, documentar los endpoints con Swagger/OpenAPI y separar claramente perfiles de seguridad para desarrollo, pruebas y produccion. Tambien conviene agregar GitHub Actions para ejecutar `./mvnw clean verify` automaticamente en cada push.

## Capturas para insertar en el Word

Evidencia 1: terminal con la ejecucion del comando `./mvnw clean verify`. Insertar la captura donde se vea el comando ejecutado y el resultado final `BUILD SUCCESS`.

Evidencia 2: resumen de pruebas en consola. Insertar la captura donde aparezca `Tests run: 49, Failures: 0, Errors: 0, Skipped: 0`.

Evidencia 3: reporte JaCoCo principal. Abrir `target/site/jacoco/index.html` en el navegador e insertar la captura del resumen general.

Evidencia 4: cobertura del paquete de servicios. En el reporte JaCoCo abrir `com.minimarket.service.impl` e insertar la captura donde se vean `UsuarioServiceImpl`, `VentaServiceImpl`, `CarritoServiceImpl` e `InventarioServiceImpl`.

Evidencia 5: pruebas de seguridad. Insertar captura del archivo `src/test/java/com/minimarket/security/SecurityAuthorizationTest.java`, mostrando los casos donde `ADMIN`, `CAJERO` y `CLIENTE` reciben permisos distintos.

Evidencia 6: pruebas de autenticacion. Insertar captura del archivo `src/test/java/com/minimarket/security/CustomUserDetailsServiceTest.java`, mostrando usuario valido y usuario inexistente.
