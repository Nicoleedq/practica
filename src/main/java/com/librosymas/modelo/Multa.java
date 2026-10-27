package com.librosymas.modelo;

import java.time.LocalDate;

/**
 * Multa generada cuando un cliente devuelve un libro después de la fecha límite.
 * El valor se calcula como: días de retraso x VALOR_POR_DIA.
 */
public class Multa {

    /** Valor de la multa por cada día de retraso (pesos colombianos). */
    public static final long VALOR_POR_DIA = 1000;

    private final String documentoCliente;
    private final String idLibro;
    private final long diasRetraso;
    private final long valor;
    private final LocalDate fechaGeneracion;
    private boolean pagada;

    /**
     * Multa nueva: el valor se calcula automáticamente a partir de los días de retraso.
     */
    public Multa(String documentoCliente, String idLibro, long diasRetraso, LocalDate fechaGeneracion) {
        this(documentoCliente, idLibro, diasRetraso, diasRetraso * VALOR_POR_DIA, fechaGeneracion, false);
    }

    /**
     * Constructor completo, usado al cargar los datos guardados en archivo.
     */
    public Multa(String documentoCliente, String idLibro, long diasRetraso, long valor,
                 LocalDate fechaGeneracion, boolean pagada) {
        this.documentoCliente = documentoCliente;
        this.idLibro = idLibro;
        this.diasRetraso = diasRetraso;
        this.valor = valor;
        this.fechaGeneracion = fechaGeneracion;
        this.pagada = pagada;
    }

    public String getDocumentoCliente() {
        return documentoCliente;
    }

    public String getIdLibro() {
        return idLibro;
    }

    public long getDiasRetraso() {
        return diasRetraso;
    }

    public long getValor() {
        return valor;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public boolean estaPagada() {
        return pagada;
    }

    public void marcarPagada() {
        this.pagada = true;
    }

    public String mostrarInformacion() {
        return "Libro " + idLibro
                + " | " + diasRetraso + " día(s) de retraso"
                + " | Valor: $" + valor
                + " | Fecha: " + fechaGeneracion
                + " | " + (pagada ? "PAGADA" : "PENDIENTE");
    }

    @Override
    public String toString() {
        return mostrarInformacion();
    }
}
