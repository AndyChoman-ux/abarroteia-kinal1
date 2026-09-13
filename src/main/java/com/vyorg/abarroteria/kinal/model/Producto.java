package main.java.com.vyorg.abarroteria.kinal.model;

import java.math.BigDecimal;

public class Producto {
    private String idProducto;
    private String nombreProducto;
    private int stock;
    private BigDecimal precio;
    private String rutaImagen;
    private String categoria;

    public Producto(String idProducto, String nombreProducto, int stock, BigDecimal precio) {
        this(idProducto, nombreProducto, stock, precio, null, null);
    }

    public Producto(String idProducto, String nombreProducto, int stock, BigDecimal precio, String rutaImagen) {
        this(idProducto, nombreProducto, stock, precio, rutaImagen, null);
    }

    public Producto(String idProducto, String nombreProducto, int stock, BigDecimal precio, String rutaImagen, String categoria) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.stock = stock;
        this.precio = precio;
        this.rutaImagen = rutaImagen;
        this.categoria = (categoria == null || categoria.isBlank()) ? "General" : categoria;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = (categoria == null || categoria.isBlank()) ? "General" : categoria;
    }
}