package main.java.com.vyorg.abarroteria.kinal.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CarritoItem {

    private Producto producto;
    private int cantidad;

    public CarritoItem(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    @Override
    public String toString() {
        return producto.getNombreProducto() + "   x" + cantidad + "   Q"
                + getSubtotal().setScale(2, RoundingMode.HALF_UP);
    }
}