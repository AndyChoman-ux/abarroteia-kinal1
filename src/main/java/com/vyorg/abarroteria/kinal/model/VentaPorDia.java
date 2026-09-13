package main.java.com.vyorg.abarroteria.kinal.model;

public class VentaPorDia {
    private String fecha;
    private double total;

    public VentaPorDia(String fecha, double total) {
        this.fecha = fecha;
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public double getTotal() {
        return total;
    }
}