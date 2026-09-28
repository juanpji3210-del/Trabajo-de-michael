package com.aprendiz.demo.ejercicio3;

/**
 * EJERCICIO 3 - Problemas introducidos A PROPÓSITO para que SonarQube los detecte.
 * Copiar a: src/main/java/com/aprendiz/demo/ejercicio3/ (en una COPIA del proyecto).
 * Después de analizar, documentar cada issue y corregirlo.
 */
public class ProblemasIntencionales {

    // 1. Credencial escrita en el código (Vulnerability)
    private static final String PASSWORD = "admin123";

    // 2. Comparación de Strings con == (Bug)
    public boolean esAdministrador(String rol) {
        return rol == "ADMIN";
    }

    // 3. Complejidad ciclomática alta por if anidados (Code Smell)
    // 4. Variable local sin usar (Code Smell)
    public int calcularDescuento(int edad, boolean socio, boolean cupon, double total) {
        int variableSinUsar = 42;
        int descuento = 0;
        if (edad > 60) {
            if (socio) {
                if (cupon) {
                    if (total > 100) {
                        descuento = 30;
                    } else {
                        descuento = 20;
                    }
                } else {
                    if (total > 100) {
                        descuento = 15;
                    } else {
                        descuento = 10;
                    }
                }
            } else {
                if (cupon) {
                    descuento = 8;
                } else {
                    descuento = 5;
                }
            }
        } else {
            if (socio && cupon) {
                descuento = 7;
            } else if (socio || cupon) {
                descuento = 3;
            }
        }
        return descuento;
    }

    // 5. Bloque catch vacío (Code Smell)
    public int convertirANumero(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    public String obtenerPassword() {
        return PASSWORD;
    }
}
