package main.java.com.vyorg.abarroteria.kinal.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import main.java.com.vyorg.abarroteria.kinal.model.CarritoItem;
import main.java.com.vyorg.abarroteria.kinal.model.DetalleVenta;
import main.java.com.vyorg.abarroteria.kinal.model.Producto;
import main.java.com.vyorg.abarroteria.kinal.service.AuthService;
import main.java.com.vyorg.abarroteria.kinal.service.DashboardService;
import main.java.com.vyorg.abarroteria.kinal.service.HistorialVentasService;
import main.java.com.vyorg.abarroteria.kinal.service.NotificacionService;
import main.java.com.vyorg.abarroteria.kinal.util.FacturaPdfGenerator;
import main.java.com.vyorg.abarroteria.kinal.util.ProductoCardFactory;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;
import main.java.com.vyorg.abarroteria.kinal.util.SesionUsuario;

public class DashboardAdminController implements Initializable {

    private final AuthService authService;
    private final DashboardService dashboardService;
    private final SceneManager sceneManager;
    private final NotificacionService notificacionService = new NotificacionService();
    private final HistorialVentasService historialVentasService = new HistorialVentasService();

    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private HBox hboxCategorias;
    @FXML
    private FlowPane flowProductos;
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

    private ObservableList<Producto> listaProductos;
    private FilteredList<Producto> productosFiltrados;
    private Producto productoSeleccionado;
    private VBox tarjetaSeleccionada;
    private String categoriaSeleccionada = "Todos";
    private final ObservableList<CarritoItem> carrito = FXCollections.observableArrayList();

