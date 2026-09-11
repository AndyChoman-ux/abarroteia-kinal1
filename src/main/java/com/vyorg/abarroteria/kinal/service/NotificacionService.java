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

    public void crearNotificacion(String titulo, String mensaje) {
        notificacionRepository.insertar(titulo, mensaje);
    }
}