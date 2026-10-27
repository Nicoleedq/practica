package com.librosymas.servicio;

import com.librosymas.modelo.Cliente;
import com.librosymas.modelo.EstadoLibro;
import com.librosymas.modelo.Libro;
import com.librosymas.modelo.Reserva;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Administra la lista de espera (cola FIFO) de cada libro.
 *
 * Reglas:
 *  - Solo se reserva un libro que NO está disponible porque está PRESTADO
 *    (o que ya tiene a otros clientes esperando).
 *  - Un cliente no puede reservar el libro que él mismo tiene prestado.
 *  - Un cliente no puede estar dos veces en la misma lista.
 *  - Cuando el libro se devuelve, el primero de la lista tiene prioridad para prestarlo.
 */
public class ServicioReservas {

    private final Inventario inventario;
    private final GestionClientes gestionClientes;

    // id del libro -> cola de reservas en orden de solicitud
    private final Map<String, LinkedList<Reserva>> reservasPorLibro = new LinkedHashMap<>();

    public ServicioReservas(Inventario inventario, GestionClientes gestionClientes) {
        this.inventario = inventario;
        this.gestionClientes = gestionClientes;
    }

    public String registrarReserva(String idLibro, String documentoCliente) {
        Libro libro = inventario.buscarPorId(idLibro);
        if (libro == null) {
            return "ERROR: no existe un libro con el id " + idLibro + ".";
        }

        Cliente cliente = gestionClientes.buscarPorDocumento(documentoCliente);
        if (cliente == null) {
            return "ERROR: no existe un cliente con el documento " + documentoCliente + ".";
        }

        if (libro.getEstado() == EstadoLibro.RETIRADO) {
            return "ERROR: el libro está RETIRADO y no se puede reservar.";
        }

        LinkedList<Reserva> cola = colaDe(libro.getId());
        if (libro.estaDisponible() && cola.isEmpty()) {
            return "ERROR: el libro está DISPONIBLE. No necesita reserva, puede prestarlo (opción 7).";
        }

        if (libro.getId().equalsIgnoreCase(cliente.getIdLibroPrestado())) {
            return "ERROR: el cliente ya tiene este libro en préstamo.";
        }

        if (posicionDe(libro.getId(), cliente.getDocumento()) > 0) {
            return "ERROR: el cliente ya tiene una reserva para este libro.";
        }

        cola.addLast(new Reserva(libro.getId(), cliente.getDocumento()));
        return "Reserva registrada. " + cliente.getNombreCompleto()
                + " quedó en la posición " + cola.size() + " de la lista de espera del libro " + libro.getId() + ".";
    }

    /**
     * Reservas de un libro en orden de solicitud.
     */
    public List<Reserva> listarReservas(String idLibro) {
        LinkedList<Reserva> cola = reservasPorLibro.get(normalizar(idLibro));
        return cola == null ? new ArrayList<>() : new ArrayList<>(cola);
    }

    /**
     * Primer cliente en la lista de espera, o null si no hay nadie.
     */
    public Reserva siguienteEnEspera(String idLibro) {
        LinkedList<Reserva> cola = reservasPorLibro.get(normalizar(idLibro));
        return (cola == null || cola.isEmpty()) ? null : cola.getFirst();
    }

    /**
     * Posición (1, 2, 3...) del cliente en la lista del libro; 0 si no está.
     */
    public int posicionDe(String idLibro, String documento) {
        List<Reserva> cola = listarReservas(idLibro);
        for (int i = 0; i < cola.size(); i++) {
            if (cola.get(i).getDocumentoCliente().equalsIgnoreCase(documento)) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Quita la reserva del cliente (se usa cuando por fin recibe el libro).
     */
    public void eliminarReserva(String idLibro, String documento) {
        LinkedList<Reserva> cola = reservasPorLibro.get(normalizar(idLibro));
        if (cola != null) {
            cola.removeIf(r -> r.getDocumentoCliente().equalsIgnoreCase(documento));
            if (cola.isEmpty()) {
                reservasPorLibro.remove(normalizar(idLibro));
            }
        }
    }

    /**
     * Todas las reservas de todos los libros (para guardarlas en archivo).
     */
    public List<Reserva> listarTodas() {
        List<Reserva> todas = new ArrayList<>();
        for (LinkedList<Reserva> cola : reservasPorLibro.values()) {
            todas.addAll(cola);
        }
        return todas;
    }

    /**
     * Usado por la persistencia: agrega al final respetando el orden del archivo.
     */
    void cargarReserva(Reserva reserva) {
        colaDe(reserva.getIdLibro()).addLast(reserva);
    }

    private LinkedList<Reserva> colaDe(String idLibro) {
        return reservasPorLibro.computeIfAbsent(normalizar(idLibro), k -> new LinkedList<>());
    }

    private String normalizar(String idLibro) {
        return idLibro == null ? "" : idLibro.toUpperCase();
    }
}
