package com.librosymas.ui;

import java.util.Scanner;

/**
 * Menú principal del sistema por consola.
 * Cada integrante agregará aquí sus opciones.
 */
public class Menu {

    private final Scanner sc;

    public Menu(Scanner sc) {
        this.sc = sc;
    }

    public void iniciar() {
        int opcion = -1;
        while (opcion != 0) {
            mostrarOpciones();
            opcion = leerEntero("Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
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
        System.out.println("0. Salir");
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

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