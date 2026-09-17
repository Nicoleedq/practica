package com.librosymas.modelo;

/**
 * Representa a un cliente registrado de la biblioteca.
 * Política interna: cada cliente puede tener máximo UN libro en préstamo.
 *
 * Responsable: Nicole  |  Rama: featureCliente
 */
public class Cliente {

    private final String documento;   // identificador único del cliente
    private String nombreCompleto;
    private String telefono;
    private String direccion;

    // Guarda el id del libro prestado. Si es null, el cliente no tiene ninguno.
    private String idLibroPrestado;

    public Cliente(String documento, String nombreCompleto, String telefono, String direccion) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.direccion = direccion;
        this.idLibroPrestado = null;
    }

    // ---------- Getters y setters ----------

    public String getDocumento() {
        return documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getIdLibroPrestado() {
        return idLibroPrestado;
    }

    // ---------- Métodos de comportamiento ----------

    /**
     * Indica si el cliente tiene actualmente un libro en préstamo.
     */
    public boolean tieneLibroPrestado() {
        return this.idLibroPrestado != null;
    }

    /**
     * Marca que el cliente recibió un libro.
     */
    public void recibirLibro(String idLibro) {
        this.idLibroPrestado = idLibro;
    }

    /**
     * Marca que el cliente devolvió el libro y queda sin pendientes.
     */
    public void devolverLibro() {
        this.idLibroPrestado = null;
    }

    /**
     * Información completa del cliente en formato de texto.
     */
    public String mostrarInformacion() {
        String pendiente = tieneLibroPrestado()
                ? "Libro en préstamo: " + idLibroPrestado
                : "Sin libros pendientes";
        return "[" + documento + "] " + nombreCompleto
                + " | Tel: " + telefono
                + " | Dir: " + direccion
                + " | " + pendiente;
    }

    @Override
    public String toString() {
        return mostrarInformacion();
    }
}