    public DashboardAdminController(AuthService authService, DashboardService dashboardService, SceneManager sceneManager) {
        this.authService = authService;
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

    private void actualizarBadgeNotificaciones() {
        int noLeidas = notificacionService.contarNoLeidas();
        lblBadgeNotificaciones.setText(String.valueOf(noLeidas));
        boolean hayNoLeidas = noLeidas > 0;
        lblBadgeNotificaciones.setVisible(hayNoLeidas);
        lblBadgeNotificaciones.setManaged(hayNoLeidas);
    }

    @FXML
    private void handleVerNotificaciones(ActionEvent event) {
        try {
            sceneManager.showNotificacionesPopup((Node) event.getSource(), this::actualizarBadgeNotificaciones);
        } catch (Exception e) {
            sceneManager.showAlertInfo("Error", "No se pudo abrir notificaciones", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleVerHistorial() {
        try {
            sceneManager.showHistorialVentas();
        } catch (Exception e) {
            sceneManager.showAlertInfo("Error", "No se pudo abrir el historial", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCerrarSesion() {
        boolean confirmado = sceneManager.showConfirmation(
                "Cerrar sesion", "Confirmar cierre de sesion",
                "¿Desea cerrar sesion y volver a la pantalla de inicio?");
        if (!confirmado) return;

        try {
            SesionUsuario.cerrarSesion();
            sceneManager.showLoginView();
        } catch (Exception e) {
            sceneManager.showAlertInfo("Error al cerrar sesion", "No se pudo cerrar sesion", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAsignarAdmin() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Asignar administrador");
        dialog.setHeaderText("Ingrese el correo del usuario a promover a administrador");
        dialog.setContentText("Correo:");
        sceneManager.estilizarDialogo(dialog);

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(email -> {
            try {
                boolean actualizado = authService.asignarRolAdmin(email);
                if (actualizado) {
                    sceneManager.showAlertInfo("Listo", "Usuario actualizado",
                            "El usuario ahora tiene rol de administrador", Alert.AlertType.INFORMATION);
                } else {
                    sceneManager.showAlertInfo("No encontrado", "No se pudo asignar el rol",
                            "No existe un usuario registrado con ese correo", Alert.AlertType.WARNING);
                }
            } catch (RuntimeException e) {
                sceneManager.showAlertInfo("Error", "No se pudo asignar el rol", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleAgregarProducto() {
        Optional<Producto> resultado = mostrarDialogoProducto("Agregar producto", null);
        resultado.ifPresent(producto -> {
            try {
                dashboardService.agregarProducto(producto.getIdProducto(), producto.getNombreProducto(),
                        producto.getStock(), producto.getPrecio(), producto.getRutaImagen(), producto.getCategoria());
                handleLoadDataTableView();
                filtrarProductos(txtBuscarProducto.getText());
                sceneManager.showAlertInfo("Producto agregado", "Listo",
                        "El producto se agrego correctamente", Alert.AlertType.INFORMATION);
            } catch (RuntimeException e) {
                sceneManager.showAlertInfo("Error al agregar", "No se pudo agregar el producto",
                        e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleActualizarProducto() {
        if (productoSeleccionado == null) {
            sceneManager.showAlertInfo("Ningun producto seleccionado", "Seleccione un producto",
                    "Debe seleccionar un producto de la lista para poder actualizarlo", Alert.AlertType.WARNING);
            return;
        }

        Optional<Producto> resultado = mostrarDialogoProducto("Actualizar producto", productoSeleccionado);
        resultado.ifPresent(producto -> {
            try {
                dashboardService.actualizarProducto(producto.getIdProducto(), producto.getNombreProducto(),
                        producto.getStock(), producto.getPrecio(), producto.getRutaImagen(), producto.getCategoria());
                handleLoadDataTableView();
                filtrarProductos(txtBuscarProducto.getText());
                sceneManager.showAlertInfo("Producto actualizado", "Listo",
                        "El producto se actualizo correctamente", Alert.AlertType.INFORMATION);
            } catch (RuntimeException e) {
                sceneManager.showAlertInfo("Error al actualizar", "No se pudo actualizar el producto",
                        e.getMessage(), Alert.AlertType.ERROR);
            }
        });
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

        private Optional<Producto> mostrarDialogoProducto(String titulo, Producto productoExistente) {
        Dialog<Producto> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(productoExistente == null
                ? "Ingrese los datos del nuevo producto"
                : "Modifique los datos de " + productoExistente.getNombreProducto());

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        TextField txtId = new TextField();
        txtId.setPromptText("ID del producto");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del producto");
        TextField txtStock = new TextField();
        txtStock.setPromptText("Stock");
        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Precio");

        ComboBox<String> cbCategoriaDialogo = new ComboBox<>();
        cbCategoriaDialogo.setEditable(true);
        cbCategoriaDialogo.setPromptText("Categoria");
        java.util.Set<String> categoriasExistentes = new java.util.TreeSet<>();
        for (Producto p : listaProductos) {
            categoriasExistentes.add(p.getCategoria());
        }
        cbCategoriaDialogo.setItems(FXCollections.observableArrayList(categoriasExistentes));

        ImageView previewImagen = new ImageView();
        previewImagen.setFitWidth(80.0);
        previewImagen.setFitHeight(80.0);
        previewImagen.setPreserveRatio(true);
        Button btnSeleccionarImagen = new Button("Seleccionar imagen");

        String[] rutaImagenSeleccionada = { null };

        if (productoExistente != null) {
            txtId.setText(productoExistente.getIdProducto());
            txtId.setDisable(true);
            txtNombre.setText(productoExistente.getNombreProducto());
            txtStock.setText(String.valueOf(productoExistente.getStock()));
            txtPrecio.setText(productoExistente.getPrecio().toPlainString());
            cbCategoriaDialogo.setValue(productoExistente.getCategoria());
            rutaImagenSeleccionada[0] = productoExistente.getRutaImagen();
        } else {
            cbCategoriaDialogo.setValue("General");
        }
        previewImagen.setImage(ProductoCardFactory.cargarImagen(rutaImagenSeleccionada[0]));

        btnSeleccionarImagen.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Seleccionar imagen del producto");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg"));
            File archivo = chooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (archivo != null) {
                rutaImagenSeleccionada[0] = archivo.getAbsolutePath();
                previewImagen.setImage(new Image(archivo.toURI().toString(), 80, 80, true, true));
            }
        });

        VBox columnaImagen = new VBox(8.0, previewImagen, btnSeleccionarImagen);
        columnaImagen.setAlignment(Pos.CENTER);

        grid.add(new Label("ID:"), 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(new Label("Stock:"), 0, 2);
        grid.add(txtStock, 1, 2);
        grid.add(new Label("Precio:"), 0, 3);
        grid.add(txtPrecio, 1, 3);
        grid.add(new Label("Imagen:"), 0, 4);
        grid.add(columnaImagen, 1, 4);
        grid.add(new Label("Categoria:"), 0, 5);
        grid.add(cbCategoriaDialogo, 1, 5);

        dialog.getDialogPane().setContent(grid);
        sceneManager.estilizarDialogo(dialog);

        Button botonGuardarNodo = (Button) dialog.getDialogPane().lookupButton(btnGuardar);

        Runnable validar = () -> {
            boolean idValido = productoExistente != null || (txtId.getText() != null && !txtId.getText().trim().isEmpty());
            boolean nombreValido = txtNombre.getText() != null && !txtNombre.getText().trim().isEmpty();

            boolean stockValido;
            try {
                stockValido = Integer.parseInt(txtStock.getText().trim()) >= 0;
            } catch (Exception e) {
                stockValido = false;
            }

            boolean precioValido;
            try {
                precioValido = new BigDecimal(txtPrecio.getText().trim()).compareTo(BigDecimal.ZERO) >= 0;
            } catch (Exception e) {
                precioValido = false;
            }

            marcarValidez(txtId, idValido);
            marcarValidez(txtNombre, nombreValido);
            marcarValidez(txtStock, stockValido);
            marcarValidez(txtPrecio, precioValido);

            botonGuardarNodo.setDisable(!(idValido && nombreValido && stockValido && precioValido));
        };

        txtId.textProperty().addListener((obs, oldVal, newVal) -> validar.run());
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> validar.run());
        txtStock.textProperty().addListener((obs, oldVal, newVal) -> validar.run());
        txtPrecio.textProperty().addListener((obs, oldVal, newVal) -> validar.run());

        validar.run();

        dialog.setResultConverter(boton -> {
            if (boton != btnGuardar) {
                return null;
            }
            try {
                String id = txtId.getText() == null ? "" : txtId.getText().trim();
                String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
                int stock = Integer.parseInt(txtStock.getText().trim());
                BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
                String rutaFinal = guardarImagenSiEsNueva(id, rutaImagenSeleccionada[0]);
                String categoria = cbCategoriaDialogo.getEditor().getText() == null || cbCategoriaDialogo.getEditor().getText().isBlank()
                        ? "General" : cbCategoriaDialogo.getEditor().getText().trim();
                return new Producto(id, nombre, stock, precio, rutaFinal, categoria);
            } catch (NumberFormatException e) {
                sceneManager.showAlertInfo("Datos invalidos", "Revise los datos",
                        "Stock y precio deben ser numeros validos", Alert.AlertType.ERROR);
                return null;
            }
        });

        return dialog.showAndWait();
    }

    private void marcarValidez(TextField campo, boolean valido) {
        if (valido) {
            campo.getStyleClass().remove("campo-invalido");
        } else if (!campo.getStyleClass().contains("campo-invalido")) {
            campo.getStyleClass().add("campo-invalido");
        }
    }

    private String guardarImagenSiEsNueva(String idProducto, String rutaSeleccionada) {
        if (rutaSeleccionada == null || rutaSeleccionada.isBlank()) {
            return null;
        }

        File origen = new File(rutaSeleccionada);
        File carpetaDestino = new File(System.getProperty("user.home"), "Imagenes_Productos_Kinal");
        if (!carpetaDestino.exists()) {
            carpetaDestino.mkdirs();
        }

        String extension = origen.getName().contains(".")
                ? origen.getName().substring(origen.getName().lastIndexOf('.'))
                : ".png";
        File destino = new File(carpetaDestino, idProducto + extension);

        if (origen.getAbsolutePath().equals(destino.getAbsolutePath())) {
            return destino.getAbsolutePath();
        }

        try {
            Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destino.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar la imagen del producto");
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