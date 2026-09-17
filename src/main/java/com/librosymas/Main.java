package com.librosymas;

import com.librosymas.modelo.Categoria;
import com.librosymas.modelo.Cliente;
import com.librosymas.modelo.Libro;
import com.librosymas.servicio.GestionClientes;
import com.librosymas.servicio.Inventario;
import com.librosymas.servicio.ServicioPrestamos;
import com.librosymas.ui.Menu;

import java.util.Scanner;

/**
 * Punto de entrada del sistema.
 * Crea los servicios, carga unos datos de prueba y lanza el menú.
 */
public class Main {

    public static void main(String[] args) {
        Inventario inventario = new Inventario();
        GestionClientes gestionClientes = new GestionClientes();
        ServicioPrestamos servicioPrestamos = new ServicioPrestamos(inventario, gestionClientes);

        cargarDatosDePrueba(inventario, gestionClientes);

        Scanner sc = new Scanner(System.in);
        Menu menu = new Menu(inventario, gestionClientes, servicioPrestamos, sc);
        menu.iniciar();
        sc.close();
    }

    /**
     * Datos iniciales para poder probar el sistema sin registrar todo a mano.
     * Se puede borrar si el profesor pide arrancar con el sistema vacío.
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
