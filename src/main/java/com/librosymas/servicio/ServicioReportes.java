package com.librosymas.servicio;

import com.librosymas.modelo.Cliente;
import com.librosymas.modelo.EstadoLibro;
import com.librosymas.modelo.Libro;
import com.librosymas.modelo.Prestamo;

import java.util.ArrayList;
import java.util.List;

/**
 * Genera el reporte general con información consolidada de la biblioteca.
 * Solo LEE información de los demás servicios; no modifica nada.
 */
public class ServicioReportes {

    private final Inventario inventario;
    private final GestionClientes gestionClientes;
    private final ServicioPrestamos servicioPrestamos;

    public ServicioReportes(Inventario inventario, GestionClientes gestionClientes,
                            ServicioPrestamos servicioPrestamos) {
        this.inventario = inventario;
        this.gestionClientes = gestionClientes;
        this.servicioPrestamos = servicioPrestamos;
    }

    public String generarReporteGeneral() {
        List<Prestamo> historial = servicioPrestamos.listarHistorial();

        StringBuilder sb = new StringBuilder();
        sb.append("Total de libros registrados ........ ").append(inventario.cantidad()).append('\n');
        sb.append("Total de clientes registrados ...... ").append(gestionClientes.cantidad()).append('\n');
        sb.append("Préstamos activos .................. ").append(servicioPrestamos.listarPrestamosActivos().size()).append('\n');
        sb.append("Préstamos realizados (histórico) ... ").append(historial.size()).append('\n');
        sb.append("Cliente con más préstamos .......... ").append(clienteConMasPrestamos(historial)).append('\n');
        sb.append("Libro menos solicitado ............. ").append(libroMenosSolicitado(historial));
        return sb.toString();
    }

    /**
     * Cliente(s) con mayor número de préstamos en el historial.
     * Si hay empate se muestran todos los empatados.
     */
    private String clienteConMasPrestamos(List<Prestamo> historial) {
        if (historial.isEmpty()) {
            return "Sin préstamos registrados";
        }
        int maximo = 0;
        List<Cliente> ganadores = new ArrayList<>();
        for (Cliente cliente : gestionClientes.listarTodos()) {
            int cantidad = contarPrestamosCliente(historial, cliente.getDocumento());
            if (cantidad > maximo) {
                maximo = cantidad;
                ganadores.clear();
                ganadores.add(cliente);
            } else if (cantidad == maximo && cantidad > 0) {
                ganadores.add(cliente);
            }
        }
        List<String> nombres = new ArrayList<>();
        for (Cliente c : ganadores) {
            nombres.add(c.getNombreCompleto() + " [" + c.getDocumento() + "]");
        }
        return String.join(", ", nombres) + " (" + maximo + " préstamo(s))";
    }

    /**
     * Libro(s) con menos préstamos en el historial (incluye los que nunca se prestaron).
     * No se tienen en cuenta los libros RETIRADOS.
     */
    private String libroMenosSolicitado(List<Prestamo> historial) {
        List<Libro> candidatos = new ArrayList<>();
        for (Libro libro : inventario.listarTodos()) {
            if (libro.getEstado() != EstadoLibro.RETIRADO) {
                candidatos.add(libro);
            }
        }
        if (candidatos.isEmpty()) {
            return "No hay libros en circulación";
        }
        int minimo = Integer.MAX_VALUE;
        List<Libro> menos = new ArrayList<>();
        for (Libro libro : candidatos) {
            int cantidad = contarPrestamosLibro(historial, libro.getId());
            if (cantidad < minimo) {
                minimo = cantidad;
                menos.clear();
                menos.add(libro);
            } else if (cantidad == minimo) {
                menos.add(libro);
            }
        }
        List<String> titulos = new ArrayList<>();
        for (Libro l : menos) {
            titulos.add(l.getTitulo() + " [" + l.getId() + "]");
        }
        return String.join(", ", titulos) + " (" + minimo + " préstamo(s))";
    }

    private int contarPrestamosCliente(List<Prestamo> historial, String documento) {
        int total = 0;
        for (Prestamo p : historial) {
            if (p.getDocumentoCliente().equalsIgnoreCase(documento)) {
                total++;
            }
        }
        return total;
    }

    private int contarPrestamosLibro(List<Prestamo> historial, String idLibro) {
        int total = 0;
        for (Prestamo p : historial) {
            if (p.getIdLibro().equalsIgnoreCase(idLibro)) {
                total++;
            }
        }
        return total;
    }
}
