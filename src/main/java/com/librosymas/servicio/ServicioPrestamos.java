package com.librosymas.servicio;

import com.librosymas.modelo.Cliente;
import com.librosymas.modelo.EstadoLibro;
import com.librosymas.modelo.Libro;
import com.librosymas.modelo.Prestamo;

import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza las operaciones de préstamo y devolución.
 * Conecta el inventario (libros) con el registro de clientes,
 * e impide situaciones incoherentes.
 *
 * Responsable: Nicole  |  Rama: feature/prestamos
 */
public class ServicioPrestamos {

    private final Inventario inventario;
    private final GestionClientes gestionClientes;
    private final List<Prestamo> prestamosActivos = new ArrayList<>();

    public ServicioPrestamos(Inventario inventario, GestionClientes gestionClientes) {
        this.inventario = inventario;
        this.gestionClientes = gestionClientes;
    }

    /**
     * Registra un préstamo si se cumplen TODAS las condiciones:
     *  - el libro existe
     *  - el libro está disponible
     *  - el cliente existe
     *  - el cliente no tiene ya un libro prestado
     *
     * @return mensaje con el resultado de la operación.
     */
    public String registrarPrestamo(String idLibro, String documentoCliente) {
        Libro libro = inventario.buscarPorId(idLibro);
        if (libro == null) {
            return "ERROR: no existe un libro con el id " + idLibro + ".";
        }

        Cliente cliente = gestionClientes.buscarPorDocumento(documentoCliente);
        if (cliente == null) {
            return "ERROR: no existe un cliente con el documento " + documentoCliente + ".";
        }

        if (!libro.estaDisponible()) {
            return "ERROR: el libro no está disponible (estado actual: " + libro.getEstado() + ").";
        }

        if (cliente.tieneLibroPrestado()) {
            return "ERROR: el cliente ya tiene el libro " + cliente.getIdLibroPrestado()
                    + " en préstamo. Solo se permite uno a la vez.";
        }

        // Todo correcto: se actualiza el libro, el cliente y se guarda el registro con la fecha.
        libro.cambiarEstado(EstadoLibro.PRESTADO);
        cliente.recibirLibro(libro.getId());
        Prestamo prestamo = new Prestamo(libro.getId(), cliente.getDocumento());
        prestamosActivos.add(prestamo);

        return "Préstamo registrado -> " + prestamo.mostrarInformacion();
    }

    /**
     * Registra la devolución de un libro que figure como prestado.
     *
     * @return mensaje con el resultado de la operación.
     */
    public String registrarDevolucion(String idLibro) {
        Libro libro = inventario.buscarPorId(idLibro);
        if (libro == null) {
            return "ERROR: no existe un libro con el id " + idLibro + ".";
        }

        Prestamo prestamo = buscarPrestamoActivo(idLibro);
        if (prestamo == null) {
            return "ERROR: el libro " + idLibro + " no figura como prestado.";
        }

        Cliente cliente = gestionClientes.buscarPorDocumento(prestamo.getDocumentoCliente());
        if (cliente != null) {
            cliente.devolverLibro(); // el cliente queda sin pendientes
        }

        libro.cambiarEstado(EstadoLibro.DISPONIBLE); // vuelve a estar disponible
        prestamosActivos.remove(prestamo);

        return "Devolución registrada: el libro " + idLibro + " vuelve a estar DISPONIBLE.";
    }

    /**
     * Busca el préstamo activo asociado a un libro.
     */
    private Prestamo buscarPrestamoActivo(String idLibro) {
        for (Prestamo prestamo : prestamosActivos) {
            if (prestamo.getIdLibro().equalsIgnoreCase(idLibro)) {
                return prestamo;
            }
        }
        return null;
    }

    public List<Prestamo> listarPrestamosActivos() {
        return new ArrayList<>(prestamosActivos);
    }
}
