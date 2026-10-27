package com.librosymas.servicio;

import com.librosymas.modelo.Cliente;
import com.librosymas.modelo.EstadoLibro;
import com.librosymas.modelo.Libro;
import com.librosymas.modelo.Multa;
import com.librosymas.modelo.Prestamo;
import com.librosymas.modelo.Reserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza las operaciones de préstamo y devolución.
 * Conecta el inventario (libros) con el registro de clientes,
 * e impide situaciones incoherentes.
 *
 * Ahora guarda TODOS los préstamos (historial): los activos y los ya devueltos.
 *
 * Responsable: Nicole  |  Rama: feature/prestamos
 */
public class ServicioPrestamos {

    private final Inventario inventario;
    private final GestionClientes gestionClientes;
    private final ServicioMultas servicioMultas;
    private final ServicioReservas servicioReservas;

    // Historial completo. Los activos son los que no tienen fecha de devolución.
    private final List<Prestamo> prestamos = new ArrayList<>();

    public ServicioPrestamos(Inventario inventario, GestionClientes gestionClientes,
                             ServicioMultas servicioMultas, ServicioReservas servicioReservas) {
        this.inventario = inventario;
        this.gestionClientes = gestionClientes;
        this.servicioMultas = servicioMultas;
        this.servicioReservas = servicioReservas;
    }

    /**
     * Registra un préstamo si se cumplen TODAS las condiciones:
     *  - el libro existe
     *  - el libro está disponible
     *  - el cliente existe
     *  - el cliente no tiene ya un libro prestado
     *  - si el libro tiene reservas, solo lo puede llevar el primero de la lista
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
            return "ERROR: el libro no está disponible (estado actual: " + libro.getEstado() + ")."
                    + (libro.getEstado() == EstadoLibro.PRESTADO ? " Puede reservarlo (opción 13)." : "");
        }

        if (cliente.tieneLibroPrestado()) {
            return "ERROR: el cliente ya tiene el libro " + cliente.getIdLibroPrestado()
                    + " en préstamo. Solo se permite uno a la vez.";
        }

        Reserva siguiente = servicioReservas.siguienteEnEspera(libro.getId());
        if (siguiente != null && !siguiente.getDocumentoCliente().equalsIgnoreCase(cliente.getDocumento())) {
            return "ERROR: el libro está reservado. El turno es del cliente "
                    + siguiente.getDocumentoCliente() + ".";
        }

        // Todo correcto: se actualiza el libro, el cliente y se guarda el registro con las fechas.
        libro.cambiarEstado(EstadoLibro.PRESTADO);
        cliente.recibirLibro(libro.getId());
        Prestamo prestamo = new Prestamo(libro.getId(), cliente.getDocumento());
        prestamos.add(prestamo);

        if (siguiente != null) {
            servicioReservas.eliminarReserva(libro.getId(), cliente.getDocumento());
        }

        return "Préstamo registrado -> " + prestamo.mostrarInformacion();
    }

    /**
     * Devolución con la fecha de hoy.
     */
    public String registrarDevolucion(String idLibro) {
        return registrarDevolucion(idLibro, LocalDate.now());
    }

    /**
     * Registra la devolución de un libro que figure como prestado.
     * Si la entrega es tardía, genera la multa. Si hay reservas, avisa quién sigue.
     *
     * @param fechaEntrega fecha real de la devolución.
     * @return mensaje con el resultado de la operación.
     */
    public String registrarDevolucion(String idLibro, LocalDate fechaEntrega) {
        Libro libro = inventario.buscarPorId(idLibro);
        if (libro == null) {
            return "ERROR: no existe un libro con el id " + idLibro + ".";
        }

        Prestamo prestamo = buscarPrestamoActivo(idLibro);
        if (prestamo == null) {
            return "ERROR: el libro " + idLibro + " no figura como prestado.";
        }

        if (fechaEntrega.isBefore(prestamo.getFechaPrestamo())) {
            return "ERROR: la fecha de devolución no puede ser anterior a la del préstamo ("
                    + prestamo.getFechaPrestamo() + ").";
        }

        Cliente cliente = gestionClientes.buscarPorDocumento(prestamo.getDocumentoCliente());
        if (cliente != null) {
            cliente.devolverLibro(); // el cliente queda sin libro pendiente
        }

        libro.cambiarEstado(EstadoLibro.DISPONIBLE); // vuelve a estar disponible
        prestamo.registrarDevolucion(fechaEntrega);    // queda en el historial

        StringBuilder mensaje = new StringBuilder(
                "Devolución registrada: el libro " + libro.getId() + " vuelve a estar DISPONIBLE.");

        Multa multa = servicioMultas.generarMultaSiAplica(prestamo, fechaEntrega);
        if (multa != null) {
            mensaje.append("\nMULTA: devolución con ").append(multa.getDiasRetraso())
                    .append(" día(s) de retraso. Se cargaron $").append(multa.getValor())
                    .append(" al cliente ").append(prestamo.getDocumentoCliente()).append(".");
            if (cliente != null) {
                mensaje.append(" Total pendiente: $").append(cliente.getMultaPendiente()).append(".");
            }
        }

        Reserva siguiente = servicioReservas.siguienteEnEspera(libro.getId());
        if (siguiente != null) {
            Cliente clienteSiguiente = gestionClientes.buscarPorDocumento(siguiente.getDocumentoCliente());
            String nombre = clienteSiguiente != null ? clienteSiguiente.getNombreCompleto() : "";
            mensaje.append("\nAVISO DE RESERVA: el siguiente en la lista de espera es ")
                    .append(nombre).append(" (").append(siguiente.getDocumentoCliente())
                    .append("). El libro queda apartado para este cliente.");
        }

        return mensaje.toString();
    }

    /**
     * Busca el préstamo activo asociado a un libro.
     */
    private Prestamo buscarPrestamoActivo(String idLibro) {
        for (Prestamo prestamo : prestamos) {
            if (prestamo.estaActivo() && prestamo.getIdLibro().equalsIgnoreCase(idLibro)) {
                return prestamo;
            }
        }
        return null;
    }

    public List<Prestamo> listarPrestamosActivos() {
        List<Prestamo> activos = new ArrayList<>();
        for (Prestamo prestamo : prestamos) {
            if (prestamo.estaActivo()) {
                activos.add(prestamo);
            }
        }
        return activos;
    }

    /**
     * Historial completo (activos y devueltos) en orden cronológico.
     */
    public List<Prestamo> listarHistorial() {
        return new ArrayList<>(prestamos);
    }

    /**
     * Usado por la persistencia al cargar los datos guardados.
     * Si el préstamo está activo, vuelve a enlazar el libro con el cliente.
     */
    void cargarPrestamo(Prestamo prestamo) {
        prestamos.add(prestamo);
        if (prestamo.estaActivo()) {
            Cliente cliente = gestionClientes.buscarPorDocumento(prestamo.getDocumentoCliente());
            if (cliente != null) {
                cliente.recibirLibro(prestamo.getIdLibro());
            }
        }
    }
}
