package main.java.com.vyorg.abarroteria.kinal.model;

import java.sql.Timestamp;

public class Notificacion {
    private int idNotificacion;
    private String titulo;
    private String mensaje;
    private String idProducto;
    private String productoNombre;
    private Timestamp fecha;
    private boolean leida;

    public Notificacion() {}

    public Notificacion(int idNotificacion, String titulo, String mensaje,
                         String idProducto, String productoNombre,
                         Timestamp fecha, boolean leida) {
        this.idNotificacion = idNotificacion;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.idProducto = idProducto;
        this.productoNombre = productoNombre;
        this.fecha = fecha;
        this.leida = leida;
    }

    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getIdProducto() { return idProducto; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}