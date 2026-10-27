package com.librosymas.servicio;

import com.librosymas.modelo.Categoria;
import com.librosymas.modelo.Cliente;
import com.librosymas.modelo.EstadoLibro;
import com.librosymas.modelo.Libro;
import com.librosymas.modelo.Multa;
import com.librosymas.modelo.Prestamo;
import com.librosymas.modelo.Reserva;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda y carga toda la información del sistema en archivos de texto (formato CSV con ';').
 *
 * Carpeta de datos:
 *  - Variable de entorno DATA_DIR si existe (en Docker: /app/datos, montada como volumen).
 *  - Si no existe, la carpeta "datos" junto al proyecto (al ejecutar desde IntelliJ).
 *
 * Archivos: libros.csv, clientes.csv, prestamos.csv, reservas.csv, multas.csv
 */
public class Persistencia {

    private static final String SEP = ";";

    private final Path carpeta;
    private final Inventario inventario;
    private final GestionClientes gestionClientes;
    private final ServicioPrestamos servicioPrestamos;
    private final ServicioReservas servicioReservas;
    private final ServicioMultas servicioMultas;

    public Persistencia(Inventario inventario, GestionClientes gestionClientes,
                        ServicioPrestamos servicioPrestamos, ServicioReservas servicioReservas,
                        ServicioMultas servicioMultas) {
        String dir = System.getenv("DATA_DIR");
        this.carpeta = Paths.get(dir == null || dir.isBlank() ? "datos" : dir);
        this.inventario = inventario;
        this.gestionClientes = gestionClientes;
        this.servicioPrestamos = servicioPrestamos;
        this.servicioReservas = servicioReservas;
        this.servicioMultas = servicioMultas;
    }

    public Path getCarpeta() {
        return carpeta.toAbsolutePath();
    }

    /**
     * Indica si ya hay datos guardados de una ejecución anterior.
     */
    public boolean existenDatos() {
        return Files.exists(carpeta.resolve("libros.csv"))
                || Files.exists(carpeta.resolve("clientes.csv"));
    }

    // =====================================================
    //  GUARDAR
    // =====================================================

    /**
     * Escribe todos los archivos. Se llama después de cada operación que modifica datos,
     * así nada se pierde aunque el contenedor se detenga de golpe.
     */
    public void guardar() {
        try {
            Files.createDirectories(carpeta);

            List<String> libros = new ArrayList<>();
            libros.add("id;titulo;autor;editorial;anio;categoria;estado");
            for (Libro l : inventario.listarTodos()) {
                libros.add(unir(l.getId(), l.getTitulo(), l.getAutor(), l.getEditorial(),
                        String.valueOf(l.getAnioPublicacion()), l.getCategoria().name(), l.getEstado().name()));
            }
            escribir("libros.csv", libros);

            List<String> clientes = new ArrayList<>();
            clientes.add("documento;nombre;telefono;direccion");
            for (Cliente c : gestionClientes.listarTodos()) {
                clientes.add(unir(c.getDocumento(), c.getNombreCompleto(), c.getTelefono(), c.getDireccion()));
            }
            escribir("clientes.csv", clientes);

            List<String> prestamos = new ArrayList<>();
            prestamos.add("idLibro;documento;fechaPrestamo;fechaLimite;fechaDevolucion");
            for (Prestamo p : servicioPrestamos.listarHistorial()) {
                prestamos.add(unir(p.getIdLibro(), p.getDocumentoCliente(), texto(p.getFechaPrestamo()),
                        texto(p.getFechaLimite()), texto(p.getFechaDevolucion())));
            }
            escribir("prestamos.csv", prestamos);

            List<String> reservas = new ArrayList<>();
            reservas.add("idLibro;documento;fechaSolicitud");
            for (Reserva r : servicioReservas.listarTodas()) {
                reservas.add(unir(r.getIdLibro(), r.getDocumentoCliente(), r.getFechaSolicitud().toString()));
            }
            escribir("reservas.csv", reservas);

            List<String> multas = new ArrayList<>();
            multas.add("documento;idLibro;diasRetraso;valor;fecha;pagada");
            for (Multa m : servicioMultas.listarTodas()) {
                multas.add(unir(m.getDocumentoCliente(), m.getIdLibro(), String.valueOf(m.getDiasRetraso()),
                        String.valueOf(m.getValor()), texto(m.getFechaGeneracion()), String.valueOf(m.estaPagada())));
            }
            escribir("multas.csv", multas);

        } catch (IOException e) {
            System.out.println("ADVERTENCIA: no se pudieron guardar los datos en " + getCarpeta()
                    + " (" + e.getMessage() + ")");
        }
    }

