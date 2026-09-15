package com.librosymas.modelo;

/**
 * Categorías fijas definidas por la biblioteca.
 * Todo libro debe pertenecer obligatoriamente a una de ellas.
 *
 * Responsable: Jerónimo  |  Rama: feature/modelo-libro
 */
public enum Categoria {
    LITERATURA,
    CIENCIA,
    HISTORIA,
    TECNOLOGIA;

    /**
     * Convierte un número del menú (1-4) en una categoría.
     * Devuelve null si el número no es válido.
     */
    public static Categoria porOpcion(int opcion) {
        switch (opcion) {
            case 1: return LITERATURA;
            case 2: return CIENCIA;
            case 3: return HISTORIA;
            case 4: return TECNOLOGIA;
            default: return null;
        }
    }
}
