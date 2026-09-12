package main.java.com.vyorg.abarroteria.kinal.util;

/**
 * Guarda el nombre y el rol del usuario que inicio sesion, para poder
 * registrarlo como "vendedor" en cada venta y saber a que dashboard regresar.
 */
public class SesionUsuario {

    private static String nombreUsuario;
    private static boolean esAdmin;

    private SesionUsuario() {
    }

    public static void iniciarSesion(String nombre) {
        nombreUsuario = nombre;
    }

    public static void iniciarSesion(String nombre, boolean admin) {
        nombreUsuario = nombre;
        esAdmin = admin;
    }

    public static String getNombreUsuario() {
        return nombreUsuario == null ? "Sin identificar" : nombreUsuario;
    }

    public static boolean esAdmin() {
        return esAdmin;
    }

    public static void cerrarSesion() {
        nombreUsuario = null;
        esAdmin = false;
    }
}