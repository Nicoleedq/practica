package com.librosymas.ui;

import com.librosymas.modelo.Categoria;
import com.librosymas.modelo.Cliente;
import com.librosymas.modelo.EstadoLibro;
import com.librosymas.modelo.Libro;
import com.librosymas.modelo.Prestamo;
import com.librosymas.servicio.GestionClientes;
import com.librosymas.servicio.Inventario;
import com.librosymas.servicio.ServicioPrestamos;

import java.util.List;
import java.util.Scanner;

/**
 * Menú principal del sistema por consola.
 *
 * Archivo compartido: las opciones de LIBROS las agrega Jerónimo y las de
 * CLIENTES/PRÉSTAMOS las agrega Nicole. Es el archivo donde puede aparecer
 * un conflicto al hacer merge (ver README).
 */
public class Menu {

    private final Inventario inventario;
    private final GestionClientes gestionClientes;
    private final ServicioPrestamos servicioPrestamos;
    private final Scanner sc;

    public Menu(Inventario inventario, GestionClientes gestionClientes,
                ServicioPrestamos servicioPrestamos, Scanner sc) {
        this.inventario = inventario;
        this.gestionClientes = gestionClientes;
        this.servicioPrestamos = servicioPrestamos;
        this.sc = sc;
    }

    /**
     * Bucle principal: muestra el menú hasta que el usuario elija salir.
     */
    public void iniciar() {
        int opcion = -1;
        while (opcion != 0) {
            mostrarOpciones();
            opcion = leerEntero("Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                // ----- Libros e inventario (Jerónimo) -----
                case 1: registrarLibro(); break;
                case 2: listarLibrosDisponibles(); break;
                case 3: listarTodosLosLibros(); break;
                case 4: cambiarEstadoLibro(); break;

                // ----- Clientes y préstamos (Nicole) -----
                case 5: registrarCliente(); break;
                case 6: listarClientes(); break;
                case 7: registrarPrestamo(); break;
                case 8: registrarDevolucion(); break;
                case 9: listarPrestamosActivos(); break;

                case 0: System.out.println("Saliendo del sistema. ¡Hasta pronto!"); break;
                default: System.out.println("Opción no válida. Intente de nuevo.");
            }
            System.out.println();
        }
    }

    private void mostrarOpciones() {
        System.out.println("==============================================");
        System.out.println("     BIBLIOTECA LibrosYMas - MENÚ PRINCIPAL");
        System.out.println("==============================================");
        System.out.println("--- Libros / Inventario ---");
        System.out.println("1. Registrar libro");
        System.out.println("2. Listar libros disponibles");
        System.out.println("3. Listar todos los libros");
        System.out.println("4. Cambiar estado de un libro");
        System.out.println("--- Clientes / Préstamos ---");
        System.out.println("5. Registrar cliente");
        System.out.println("6. Listar clientes");
        System.out.println("7. Registrar préstamo");
        System.out.println("8. Registrar devolución");
        System.out.println("9. Listar préstamos activos");
        System.out.println("----------------------------------------------");
        System.out.println("0. Salir");
    }

    // =====================================================
    //  OPCIONES DE LIBROS / INVENTARIO   (Jerónimo)
    // =====================================================

    private void registrarLibro() {
        System.out.println(">> REGISTRAR LIBRO");
        String id = leerTexto("Identificador único: ");

        if (inventario.buscarPorId(id) != null) {
            System.out.println("ERROR: ya existe un libro con ese identificador.");
            return;
        }

        String titulo = leerTexto("Título: ");
        String autor = leerTexto("Autor: ");
        String editorial = leerTexto("Editorial: ");
        int anio = leerEntero("Año de publicación: ");

        System.out.println("Categorías: 1) LITERATURA  2) CIENCIA  3) HISTORIA  4) TECNOLOGIA");
        Categoria categoria = Categoria.porOpcion(leerEntero("Seleccione categoría: "));
        if (categoria == null) {
            System.out.println("ERROR: categoría no válida. No se registró el libro.");
            return;
        }

        Libro libro = new Libro(id, titulo, autor, editorial, anio, categoria);
        if (inventario.registrarLibro(libro)) {
            System.out.println("Libro registrado correctamente:");
            System.out.println(libro.mostrarInformacion());
        } else {
            System.out.println("ERROR: no fue posible registrar el libro.");
        }
    }

