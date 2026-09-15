package com.librosymas.modelo;

/**
 * Representa un ejemplar físico de la biblioteca.
 * Cada ejemplar tiene un identificador único, aunque el título se repita.
 *
 * Responsable: Jerónimo  |  Rama: feature/modelo-libro
 */
public class Libro {

    // El id no cambia nunca, por eso es final.
    private final String id;
    private String titulo;
    private String autor;
    private String editorial;
    private int anioPublicacion;
    private Categoria categoria;
    private EstadoLibro estado;

    /**
     * Al crear un libro siempre entra al inventario como DISPONIBLE.
     */
    public Libro(String id, String titulo, String autor, String editorial,
                 int anioPublicacion, Categoria categoria) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.categoria = categoria;
        this.estado = EstadoLibro.DISPONIBLE;
    }

    // ---------- Getters y setters (encapsulamiento) ----------

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public EstadoLibro getEstado() {
        return estado;
    }

    // ---------- Métodos de comportamiento ----------

    /**
     * Cambia el estado del ejemplar (disponible / prestado / retirado).
     */
    public void cambiarEstado(EstadoLibro nuevoEstado) {
        this.estado = nuevoEstado;
    }

    /**
     * Indica si el ejemplar se puede prestar en este momento.
     */
    public boolean estaDisponible() {
        return this.estado == EstadoLibro.DISPONIBLE;
    }

    /**
     * Información completa del libro en formato de texto.
     */
    public String mostrarInformacion() {
        return "[" + id + "] " + titulo
                + " | Autor: " + autor
                + " | Editorial: " + editorial
                + " | Año: " + anioPublicacion
                + " | Categoría: " + categoria
                + " | Estado: " + estado;
    }

    @Override
    public String toString() {
        return mostrarInformacion();
    }
}
