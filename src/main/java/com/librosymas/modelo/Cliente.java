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

    // Total de multas que el cliente aún no ha pagado (pesos).
    private long multaPendiente;

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

    // ---------- Multas ----------

    public long getMultaPendiente() {
        return multaPendiente;
    }

    public boolean tieneMultasPendientes() {
        return multaPendiente > 0;
    }

    /**
     * Acumula una multa en el perfil del cliente.
     */
    public void agregarMulta(long valor) {
        if (valor > 0) {
            this.multaPendiente += valor;
        }
    }

    /**
     * Deja el saldo de multas en cero (el cliente pagó todo).
     */
    public void pagarMultas() {
        this.multaPendiente = 0;
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
                + " | " + pendiente
                + " | Multas pendientes: $" + multaPendiente;
    }

    @Override
    public String toString() {
        return mostrarInformacion();
    }
}
