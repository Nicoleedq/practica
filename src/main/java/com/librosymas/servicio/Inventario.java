package com.librosymas.servicio;

import com.librosymas.modelo.EstadoLibro;
import com.librosymas.modelo.Libro;

import java.util.ArrayList;
import java.util.List;

/**
 * Estructura que almacena los libros y centraliza la gestión del inventario.
 * Toda la información se maneja en memoria (una lista).
 *
 * Responsable: Jerónimo  |  Rama: feature/inventario
 */
public class Inventario {

    private final List<Libro> libros = new ArrayList<>();

    /**
     * Registra un libro nuevo. Rechaza el registro si el id ya existe,
     * porque el identificador debe ser único en el sistema.
     *
     * @return true si se registró, false si el id estaba repetido.
     */
    public boolean registrarLibro(Libro libro) {
        if (libro == null) {
            return false;
        }
        if (buscarPorId(libro.getId()) != null) {
            return false; // id duplicado
        }
        libros.add(libro);
        return true;
    }

    /**
     * Busca un ejemplar por su identificador único.
     *
     * @return el libro encontrado o null si no existe.
     */
    public Libro buscarPorId(String id) {
        for (Libro libro : libros) {
            if (libro.getId().equalsIgnoreCase(id)) {
                return libro;
            }
        }
        return null;
    }

    /**
     * Lista completa del inventario.
     */
    public List<Libro> listarTodos() {
        return new ArrayList<>(libros);
    }

    /**
     * Libros filtrados por un estado específico.
     */
    public List<Libro> listarPorEstado(EstadoLibro estado) {
        List<Libro> resultado = new ArrayList<>();
        for (Libro libro : libros) {
            if (libro.getEstado() == estado) {
                resultado.add(libro);
            }
        }
        return resultado;
    }

    /**
     * Libros que se pueden prestar en este momento.
     */
    public List<Libro> listarDisponibles() {
        return listarPorEstado(EstadoLibro.DISPONIBLE);
    }

    public boolean estaVacio() {
        return libros.isEmpty();
    }

    public int cantidad() {
        return libros.size();
    }
}
