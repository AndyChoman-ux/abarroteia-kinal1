package main.java.com.vyorg.abarroteria.kinal.service;

import javafx.collections.ObservableList;
import main.java.com.vyorg.abarroteria.kinal.model.Producto;
import main.java.com.vyorg.abarroteria.kinal.repository.ProductoRepository;


public class DashboardService {

    private final ProductoRepository productoRepository;

    public DashboardService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public ObservableList<Producto> findProducto() {
        if (productoRepository.findAll() == null) {
            throw new RuntimeException("Sin productos");
        } else {
            return productoRepository.findAll();
        }
    }

    public void eliminarProducto(String idProducto) {
        if (idProducto == null || idProducto.isBlank()) {
            throw new RuntimeException("Debe seleccionar un producto valido");
        }

        boolean eliminado = productoRepository.eliminarProducto(idProducto);

        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar el producto");
        }
    }

    public void descontarStock(String idProducto, int cantidad) {
        if (idProducto == null || idProducto.isBlank()) {
            throw new RuntimeException("Debe seleccionar un producto valido");
        }
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }

        boolean actualizado = productoRepository.descontarStock(idProducto, cantidad);

        if (!actualizado) {
            throw new RuntimeException("Stock insuficiente");
        }
    }
}