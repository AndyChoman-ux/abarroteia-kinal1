package main.java.com.vyorg.abarroteria.kinal.service;

import main.java.com.vyorg.abarroteria.kinal.model.MovimientoInventario;
import main.java.com.vyorg.abarroteria.kinal.repository.MovimientoInventarioRepository;
import main.java.com.vyorg.abarroteria.kinal.util.SesionUsuario;

import java.util.List;

public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public MovimientoInventarioService() {
        this.movimientoInventarioRepository = new MovimientoInventarioRepository();
    }

    public void registrarMovimiento(String idProducto, String nombreProducto, String tipoMovimiento, String detalle) {
        movimientoInventarioRepository.insertarMovimiento(idProducto, nombreProducto, tipoMovimiento, detalle, SesionUsuario.getNombreUsuario());
    }

    public List<MovimientoInventario> listarMovimientos() {
        return movimientoInventarioRepository.obtenerTodos();
    }
}