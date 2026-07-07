# Checklist Semana 7 - OpenAPI

## Realizado

- Se mantuvo el proyecto acumulado de Minimarket Plus como base de trabajo.
- Se agrego `springdoc-openapi-starter-webmvc-ui` al `pom.xml`.
- Se creo configuracion general OpenAPI para la API Minimarket Plus.
- Se habilito acceso publico a Swagger UI y `/v3/api-docs` sin cambiar las reglas de seguridad de los endpoints del negocio.
- Se documento `ProductoController` con `@Tag`, `@Operation`, `@ApiResponses`, parametros, schemas y ejemplos.
- Se documento `CarritoController` con `@Tag`, `@Operation`, `@ApiResponses`, parametros, schemas y ejemplos.
- Se agregaron descripciones y ejemplos OpenAPI en los modelos `Producto`, `Carrito` y `Categoria`.
- Se genero el archivo `docs/openapi-minimarket.json` desde `http://localhost:8080/v3/api-docs`.
- Se verifico Swagger UI en `http://localhost:8080/swagger-ui.html`, con redireccion final correcta a `/swagger-ui/index.html`.
- Se valido el proyecto con `./mvnw clean verify`: 49 pruebas ejecutadas, 0 fallas, 0 errores y JaCoCo aprobado.
- Se actualizo el `README.md` con instrucciones para iniciar el proyecto, abrir Swagger UI y exportar el JSON OpenAPI.

## Faltante para entregar

- Importar `docs/openapi-minimarket.json` en Postman.
- Ejecutar pruebas desde Postman contra los endpoints documentados y tomar capturas.
- Tomar capturas de Swagger UI mostrando los grupos `Productos` y `Carrito`.
- Completar el formato Word de respuesta con resumen tecnico, analisis, evidencias y reflexion.
- Adjuntar o mencionar el archivo JSON exportado en la evidencia.
- Subir el codigo actualizado a GitHub.
- Pegar el enlace del repositorio GitHub en la entrega del AVA.

## Nota de ruta

El documento de actividad usa `/api/carritos` como ejemplo, pero el proyecto real expone el controlador en `/api/carrito`. Se mantuvo la ruta real para no romper compatibilidad con el backend existente.
