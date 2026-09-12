package main.java.com.vyorg.abarroteria.kinal.service;

import java.math.BigDecimal;
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
    public void agregarProducto(String idProducto, String nombreProducto, int stock, BigDecimal precio) {
        if (idProducto == null || idProducto.isBlank()) {
            throw new RuntimeException("El ID del producto no es válido");
        }
        if (nombreProducto == null || nombreProducto.isBlank()) {
            throw new RuntimeException("El nombre del producto no puede estar vacío");
        }
        if (stock < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El precio debe ser un valor válido");
        }

        Producto nuevoProducto = new Producto(idProducto, nombreProducto, stock, precio);
        boolean guardado = productoRepository.guardarProducto(nuevoProducto);

        if (!guardado) {
            throw new RuntimeException("No se pudo agregar el producto");
        }
    }

    public void actualizarProducto(String idProducto, String nombreProducto, int stock, BigDecimal precio) {
        if (idProducto == null || idProducto.isBlank()) {
            throw new RuntimeException("Debe seleccionar un producto válido");
        }
        if (nombreProducto == null || nombreProducto.isBlank()) {
            throw new RuntimeException("El nombre del producto no puede estar vacío");
        }
        if (stock < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El precio debe ser un valor válido");
        }

        Producto productoActualizado = new Producto(idProducto, nombreProducto, stock, precio);
        boolean actualizado = productoRepository.actualizarProducto(productoActualizado);

        if (!actualizado) {
            throw new RuntimeException("No se pudo actualizar el producto");
        }
    }
}