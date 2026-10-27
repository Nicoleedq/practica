package com.librosymas;

import com.librosymas.modelo.Categoria;
import com.librosymas.modelo.Cliente;
import com.librosymas.modelo.Libro;
import com.librosymas.servicio.GestionClientes;
import com.librosymas.servicio.Inventario;
import com.librosymas.servicio.Persistencia;
import com.librosymas.servicio.ServicioMultas;
import com.librosymas.servicio.ServicioPrestamos;
import com.librosymas.servicio.ServicioReportes;
import com.librosymas.servicio.ServicioReservas;
import com.librosymas.ui.Menu;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Punto de entrada del sistema.
 * Crea los servicios, carga los datos guardados (o unos de prueba la primera vez) y lanza el menú.
 */
public class Main {

    public static void main(String[] args) {
        Inventario inventario = new Inventario();
        GestionClientes gestionClientes = new GestionClientes();
        ServicioMultas servicioMultas = new ServicioMultas(gestionClientes);
        ServicioReservas servicioReservas = new ServicioReservas(inventario, gestionClientes);
        ServicioPrestamos servicioPrestamos = new ServicioPrestamos(inventario, gestionClientes,
                servicioMultas, servicioReservas);
        ServicioReportes servicioReportes = new ServicioReportes(inventario, gestionClientes, servicioPrestamos);

        Persistencia persistencia = new Persistencia(inventario, gestionClientes,
                servicioPrestamos, servicioReservas, servicioMultas);

        if (persistencia.existenDatos()) {
            persistencia.cargar();
            System.out.println("Datos cargados desde: " + persistencia.getCarpeta());
        } else {
            cargarDatosDePrueba(inventario, gestionClientes);
            persistencia.guardar();
            System.out.println("Primera ejecución: se crearon datos de prueba en " + persistencia.getCarpeta());
        }

        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);
        Menu menu = new Menu(inventario, gestionClientes, servicioPrestamos, servicioReservas,
                servicioMultas, servicioReportes, persistencia, sc);
        menu.iniciar();
        sc.close();
    }

    /**
     * Datos iniciales para poder probar el sistema sin registrar todo a mano.
     * Solo se cargan la primera vez (cuando todavía no hay archivos de datos).
     */
    private static void cargarDatosDePrueba(Inventario inventario, GestionClientes gestionClientes) {
        inventario.registrarLibro(new Libro("L001", "Cien años de soledad", "Gabriel García Márquez",
                "Sudamericana", 1967, Categoria.LITERATURA));
        inventario.registrarLibro(new Libro("L002", "Breve historia del tiempo", "Stephen Hawking",
                "Crítica", 1988, Categoria.CIENCIA));
        inventario.registrarLibro(new Libro("L003", "Clean Code", "Robert C. Martin",
                "Prentice Hall", 2008, Categoria.TECNOLOGIA));

        gestionClientes.registrarCliente(new Cliente("1094123456", "Jerónimo Morales",
                "3001112233", "Cra 14 #10-20, Armenia"));
        gestionClientes.registrarCliente(new Cliente("1094654321", "Nicole Duque",
                "3004445566", "Calle 20 #15-30, Armenia"));
    }
}