    // =====================================================
    //  CARGAR
    // =====================================================

    /**
     * Lee los archivos y reconstruye el estado del sistema.
     * El orden importa: primero libros y clientes, luego lo que depende de ellos.
     */
    public void cargar() {
        try {
            for (String[] c : leer("libros.csv")) {
                Libro libro = new Libro(c[0], c[1], c[2], c[3], Integer.parseInt(c[4]), Categoria.valueOf(c[5]));
                libro.cambiarEstado(EstadoLibro.valueOf(c[6]));
                inventario.registrarLibro(libro);
            }
            for (String[] c : leer("clientes.csv")) {
                gestionClientes.registrarCliente(new Cliente(c[0], c[1], c[2], c[3]));
            }
            for (String[] c : leer("prestamos.csv")) {
                servicioPrestamos.cargarPrestamo(new Prestamo(c[0], c[1],
                        fecha(c[2]), fecha(c[3]), fecha(c[4])));
            }
            for (String[] c : leer("reservas.csv")) {
                servicioReservas.cargarReserva(new Reserva(c[0], c[1], LocalDateTime.parse(c[2])));
            }
            for (String[] c : leer("multas.csv")) {
                servicioMultas.cargarMulta(new Multa(c[0], c[1], Long.parseLong(c[2]),
                        Long.parseLong(c[3]), fecha(c[4]), Boolean.parseBoolean(c[5])));
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("ADVERTENCIA: algún archivo de datos está dañado y no se cargó completo ("
                    + e.getMessage() + ")");
        }
    }

    // =====================================================
    //  AUXILIARES
    // =====================================================

    /**
     * Escribe primero en un archivo temporal y luego lo reemplaza,
     * para no dejar un archivo a medias si el programa se interrumpe.
     */
    private void escribir(String nombre, List<String> lineas) throws IOException {
        Path destino = carpeta.resolve(nombre);
        Path temporal = carpeta.resolve(nombre + ".tmp");
        Files.write(temporal, lineas, StandardCharsets.UTF_8);
        Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Lee un archivo y devuelve sus filas (sin la línea de encabezado).
     */
    private List<String[]> leer(String nombre) throws IOException {
        List<String[]> filas = new ArrayList<>();
        Path archivo = carpeta.resolve(nombre);
        if (!Files.exists(archivo)) {
            return filas;
        }
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        for (int i = 1; i < lineas.size(); i++) {      // i = 1 salta el encabezado
            if (!lineas.get(i).isBlank()) {
                filas.add(lineas.get(i).split(SEP, -1)); // -1 conserva campos vacíos
            }
        }
        return filas;
    }

    private String unir(String... campos) {
        List<String> limpios = new ArrayList<>();
        for (String campo : campos) {
            // El ';' es el separador: si viniera dentro de un texto dañaría el archivo.
            limpios.add(campo == null ? "" : campo.replace(SEP, ",").replace("\n", " ").replace("\r", " "));
        }
        return String.join(SEP, limpios);
    }

    private String texto(LocalDate fecha) {
        return fecha == null ? "" : fecha.toString();
    }

    private LocalDate fecha(String texto) {
        return texto == null || texto.isBlank() ? null : LocalDate.parse(texto);
    }
}
