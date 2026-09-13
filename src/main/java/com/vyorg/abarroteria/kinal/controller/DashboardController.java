package main.java.com.vyorg.abarroteria.kinal.controller;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import main.java.com.vyorg.abarroteria.kinal.model.CarritoItem;
import main.java.com.vyorg.abarroteria.kinal.model.DetalleVenta;
import main.java.com.vyorg.abarroteria.kinal.model.Producto;
import main.java.com.vyorg.abarroteria.kinal.service.DashboardService;
import main.java.com.vyorg.abarroteria.kinal.service.HistorialVentasService;
import main.java.com.vyorg.abarroteria.kinal.service.NotificacionService;
import main.java.com.vyorg.abarroteria.kinal.util.FacturaPdfGenerator;
import main.java.com.vyorg.abarroteria.kinal.util.ProductoCardFactory;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;
import main.java.com.vyorg.abarroteria.kinal.util.SesionUsuario;

public class DashboardController implements Initializable {

    private DashboardService dashboardService;
    private SceneManager sceneManager;

    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private HBox hboxCategorias;
    @FXML
    private FlowPane flowProductos;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private Label lblBadgeNotificaciones;
    @FXML
    private Label lblBienvenida;
    @FXML
    private ComboBox<String> cbListaPrecio;
    @FXML
    private ComboBox<String> cbNumeracion;
    @FXML
    private ComboBox<String> cbMetodoPago;
    @FXML
    private TextField txtCliente;
    @FXML
    private ListView<CarritoItem> listViewCarrito;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblCantidadProductos;

    private Timeline timelineNotificaciones;

    private ObservableList<Producto> listaProductos;
    private FilteredList<Producto> productosFiltrados;
    private Producto productoSeleccionado;
    private VBox tarjetaSeleccionada;
    private String categoriaSeleccionada = "Todos";
    private final ObservableList<CarritoItem> carrito = FXCollections.observableArrayList();
    private final NotificacionService notificacionService = new NotificacionService();
    private final HistorialVentasService historialVentasService = new HistorialVentasService();

