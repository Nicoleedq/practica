package com.librosymas.ui;

import com.librosymas.modelo.Categoria;
import com.librosymas.modelo.Cliente;
import com.librosymas.modelo.EstadoLibro;
import com.librosymas.modelo.Libro;
import com.librosymas.modelo.Multa;
import com.librosymas.modelo.Prestamo;
import com.librosymas.modelo.Reserva;
import com.librosymas.servicio.GestionClientes;
import com.librosymas.servicio.Inventario;
import com.librosymas.servicio.Persistencia;
import com.librosymas.servicio.ServicioMultas;
import com.librosymas.servicio.ServicioPrestamos;
import com.librosymas.servicio.ServicioReportes;
import com.librosymas.servicio.ServicioReservas;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final ServicioReservas servicioReservas;
    private final ServicioMultas servicioMultas;
    private final ServicioReportes servicioReportes;
    private final Persistencia persistencia;
    private final Scanner sc;

    public Menu(Inventario inventario, GestionClientes gestionClientes,
                ServicioPrestamos servicioPrestamos, ServicioReservas servicioReservas,
                ServicioMultas servicioMultas, ServicioReportes servicioReportes,
                Persistencia persistencia, Scanner sc) {
        this.inventario = inventario;
        this.gestionClientes = gestionClientes;
        this.servicioPrestamos = servicioPrestamos;
        this.servicioReservas = servicioReservas;
        this.servicioMultas = servicioMultas;
        this.servicioReportes = servicioReportes;
        this.persistencia = persistencia;
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

                // ----- Historial y multas -----
                case 10: verHistorialPrestamos(); break;
                case 11: consultarMultasCliente(); break;
                case 12: pagarMultasCliente(); break;

                // ----- Reservas -----
                case 13: registrarReserva(); break;
                case 14: consultarReservasLibro(); break;

                // ----- Reportes -----
                case 15: mostrarReporteGeneral(); break;

                case 0: System.out.println("Saliendo del sistema. ¡Hasta pronto!"); break;
                default: System.out.println("Opción no válida. Intente de nuevo.");
            }

            // Después de cada opción que modifica datos se guarda todo en disco.
            if (modificaDatos(opcion)) {
                persistencia.guardar();
            }
            System.out.println();
        }
    }

    private boolean modificaDatos(int opcion) {
        switch (opcion) {
            case 1: case 4: case 5: case 7: case 8: case 12: case 13:
                return true;
            default:
                return false;
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
        System.out.println("--- Historial / Multas ---");
        System.out.println("10. Ver historial de préstamos");
        System.out.println("11. Consultar multas de un cliente");
        System.out.println("12. Pagar multas de un cliente");
        System.out.println("--- Reservas ---");
        System.out.println("13. Registrar reserva");
        System.out.println("14. Consultar reservas de un libro");
        System.out.println("--- Reportes ---");
        System.out.println("15. Reporte general del sistema");
        System.out.println("----------------------------------------------");
        System.out.println("0. Salir");
    }

    // =====================================================
    //  OPCIONES DE LIBROS / INVENTARIO   (Jerónimo)
    // =====================================================

    private void registrarLibro() {
        System.out.println(">> REGISTRAR LIBRO");
        String id = leerTextoObligatorio("Identificador único: ");

        if (inventario.buscarPorId(id) != null) {
            System.out.println("ERROR: ya existe un libro con ese identificador.");
            return;
        }

        String titulo = leerTextoObligatorio("Título: ");
        String autor = leerTextoObligatorio("Autor: ");
        String editorial = leerTextoObligatorio("Editorial: ");
        int anio = leerEnteroEnRango("Año de publicación: ", 1, Year.now().getValue());

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

        // Un libro PRESTADO solo cambia de estado con "Registrar devolución" (opción 8);
        // si se cambiara aquí, el préstamo seguiría activo y el cliente quedaría bloqueado.
        if (libro.getEstado() == EstadoLibro.PRESTADO) {
            System.out.println("ERROR: el libro está PRESTADO. Use la opción 8 (Registrar devolución).");
            return;
        }

        System.out.println("Estados: 1) DISPONIBLE  2) PRESTADO  3) RETIRADO");
        EstadoLibro estado = EstadoLibro.porOpcion(leerEntero("Nuevo estado: "));
        if (estado == null) {
            System.out.println("ERROR: estado no válido.");
            return;
        }

        // Marcar como PRESTADO a mano crearía un préstamo "fantasma" sin cliente ni fecha.
        if (estado == EstadoLibro.PRESTADO) {
            System.out.println("ERROR: para prestar un libro use la opción 7 (Registrar préstamo).");
            return;
        }

        // Si hay clientes esperando el libro, no se puede retirar del inventario.
        if (estado == EstadoLibro.RETIRADO && !servicioReservas.listarReservas(libro.getId()).isEmpty()) {
            System.out.println("ERROR: el libro tiene reservas pendientes; no se puede retirar.");
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
        String documento = leerSoloDigitos("Documento de identidad: ");

        if (gestionClientes.buscarPorDocumento(documento) != null) {
            System.out.println("ERROR: ya existe un cliente con ese documento.");
            return;
        }

        String nombre = leerTextoObligatorio("Nombre completo: ");
        String telefono = leerSoloDigitos("Teléfono: ");
        String direccion = leerTextoObligatorio("Dirección: ");

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
        LocalDate fecha = leerFechaOpcional("Fecha de devolución (AAAA-MM-DD) [Enter = hoy]: ");
        System.out.println(servicioPrestamos.registrarDevolucion(idLibro, fecha));
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
    //  HISTORIAL Y MULTAS
    // =====================================================

    private void verHistorialPrestamos() {
        System.out.println(">> HISTORIAL DE PRÉSTAMOS");
        List<Prestamo> historial = servicioPrestamos.listarHistorial();
        if (historial.isEmpty()) {
            System.out.println("Todavía no se ha realizado ningún préstamo.");
            return;
        }
        for (Prestamo prestamo : historial) {
            System.out.println(prestamo.mostrarInformacion());
        }
    }

    private void consultarMultasCliente() {
        System.out.println(">> MULTAS DE UN CLIENTE");
        Cliente cliente = pedirCliente();
        if (cliente == null) {
            return;
        }
        List<Multa> multas = servicioMultas.listarPorCliente(cliente.getDocumento());
        System.out.println("Cliente: " + cliente.getNombreCompleto() + " [" + cliente.getDocumento() + "]");
        if (multas.isEmpty()) {
            System.out.println("El cliente no tiene multas registradas.");
        } else {
            for (Multa multa : multas) {
                System.out.println("  - " + multa.mostrarInformacion());
            }
        }
        System.out.println("TOTAL PENDIENTE: $" + cliente.getMultaPendiente()
                + "  (tarifa: $" + Multa.VALOR_POR_DIA + " por día de retraso, plazo de "
                + Prestamo.DIAS_PRESTAMO + " días)");
    }

    private void pagarMultasCliente() {
        System.out.println(">> PAGAR MULTAS");
        Cliente cliente = pedirCliente();
        if (cliente == null) {
            return;
        }
        if (!cliente.tieneMultasPendientes()) {
            System.out.println("El cliente no tiene multas pendientes.");
            return;
        }
        long pagado = servicioMultas.pagarMultas(cliente);
        System.out.println("Pago registrado por $" + pagado + ". El cliente queda a paz y salvo.");
    }

    // =====================================================
    //  RESERVAS
    // =====================================================

    private void registrarReserva() {
        System.out.println(">> REGISTRAR RESERVA");
        String idLibro = leerTexto("Id del libro: ");
        String documento = leerTexto("Documento del cliente: ");
        System.out.println(servicioReservas.registrarReserva(idLibro, documento));
    }

    private void consultarReservasLibro() {
        System.out.println(">> RESERVAS DE UN LIBRO");
        String idLibro = leerTexto("Id del libro: ");
        Libro libro = inventario.buscarPorId(idLibro);
        if (libro == null) {
            System.out.println("ERROR: no existe un libro con ese id.");
            return;
        }
        System.out.println(libro.mostrarInformacion());
        List<Reserva> reservas = servicioReservas.listarReservas(libro.getId());
        if (reservas.isEmpty()) {
            System.out.println("El libro no tiene reservas.");
            return;
        }
        System.out.println("Lista de espera (en orden de solicitud):");
        int posicion = 1;
        for (Reserva reserva : reservas) {
            Cliente c = gestionClientes.buscarPorDocumento(reserva.getDocumentoCliente());
            String nombre = c != null ? c.getNombreCompleto() + " - " : "";
            System.out.println("  " + posicion + ". " + nombre + reserva.mostrarInformacion());
            posicion++;
        }
    }

    // =====================================================
    //  REPORTES
    // =====================================================

    private void mostrarReporteGeneral() {
        System.out.println(">> REPORTE GENERAL DEL SISTEMA");
        System.out.println("----------------------------------------------");
        System.out.println(servicioReportes.generarReporteGeneral());
        System.out.println("----------------------------------------------");
    }

    // =====================================================
    //  MÉTODOS AUXILIARES DE LECTURA POR CONSOLA
    // =====================================================

    private Cliente pedirCliente() {
        String documento = leerTexto("Documento del cliente: ");
        Cliente cliente = gestionClientes.buscarPorDocumento(documento);
        if (cliente == null) {
            System.out.println("ERROR: no existe un cliente con ese documento.");
        }
        return cliente;
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        try {
            // El ';' se reemplaza porque es el separador de los archivos de datos.
            return sc.nextLine().trim().replace(';', ',');
        } catch (NoSuchElementException e) {
            // La entrada se cerró (p. ej. contenedor ejecutado sin -it): se sale sin perder datos.
            System.out.println("\nEntrada cerrada. Guardando y saliendo...");
            persistencia.guardar();
            System.exit(0);
            return "";
        }
    }

    /**
     * Lee un texto que no puede quedar vacío.
     */
    private String leerTextoObligatorio(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Este campo es obligatorio.");
        }
    }

    /**
     * Lee un texto que solo puede contener números (documento, teléfono).
     */
    private String leerSoloDigitos(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            if (texto.matches("\\d{5,15}")) {
                return texto;
            }
            System.out.println("Ingrese solo números (entre 5 y 15 dígitos).");
        }
    }

    private int leerEnteroEnRango(String mensaje, int minimo, int maximo) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= minimo && valor <= maximo) {
                return valor;
            }
            System.out.println("El valor debe estar entre " + minimo + " y " + maximo + ".");
        }
    }

    /**
     * Lee una fecha AAAA-MM-DD. Si el usuario presiona Enter, se usa la fecha de hoy.
     */
    private LocalDate leerFechaOpcional(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            if (texto.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Use AAAA-MM-DD, por ejemplo 2026-11-30.");
            }
        }
    }

    /**
     * Lee un número entero y evita que el programa se caiga
     * si el usuario escribe letras.
     */
    private int leerEntero(String mensaje) {
        while (true) {
            String entrada = leerTexto(mensaje);
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }
    }
}
