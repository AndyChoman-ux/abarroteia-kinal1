package main.java.com.vyorg.abarroteria.kinal.model;

import java.sql.Timestamp;

public class Venta {
    private int idVenta;
    private Timestamp fecha;
    private double total;
    private String cliente;
    private String metodoPago;
    private String estado;
    private String vendedor;
    private String rutaFactura;

    public Venta() {
    }

    public Venta(int idVenta, Timestamp fecha, double total, String cliente,
                 String metodoPago, String estado, String vendedor, String rutaFactura) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.total = total;
        this.cliente = cliente;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.vendedor = vendedor;
        this.rutaFactura = rutaFactura;
    }

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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getRutaFactura() {
        return rutaFactura;
    }

    public void setRutaFactura(String rutaFactura) {
        this.rutaFactura = rutaFactura;
    }

    /** Numero de factura derivado del id (no se guarda en la BD). */
    public String getNumeroFactura() {
        return "F-" + String.format("%04d", idVenta);
    }

    /** Subtotal sin IVA. El total ya incluye el 12% (igual que en FacturaPdfGenerator). */
    public double getSubtotal() {
        return total / 1.12;
    }

    /** IVA (12%) incluido dentro del total. */
    public double getImpuesto() {
        return total - getSubtotal();
    }
}