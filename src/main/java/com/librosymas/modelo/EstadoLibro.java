package com.librosymas.modelo;

/**
 * Estados posibles de un ejemplar dentro del inventario.
 *
 * DISPONIBLE -> puede prestarse
 * PRESTADO   -> lo tiene un cliente
 * RETIRADO   -> fuera del inventario por daño o decisión administrativa
 *
 * Responsable: Jerónimo  |  Rama: feature/modelo-libro
 */
public enum EstadoLibro {
    DISPONIBLE,
    PRESTADO,
    RETIRADO;

    public static EstadoLibro porOpcion(int opcion) {
        switch (opcion) {
            case 1: return DISPONIBLE;
            case 2: return PRESTADO;
            case 3: return RETIRADO;
            default: return null;
        }
    }
}
