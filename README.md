# Biblioteca LibrosYMas — Sistema de gestión por consola

Aplicación Java 21 (Maven) para administrar libros, clientes, préstamos, devoluciones,
historial, **multas por retraso**, **reservas** y un **reporte general**. Los datos se
guardan en archivos CSV y la aplicación se ejecuta dentro de un contenedor Docker con volumen persistente.

Integrantes: Jerónimo Morales (libros / inventario) · Nicole Duque (clientes / préstamos)

---

## 1. Ejecutar con Docker (forma oficial del taller)

Requisito: tener Docker Desktop instalado y abierto.

```bash
# 1) Construir la imagen (una sola vez, o cada vez que cambie el código)
docker build -t biblioteca .

# 2) Ejecutar la aplicación (único comando)
docker run -it --rm -v biblioteca-datos:/app/datos biblioteca
```

| Parte del comando | Para qué sirve |
|---|---|
| `-it` | Modo interactivo: permite escribir en el menú de consola |
| `--rm` | Borra el contenedor al salir (los datos NO se pierden, están en el volumen) |
| `-v biblioteca-datos:/app/datos` | Volumen con nombre donde se guardan los CSV. Docker lo crea solo la primera vez |

### Comprobar la persistencia

1. Ejecutar `docker run ...`, registrar un libro o un préstamo y salir con `0`.
2. Volver a ejecutar el mismo `docker run ...`: aparece `Datos cargados desde: /app/datos` y la información sigue ahí.
3. Ver el volumen: `docker volume ls` y `docker volume inspect biblioteca-datos`.

Para ver los archivos directamente en el computador, se puede usar una carpeta local en vez del volumen con nombre:

```bash
# PowerShell / Linux / macOS
docker run -it --rm -v "${PWD}/datos:/app/datos" biblioteca
```

Para reiniciar el sistema desde cero: `docker volume rm biblioteca-datos`.

## 2. Ejecutar desde IntelliJ (desarrollo)

Ejecutar `com.librosymas.Main`. Los datos se guardan en la carpeta `datos/` del proyecto
(ignorada por Git). La carpeta se puede cambiar con la variable de entorno `DATA_DIR`.

## 3. Menú

| Opción | Función |
|---|---|
| 1–4 | Registrar libro, listar disponibles, listar todos, cambiar estado |
| 5–6 | Registrar y listar clientes |
| 7–9 | Préstamo, devolución, préstamos activos |
| 10 | Historial de préstamos |
| 11 | Consultar multas de un cliente |
| 12 | Pagar multas de un cliente |
| 13 | Registrar reserva |
| 14 | Consultar reservas de un libro |
| 15 | Reporte general del sistema |
| 0 | Salir |

## 4. Reglas de negocio

**Préstamos.** Cada cliente puede tener un solo libro a la vez. El plazo es de
`Prestamo.DIAS_PRESTAMO` = 15 días; la fecha límite se calcula automáticamente.

**Multas.** Si la devolución ocurre después de la fecha límite:
`multa = días de retraso × Multa.VALOR_POR_DIA` ($1.000). La multa se acumula en el
perfil del cliente. Al devolver se puede escribir una fecha (AAAA-MM-DD) para simular
retrasos durante la sustentación; con Enter se usa la fecha de hoy.

**Reservas.** Solo se reservan libros PRESTADOS (o que ya tienen lista de espera).
La lista se atiende en orden de llegada (FIFO). Al devolver el libro, la consola indica
quién es el siguiente y solo ese cliente puede prestarlo. Al recibirlo, su reserva se elimina.
No se puede reservar el libro que el cliente ya tiene ni reservar dos veces el mismo.

**Estados.** El estado PRESTADO solo cambia mediante préstamo (opción 7) o devolución (opción 8),
nunca a mano (opción 4). Un libro con reservas no se puede retirar.

**Reporte.** Total de libros, clientes, préstamos activos y préstamos históricos; cliente con
más préstamos y libro menos solicitado (sin contar los retirados). Si hay empate, se muestran todos.

## 5. Persistencia

Clase `servicio/Persistencia.java`. Guarda después de cada operación que modifica datos
(no solo al salir), así nada se pierde si el contenedor se detiene. Escribe a un archivo
temporal y luego lo reemplaza, para no dejar archivos a medias.

| Archivo | Contenido |
|---|---|
| `libros.csv` | id, título, autor, editorial, año, categoría, estado |
| `clientes.csv` | documento, nombre, teléfono, dirección |
| `prestamos.csv` | historial completo (fecha de devolución vacía = préstamo activo) |
| `reservas.csv` | listas de espera en orden de solicitud |
| `multas.csv` | multas pagadas y pendientes |

## 6. Estructura

```
src/main/java/com/librosymas/
├── Main.java                  # arma los servicios, carga datos y lanza el menú
├── modelo/                    # Libro, Cliente, Prestamo, Multa, Reserva, Categoria, EstadoLibro
├── servicio/                  # Inventario, GestionClientes, ServicioPrestamos,
│                              # ServicioMultas, ServicioReservas, ServicioReportes, Persistencia
└── ui/Menu.java               # menú de consola y validación de entradas
Dockerfile                     # build en dos etapas (Maven -> JRE 21)
```
