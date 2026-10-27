package com.librosymas.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Registro de un préstamo: qué libro, a qué cliente, cuándo se prestó,
 * hasta cuándo debe devolverse y cuándo se devolvió realmente.
 *
 * Un préstamo con fechaDevolucion == null está ACTIVO.
 * Un préstamo devuelto se conserva como parte del HISTORIAL.
 *
 * Responsable: Nicole  |  Rama: feature/prestamos
 */
public class Prestamo {

    /** Días que el cliente tiene para devolver el libro. */
    public static final int DIAS_PRESTAMO = 15;

    private final String idLibro;
    private final String documentoCliente;
    private final LocalDate fechaPrestamo;
    private final LocalDate fechaLimite;
    private LocalDate fechaDevolucion; // null mientras el préstamo esté activo

    /**
     * Préstamo nuevo: se presta hoy y la fecha límite se calcula automáticamente.
     */
    public Prestamo(String idLibro, String documentoCliente) {
        this(idLibro, documentoCliente, LocalDate.now(),
                LocalDate.now().plusDays(DIAS_PRESTAMO), null);
    }

    /**
     * Constructor completo, usado al cargar los datos guardados en archivo.
     */
    public Prestamo(String idLibro, String documentoCliente, LocalDate fechaPrestamo,
                    LocalDate fechaLimite, LocalDate fechaDevolucion) {
        this.idLibro = idLibro;
        this.documentoCliente = documentoCliente;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getIdLibro() {
        return idLibro;
    }

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    // ---------- Métodos de comportamiento ----------

    public boolean estaActivo() {
        return fechaDevolucion == null;
    }

    /**
     * Cierra el préstamo con la fecha en que se devolvió el libro.
     */
    public void registrarDevolucion(LocalDate fecha) {
        this.fechaDevolucion = fecha;
    }

    /**
     * Días de retraso respecto a la fecha límite (0 si se devolvió a tiempo).
     */
    public long calcularDiasRetraso(LocalDate fechaEntrega) {
        long dias = ChronoUnit.DAYS.between(fechaLimite, fechaEntrega);
        return Math.max(0, dias);
    }

    public String mostrarInformacion() {
        String estado = estaActivo()
                ? "ACTIVO"
                : "Devuelto: " + fechaDevolucion;
        return "Libro " + idLibro + " -> Cliente " + documentoCliente
                + " | Prestado: " + fechaPrestamo
                + " | Límite: " + fechaLimite
                + " | " + estado;
    }

    @Override
    public String toString() {
        return mostrarInformacion();
    }
}
