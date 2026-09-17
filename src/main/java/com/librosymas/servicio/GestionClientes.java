package com.librosymas.servicio;

import com.librosymas.modelo.Cliente;

import java.util.ArrayList;
import java.util.List;

/**
 * Almacena y administra el registro de clientes de la biblioteca.
 *
 * Responsable: Nicole  |  Rama: featureCliente
 */
public class GestionClientes {

    private final List<Cliente> clientes = new ArrayList<>();

    /**
     * Registra un cliente nuevo. El documento no se puede repetir.
     *
     * @return true si se registró, false si el documento ya existía.
     */
    public boolean registrarCliente(Cliente cliente) {
        if (cliente == null) {
            return false;
        }
        if (buscarPorDocumento(cliente.getDocumento()) != null) {
            return false; // documento duplicado
        }
        clientes.add(cliente);
        return true;
    }

    /**
     * Busca un cliente por su documento de identidad.
     *
     * @return el cliente o null si no está registrado.
     */
    public Cliente buscarPorDocumento(String documento) {
        for (Cliente cliente : clientes) {
            if (cliente.getDocumento().equalsIgnoreCase(documento)) {
                return cliente;
            }
        }
        return null;
    }

    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes);
    }

    public boolean estaVacio() {
        return clientes.isEmpty();
    }

    public int cantidad() {
        return clientes.size();
    }
}
