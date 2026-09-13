package main.java.com.vyorg.abarroteria.kinal.model;

public class ProductoMasVendido {
    private String nombreProducto;
    private int cantidadVendida;

    public ProductoMasVendido(String nombreProducto, int cantidadVendida) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }
}