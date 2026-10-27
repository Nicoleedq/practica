package com.librosymas.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Solicitud de un cliente para recibir un libro que hoy está prestado.
 * Las reservas de un mismo libro se atienden en orden de llegada (FIFO).
 */
public class Reserva {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String idLibro;
    private final String documentoCliente;
    private final LocalDateTime fechaSolicitud;

    public Reserva(String idLibro, String documentoCliente) {
        this(idLibro, documentoCliente, LocalDateTime.now());
    }

    /**
     * Constructor completo, usado al cargar los datos guardados en archivo.
     */
    public Reserva(String idLibro, String documentoCliente, LocalDateTime fechaSolicitud) {
        this.idLibro = idLibro;
        this.documentoCliente = documentoCliente;
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getIdLibro() {
        return idLibro;
    }

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public String mostrarInformacion() {
        return "Cliente " + documentoCliente + " | Solicitada: " + fechaSolicitud.format(FORMATO);
    }

    @Override
    public String toString() {
        return mostrarInformacion();
    }
}
