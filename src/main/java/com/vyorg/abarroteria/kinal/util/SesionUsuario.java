package main.java.com.vyorg.abarroteria.kinal.util;

/**
 * Guarda el nombre del usuario que inicio sesion para poder
 * registrarlo como "vendedor" en cada venta.
 */
public class SesionUsuario {

    private static String nombreUsuario;

    private SesionUsuario() {
    }

    public static void iniciarSesion(String nombre) {
        nombreUsuario = nombre;
    }

    public static String getNombreUsuario() {
        return nombreUsuario == null ? "Sin identificar" : nombreUsuario;
    }

    public static void cerrarSesion() {
        nombreUsuario = null;
    }
}