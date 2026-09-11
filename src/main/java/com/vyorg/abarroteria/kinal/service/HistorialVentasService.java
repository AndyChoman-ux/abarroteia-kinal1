package main.java.com.vyorg.abarroteria.kinal.service;

import main.java.com.vyorg.abarroteria.kinal.model.DetalleVenta;
import main.java.com.vyorg.abarroteria.kinal.model.Venta;
import main.java.com.vyorg.abarroteria.kinal.repository.VentaRepository;

import java.util.List;

public class HistorialVentasService {

    private final VentaRepository ventaRepository;

    public HistorialVentasService() {
        this.ventaRepository = new VentaRepository();
    }

    public List<Venta> listarHistorial() {
        return ventaRepository.obtenerTodasLasVentas();
    }

    public int registrarVenta(String cliente, double total, String metodoPago, String vendedor) {
        return ventaRepository.insertarVenta(cliente, total, metodoPago, vendedor);
    }

    public void registrarDetalleVenta(int idVenta, List<DetalleVenta> detalles) {
        ventaRepository.insertarDetalleVenta(idVenta, detalles);
    }

    public List<DetalleVenta> obtenerDetalleVenta(int idVenta) {
        return ventaRepository.obtenerDetalleVenta(idVenta);
    }

    public void actualizarFactura(int idVenta, String rutaFactura) {
        ventaRepository.actualizarRutaFactura(idVenta, rutaFactura);
    }
}