package main.java.com.vyorg.abarroteria.kinal.model;

import java.sql.Timestamp;

public class MovimientoInventario {
    private int idMovimiento;
    private String idProducto;
    private String nombreProducto;
    private String tipoMovimiento;
    private String detalle;
    private String usuario;
    private Timestamp fecha;

    public MovimientoInventario(int idMovimiento, String idProducto, String nombreProducto,
            String tipoMovimiento, String detalle, String usuario, Timestamp fecha) {
        this.idMovimiento = idMovimiento;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.tipoMovimiento = tipoMovimiento;
        this.detalle = detalle;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getUsuario() {
        return usuario;
    }

    public Timestamp getFecha() {
        return fecha;
    }
}