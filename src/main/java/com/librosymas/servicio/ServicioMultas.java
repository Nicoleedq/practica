package com.librosymas.servicio;

import com.librosymas.modelo.Cliente;
import com.librosymas.modelo.Multa;
import com.librosymas.modelo.Prestamo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Calcula, registra y permite consultar/pagar las multas por retraso.
 */
public class ServicioMultas {

    private final GestionClientes gestionClientes;
    private final List<Multa> multas = new ArrayList<>();

    public ServicioMultas(GestionClientes gestionClientes) {
        this.gestionClientes = gestionClientes;
    }

    /**
     * Revisa si la devolución fue tardía y, si es así, genera la multa
     * y la acumula en el perfil del cliente.
     *
     * @return la multa generada, o null si el libro se devolvió a tiempo.
     */
    public Multa generarMultaSiAplica(Prestamo prestamo, LocalDate fechaEntrega) {
        long diasRetraso = prestamo.calcularDiasRetraso(fechaEntrega);
        if (diasRetraso == 0) {
            return null;
        }
        Multa multa = new Multa(prestamo.getDocumentoCliente(), prestamo.getIdLibro(),
                diasRetraso, fechaEntrega);
        multas.add(multa);

        Cliente cliente = gestionClientes.buscarPorDocumento(prestamo.getDocumentoCliente());
        if (cliente != null) {
            cliente.agregarMulta(multa.getValor());
        }
        return multa;
    }

    /**
     * Todas las multas (pagadas y pendientes) de un cliente.
     */
    public List<Multa> listarPorCliente(String documento) {
        List<Multa> resultado = new ArrayList<>();
        for (Multa multa : multas) {
            if (multa.getDocumentoCliente().equalsIgnoreCase(documento)) {
                resultado.add(multa);
            }
        }
        return resultado;
    }

    /**
     * Marca como pagadas todas las multas pendientes del cliente.
     *
     * @return valor total pagado (0 si no debía nada).
     */
    public long pagarMultas(Cliente cliente) {
        long pagado = cliente.getMultaPendiente();
        for (Multa multa : listarPorCliente(cliente.getDocumento())) {
            multa.marcarPagada();
        }
        cliente.pagarMultas();
        return pagado;
    }

    public List<Multa> listarTodas() {
        return new ArrayList<>(multas);
    }

    /**
     * Usado por la persistencia al cargar los datos guardados.
     */
    void cargarMulta(Multa multa) {
        multas.add(multa);
        if (!multa.estaPagada()) {
            Cliente cliente = gestionClientes.buscarPorDocumento(multa.getDocumentoCliente());
            if (cliente != null) {
                cliente.agregarMulta(multa.getValor());
            }
        }
    }
}
