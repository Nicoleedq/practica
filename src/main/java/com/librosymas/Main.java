package com.librosymas;

import java.util.Scanner;

public class Main {

    // Un solo Scanner para todo el programa.
    // Crear varios Scanner sobre System.in genera errores al leer.
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elija una opción: ");
            switch (opcion) {
                case 0 -> System.out.println("¡Hasta pronto!");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0); // el menú se repite hasta que elijan 0
    }

    private static void mostrarMenu() {
        System.out.println("\n===== BIBLIOTECA LIBROSYMAS =====");
        System.out.println("0. Salir");
    }

    // ===== UTILIDADES DE LECTURA (compartidas) =====

    /** Lee un número entero. Si el usuario escribe letras, vuelve a preguntar. */
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un número.");
            }
        }
    }

    /** Lee un texto que no puede quedar vacío. */
    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim(); // trim() quita espacios al inicio y final
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Este campo no puede estar vacío.");
        }
    }
}