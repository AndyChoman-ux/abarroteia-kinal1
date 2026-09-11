package main.java.com.vyorg.abarroteria.kinal.service;

import main.java.com.vyorg.abarroteria.kinal.model.Notificacion;
import main.java.com.vyorg.abarroteria.kinal.repository.NotificacionRepository;

import java.util.List;

public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService() {
        this.notificacionRepository = new NotificacionRepository();
    }

    public List<Notificacion> listarNotificaciones() {
        return notificacionRepository.obtenerTodas();
    }

    public void crearNotificacion(String titulo, String mensaje, String idProducto, String productoNombre) {
        notificacionRepository.insertar(titulo, mensaje, idProducto, productoNombre);
    }

    /**
     * Crea la notificacion solo si no existe ya una identica,
     * para no llenar la tabla de avisos repetidos de stock bajo.
     */
    public void crearNotificacionSiNoExiste(String titulo, String mensaje, String idProducto, String productoNombre) {
        if (!notificacionRepository.existeNotificacionConMensaje(mensaje)) {
            notificacionRepository.insertar(titulo, mensaje, idProducto, productoNombre);
        }
    }

    public void marcarComoLeida(int idNotificacion) {
        notificacionRepository.marcarComoLeida(idNotificacion);
    }

    public void eliminarNotificacion(int idNotificacion) {
        notificacionRepository.eliminar(idNotificacion);
    }

    public void eliminarTodas() {
        notificacionRepository.eliminarTodas();
    }

    public int contarNoLeidas() {
        return notificacionRepository.contarNoLeidas();
    }
}