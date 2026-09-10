package main.java.com.vyorg.abarroteria.kinal.model;

import java.sql.Timestamp;

public class Notificacion {
    private int idNotificacion;
    private String titulo;
    private String mensaje;
    private Timestamp fecha;
    private boolean leida;

    public Notificacion() {}

    public Notificacion(int idNotificacion, String titulo, String mensaje, Timestamp fecha, boolean leida) {
        this.idNotificacion = idNotificacion;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.leida = leida;
    }

    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}
