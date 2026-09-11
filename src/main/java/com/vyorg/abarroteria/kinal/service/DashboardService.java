package main.java.com.vyorg.abarroteria.kinal.service;

import javafx.collections.ObservableList;
import main.java.com.vyorg.abarroteria.kinal.model.Producto;
import main.java.com.vyorg.abarroteria.kinal.repository.ProductoRepository;

public class DashboardService {

    private static final int STOCK_MINIMO = 10;

    private final ProductoRepository productoRepository;
    private final NotificacionService notificacionService;

    public DashboardService(ProductoRepository productoRepository) {
        this(productoRepository, new NotificacionService());
    }

    public DashboardService(ProductoRepository productoRepository, NotificacionService notificacionService) {
        this.productoRepository = productoRepository;
        this.notificacionService = notificacionService;
    }

    public ObservableList<Producto> findProducto() {
        ObservableList<Producto> productos = productoRepository.findAll();

        if (productos == null) {
            throw new RuntimeException("Sin productos");
        }

        verificarStockBajo(productos);
        return productos;
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

    private void verificarStockBajo(ObservableList<Producto> productos) {
        for (Producto producto : productos) {
            if (producto.getStock() <= STOCK_MINIMO) {
                String mensaje = "El producto " + producto.getNombreProducto()
                        + " tiene stock bajo: quedan " + producto.getStock() + " unidades";
                notificacionService.crearNotificacionSiNoExiste("Stock bajo", mensaje,
                        producto.getIdProducto(), producto.getNombreProducto());
            }
        }
    }
}