    private void listarLibrosDisponibles() {
        System.out.println(">> LIBROS DISPONIBLES");
        imprimirLibros(inventario.listarDisponibles());
    }

    private void listarTodosLosLibros() {
        System.out.println(">> INVENTARIO COMPLETO");
        imprimirLibros(inventario.listarTodos());
    }

    private void cambiarEstadoLibro() {
        System.out.println(">> CAMBIAR ESTADO DE UN LIBRO");
        String id = leerTexto("Id del libro: ");
        Libro libro = inventario.buscarPorId(id);
        if (libro == null) {
            System.out.println("ERROR: no existe un libro con ese id.");
            return;
        }

        System.out.println("Estados: 1) DISPONIBLE  2) PRESTADO  3) RETIRADO");
        EstadoLibro estado = EstadoLibro.porOpcion(leerEntero("Nuevo estado: "));
        if (estado == null) {
            System.out.println("ERROR: estado no válido.");
            return;
        }

        libro.cambiarEstado(estado);
        System.out.println("Estado actualizado: " + libro.mostrarInformacion());
    }

    private void imprimirLibros(List<Libro> libros) {
        if (libros.isEmpty()) {
            System.out.println("No hay libros para mostrar.");
            return;
        }
        for (Libro libro : libros) {
            System.out.println(libro.mostrarInformacion());
        }
    }

    // =====================================================
    //  OPCIONES DE CLIENTES / PRÉSTAMOS   (Nicole)
    // =====================================================

    private void registrarCliente() {
        System.out.println(">> REGISTRAR CLIENTE");
        String documento = leerTexto("Documento de identidad: ");

        if (gestionClientes.buscarPorDocumento(documento) != null) {
            System.out.println("ERROR: ya existe un cliente con ese documento.");
            return;
        }

        String nombre = leerTexto("Nombre completo: ");
        String telefono = leerTexto("Teléfono: ");
        String direccion = leerTexto("Dirección: ");

        Cliente cliente = new Cliente(documento, nombre, telefono, direccion);
        if (gestionClientes.registrarCliente(cliente)) {
            System.out.println("Cliente registrado correctamente:");
            System.out.println(cliente.mostrarInformacion());
        } else {
            System.out.println("ERROR: no fue posible registrar el cliente.");
        }
    }

    private void listarClientes() {
        System.out.println(">> CLIENTES REGISTRADOS");
        List<Cliente> clientes = gestionClientes.listarTodos();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        for (Cliente cliente : clientes) {
            System.out.println(cliente.mostrarInformacion());
        }
    }

    private void registrarPrestamo() {
        System.out.println(">> REGISTRAR PRÉSTAMO");
        String idLibro = leerTexto("Id del libro: ");
        String documento = leerTexto("Documento del cliente: ");
        System.out.println(servicioPrestamos.registrarPrestamo(idLibro, documento));
    }

    private void registrarDevolucion() {
        System.out.println(">> REGISTRAR DEVOLUCIÓN");
        String idLibro = leerTexto("Id del libro a devolver: ");
        System.out.println(servicioPrestamos.registrarDevolucion(idLibro));
    }

    private void listarPrestamosActivos() {
        System.out.println(">> PRÉSTAMOS ACTIVOS");
        List<Prestamo> prestamos = servicioPrestamos.listarPrestamosActivos();
        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos activos.");
            return;
        }
        for (Prestamo prestamo : prestamos) {
            System.out.println(prestamo.mostrarInformacion());
        }
    }

    // =====================================================
    //  MÉTODOS AUXILIARES DE LECTURA POR CONSOLA
    // =====================================================

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    /**
     * Lee un número entero y evita que el programa se caiga
     * si el usuario escribe letras.
     */
    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = sc.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }
    }
}
