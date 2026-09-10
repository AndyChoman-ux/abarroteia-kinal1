package main.java.com.vyorg.abarroteria.kinal.service;

import main.java.com.vyorg.abarroteria.kinal.model.Venta;
import main.java.com.vyorg.abarroteria.kinal.repository.VentaRepository;

import java.util.List;

public class HistorialVentasService {

    private final VentaRepository ventaRepository;

    public HistorialVentasService() {
        this.ventaRepository = new VentaRepository();
    }

    public List<Venta> listarHistorial() {
        // Aquí se pueden agregar validaciones o filtros si se requiere en el futuro
        return ventaRepository.obtenerTodasLasVentas();
    }
}
