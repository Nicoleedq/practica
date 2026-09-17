package com.librosymas.modelo;

import java.time.LocalDate;

/**
 * Registro de un préstamo activo: qué libro, a qué cliente y en qué fecha.
 *
 * Responsable: Nicole  |  Rama: feature/prestamos
 */
public class Prestamo {

    private final String idLibro;
    private final String documentoCliente;
    private final LocalDate fechaPrestamo;

    public Prestamo(String idLibro, String documentoCliente) {
        this.idLibro = idLibro;
        this.documentoCliente = documentoCliente;
        this.fechaPrestamo = LocalDate.now(); // fecha en la que se realizó la operación
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

    public String mostrarInformacion() {
        return "Libro " + idLibro + " -> Cliente " + documentoCliente
                + " | Fecha: " + fechaPrestamo;
    }

    @Override
    public String toString() {
        return mostrarInformacion();
    }
}