    public DashboardController(DashboardService dashboardService, SceneManager sceneManager) {
        this.dashboardService = dashboardService;
        this.sceneManager = sceneManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handleLoadDataTableView();

        cbListaPrecio.setItems(FXCollections.observableArrayList("General", "Mayorista"));
        cbListaPrecio.getSelectionModel().selectFirst();

        cbNumeracion.setItems(FXCollections.observableArrayList("Ticket principal", "Ticket secundario"));
        cbNumeracion.getSelectionModel().selectFirst();

        cbMetodoPago.setItems(FXCollections.observableArrayList("Efectivo", "Tarjeta Cred."));
        cbMetodoPago.getSelectionModel().selectFirst();

        listViewCarrito.setItems(carrito);
        listViewCarrito.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleQuitarDelCarrito();
            }
        });

        txtBuscarProducto.textProperty().addListener((obs, oldValue, newValue) -> filtrarProductos(newValue));

        actualizarResumenCarrito();
        actualizarBadgeNotificaciones();
        lblBienvenida.setText("Bienvenido, " + SesionUsuario.getNombreUsuario());

        timelineNotificaciones = new Timeline(new KeyFrame(Duration.seconds(15), event -> actualizarBadgeNotificaciones()));
        timelineNotificaciones.setCycleCount(Timeline.INDEFINITE);
        timelineNotificaciones.play();
    }

    private void handleLoadDataTableView() {
        listaProductos = dashboardService.findProducto();
        productosFiltrados = new FilteredList<>(listaProductos, p -> true);
        renderizarCategorias();
        renderizarProductos();
    }

    private void renderizarCategorias() {
        hboxCategorias.getChildren().clear();
        java.util.Set<String> categorias = new java.util.TreeSet<>();
        categorias.add("Todos");
        for (Producto productoInstancia : listaProductos) {
            if (productoInstancia.getCategoria() != null) {
                categorias.add(productoInstancia.getCategoria());
            }
        }
        for (String categoria : categorias) {
            Button chip = new Button(categoria);
            chip.getStyleClass().add("chip-categoria");
            if (categoria.equals(categoriaSeleccionada)) {
                chip.getStyleClass().add("chip-categoria-activo");
            }
            chip.setOnAction(e -> {
                categoriaSeleccionada = categoria;
                renderizarCategorias();
                filtrarProductos(txtBuscarProducto.getText());
            });
            hboxCategorias.getChildren().add(chip);
        }
    }

    private void filtrarProductos(String texto) {
        if (productosFiltrados == null) {
            return;
        }

        String filtro = texto == null ? "" : texto.trim().toLowerCase();

        productosFiltrados.setPredicate(producto -> {
            boolean coincideTexto = filtro.isEmpty()
                    || producto.getNombreProducto().toLowerCase().contains(filtro)
                    || producto.getIdProducto().toLowerCase().contains(filtro);
            boolean coincideCategoria = "Todos".equals(categoriaSeleccionada)
                    || (producto.getCategoria() != null && producto.getCategoria().equals(categoriaSeleccionada));
            return coincideTexto && coincideCategoria;
        });

        renderizarProductos();
    }

    private void renderizarProductos() {
        flowProductos.getChildren().clear();
        productoSeleccionado = null;
        tarjetaSeleccionada = null;

        for (Producto producto : productosFiltrados) {
            VBox tarjeta = ProductoCardFactory.crear(producto);
            tarjeta.setOnMouseClicked(event -> seleccionarTarjeta(producto, tarjeta));
            flowProductos.getChildren().add(tarjeta);
        }
    }

    private void seleccionarTarjeta(Producto producto, VBox tarjeta) {
        if (tarjetaSeleccionada != null) {
            tarjetaSeleccionada.getStyleClass().remove("producto-card-seleccionada");
        }
        productoSeleccionado = producto;
        tarjetaSeleccionada = tarjeta;
        tarjeta.getStyleClass().add("producto-card-seleccionada");
    }

    @FXML
    private void handleEliminarProducto() {
        if (productoSeleccionado == null) {
            sceneManager.showAlertInfo("Ningun producto seleccionado", "Seleccione un producto",
                    "Debe seleccionar un producto de la lista para poder eliminarlo", Alert.AlertType.WARNING);
            return;
        }

        boolean confirmado = sceneManager.showConfirmation(
                "Eliminar " + productoSeleccionado.getNombreProducto(),
                "Confirmar eliminacion",
                "Esta accion no se puede deshacer. ¿Desea eliminar este producto?");

        if (!confirmado) {
            return;
        }

        try {
            dashboardService.eliminarProducto(productoSeleccionado.getIdProducto());
            handleLoadDataTableView();
            filtrarProductos(txtBuscarProducto.getText());
            sceneManager.showAlertInfo("Producto eliminado", "Listo",
                    "El producto se elimino correctamente", Alert.AlertType.INFORMATION);
        } catch (RuntimeException e) {
            sceneManager.showAlertInfo("Error al eliminar", "No se pudo eliminar el producto",
                    e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCerrarSesion() {
        boolean confirmado = sceneManager.showConfirmation(
                "Cerrar sesion",
                "Confirmar cierre de sesion",
                "¿Desea cerrar sesion y volver a la pantalla de inicio?");

        if (!confirmado) {
            return;
        }

        try {
            timelineNotificaciones.stop();
            SesionUsuario.cerrarSesion();
            sceneManager.showLoginView();
        } catch (Exception e) {
            sceneManager.showAlertInfo("Error al cerrar sesion", "No se pudo cerrar sesion",
                    e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleVerNotificaciones(javafx.event.ActionEvent event) {
        try {
            sceneManager.showNotificacionesPopup((javafx.scene.Node) event.getSource(), this::actualizarBadgeNotificaciones);
        } catch (Exception e) {
            sceneManager.showAlertInfo("Error", "No se pudo abrir notificaciones", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void actualizarBadgeNotificaciones() {
        int noLeidas = notificacionService.contarNoLeidas();
        lblBadgeNotificaciones.setText(String.valueOf(noLeidas));
        boolean hayNoLeidas = noLeidas > 0;
        lblBadgeNotificaciones.setVisible(hayNoLeidas);
        lblBadgeNotificaciones.setManaged(hayNoLeidas);
    }

    @FXML
    private void handleVerHistorial() {
        try {
            timelineNotificaciones.stop();
            sceneManager.showHistorialVentas();
        } catch (Exception e) {
            sceneManager.showAlertInfo("Error", "No se pudo abrir el historial", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAgregarCarrito() {
        if (productoSeleccionado == null) {
            sceneManager.showAlertInfo("Ningun producto seleccionado", "Seleccione un producto",
                    "Debe seleccionar un producto de la lista para agregarlo al carrito", Alert.AlertType.WARNING);
            return;
        }

        if (productoSeleccionado.getStock() <= 0) {
            sceneManager.showAlertInfo("Sin stock", "Producto agotado",
                    "El producto " + productoSeleccionado.getNombreProducto() + " no tiene stock disponible",
                    Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Cantidad");
        dialog.setHeaderText("Agregar " + productoSeleccionado.getNombreProducto() + " al carrito");
        dialog.setContentText("Cantidad:");
        sceneManager.estilizarDialogo(dialog);

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isEmpty()) {
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(resultado.get().trim());
        } catch (NumberFormatException e) {
            sceneManager.showAlertInfo("Cantidad invalida", "Dato incorrecto",
                    "Debe ingresar un numero entero valido", Alert.AlertType.ERROR);
            return;
        }

        if (cantidad <= 0) {
            sceneManager.showAlertInfo("Cantidad invalida", "Dato incorrecto",
                    "La cantidad debe ser mayor a 0", Alert.AlertType.ERROR);
            return;
        }

        CarritoItem itemExistente = null;
        for (CarritoItem item : carrito) {
            if (item.getProducto().getIdProducto().equals(productoSeleccionado.getIdProducto())) {
                itemExistente = item;
                break;
            }
        }

        int cantidadEnCarrito = itemExistente == null ? 0 : itemExistente.getCantidad();

        if (cantidadEnCarrito + cantidad > productoSeleccionado.getStock()) {
            notificacionService.crearNotificacion("Stock insuficiente",
                    "Solo hay " + productoSeleccionado.getStock() + " unidades disponibles de " + productoSeleccionado.getNombreProducto(),
                    productoSeleccionado.getIdProducto(), productoSeleccionado.getNombreProducto());
            actualizarBadgeNotificaciones();
            sceneManager.showAlertInfo("Stock insuficiente", "No hay suficiente stock",
                    "Solo hay " + productoSeleccionado.getStock() + " unidades disponibles de "
                            + productoSeleccionado.getNombreProducto(), Alert.AlertType.WARNING);
            return;
        }

        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
            listViewCarrito.refresh();
        } else {
            carrito.add(new CarritoItem(productoSeleccionado, cantidad));
        }

        actualizarResumenCarrito();
    }

    @FXML
    private void handleCancelarCarrito() {
        if (carrito.isEmpty()) {
            return;
        }

        boolean confirmado = sceneManager.showConfirmation(
                "Vaciar carrito",
                "Cancelar venta",
                "¿Desea eliminar todos los productos del carrito?");

        if (!confirmado) {
            return;
        }

        carrito.clear();
        actualizarResumenCarrito();
    }

    @FXML
    private void handleComprar() {
        if (carrito.isEmpty()) {
            sceneManager.showAlertInfo("Carrito vacio", "No hay productos",
                    "Agregue al menos un producto al carrito antes de comprar", Alert.AlertType.WARNING);
            return;
        }

        BigDecimal total = calcularTotal();
        String cliente = txtCliente.getText() == null || txtCliente.getText().isBlank()
                ? "Publico en general" : txtCliente.getText();
        String metodoPago = cbMetodoPago.getValue() == null ? "Efectivo" : cbMetodoPago.getValue();
        String vendedor = SesionUsuario.getNombreUsuario();

        boolean confirmado = sceneManager.showConfirmation(
                "Confirmar venta",
                "Total a cobrar: Q" + total.setScale(2, RoundingMode.HALF_UP),
                "Cliente: " + cliente + "\n¿Desea confirmar la venta?");

        if (!confirmado) {
            return;
        }

        try {
            for (CarritoItem item : carrito) {
                dashboardService.descontarStock(item.getProducto().getIdProducto(), item.getCantidad());
            }
        } catch (RuntimeException e) {
            sceneManager.showAlertInfo("Error en la venta", "No se pudo completar la compra",
                    e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        int idVenta = historialVentasService.registrarVenta(cliente, total.doubleValue(), metodoPago, vendedor);

        List<CarritoItem> carritoVenta = new ArrayList<>(carrito);

        List<DetalleVenta> detalles = new ArrayList<>();
        for (CarritoItem item : carritoVenta) {
            detalles.add(new DetalleVenta(
                    item.getProducto().getIdProducto(),
                    item.getProducto().getNombreProducto(),
                    item.getCantidad(),
                    item.getProducto().getPrecio().doubleValue(),
                    item.getSubtotal().doubleValue()
            ));
        }
        historialVentasService.registrarDetalleVenta(idVenta, detalles);

        Alert generando = new Alert(Alert.AlertType.INFORMATION);
        generando.setTitle("Procesando venta");
        generando.setHeaderText(null);
        generando.setContentText("Generando factura...");
        generando.show();

        PauseTransition espera = new PauseTransition(Duration.seconds(3));
        espera.setOnFinished(event -> {
            generando.close();
            try {
                File factura = FacturaPdfGenerator.generar(idVenta, cliente, carritoVenta, total);
                historialVentasService.actualizarFactura(idVenta, factura.getAbsolutePath());
                sceneManager.showAlertInfo("Venta realizada", "Compra exitosa",
                        "Se vendio un total de Q" + total.setScale(2, RoundingMode.HALF_UP)
                                + "\nSe genero la factura en PDF.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                sceneManager.showAlertInfo("Error al generar factura", "No se pudo crear el PDF",
                        e.getMessage(), Alert.AlertType.ERROR);
            }
        });
        espera.play();

        carrito.clear();
        actualizarResumenCarrito();
        handleLoadDataTableView();
        filtrarProductos(txtBuscarProducto.getText());
        actualizarBadgeNotificaciones();
    }

    private void handleQuitarDelCarrito() {
        CarritoItem seleccionado = listViewCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        boolean confirmado = sceneManager.showConfirmation(
                "Quitar producto",
                "Quitar " + seleccionado.getProducto().getNombreProducto(),
                "¿Desea quitar este producto del carrito?");

        if (!confirmado) {
            return;
        }

        carrito.remove(seleccionado);
        actualizarResumenCarrito();
    }

    private void actualizarResumenCarrito() {
        BigDecimal total = calcularTotal();
        lblTotal.setText("Q" + total.setScale(2, RoundingMode.HALF_UP));

        int totalProductos = 0;
        for (CarritoItem item : carrito) {
            totalProductos += item.getCantidad();
        }
        lblCantidadProductos.setText(totalProductos + " productos");
    }

    private BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem item : carrito) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
}