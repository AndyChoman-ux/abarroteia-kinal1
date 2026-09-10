package main.java.com.vyorg.abarroteria.kinal.model;

import java.sql.Timestamp;

public class Venta {
    private int idVenta;
    private Timestamp fecha;
    private double total;
    private String cliente;

    // Constructor vacío
    public Venta() {
    }

    // Constructor con parámetros
    public Venta(int idVenta, Timestamp fecha, double total, String cliente) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.total = total;
        this.cliente = cliente;
    }

    // Getters y Setters
    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}