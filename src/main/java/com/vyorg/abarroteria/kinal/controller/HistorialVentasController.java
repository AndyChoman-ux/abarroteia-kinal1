package main.java.com.vyorg.abarroteria.kinal.controller;

import main.java.com.vyorg.abarroteria.kinal.model.DetalleVenta;
import main.java.com.vyorg.abarroteria.kinal.model.Venta;
import main.java.com.vyorg.abarroteria.kinal.service.HistorialVentasService;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import main.java.com.vyorg.abarroteria.kinal.util.SesionUsuario;

public class HistorialVentasController implements Initializable {

    @FXML
    private TableView<Venta> tblVentas;
    @FXML
    private TableColumn<Venta, String> colFactura;
    @FXML
    private TableColumn<Venta, Timestamp> colFecha;
    @FXML
    private TableColumn<Venta, String> colCliente;
    @FXML
    private TableColumn<Venta, String> colMetodoPago;
    @FXML
    private TableColumn<Venta, Double> colTotal;
    @FXML
    private TableColumn<Venta, String> colEstado;
    @FXML
    private TableColumn<Venta, Void> colAcciones;

    private final HistorialVentasService historialVentasService = new HistorialVentasService();
    
    // Cambiado: Ahora la referencia es inyectada desde SceneManager
    private SceneManager sceneManager;
    
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // Setter para recibir la instancia activa de SceneManager
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    // Acción para el botón de regresar (asegúrate de colocar onAction="#handleRegresarAction" en la vista FXML)
        @FXML
    private void handleRegresarAction() {
        if (sceneManager != null) {
            try {
                if (SesionUsuario.esAdmin()) {
                    sceneManager.showDashboardAdminView();
                } else {
                    sceneManager.showDashboardView();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        configurarColumnaAcciones();
        configurarDobleClicEnFila();
        cargarDatos();
    }

    private void configurarTabla() {
        colFactura.setCellValueFactory(new PropertyValueFactory<>("numeroFactura"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setCellFactory(col -> new TableCell<Venta, Timestamp>() {
            @Override
            protected void updateItem(Timestamp fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? null : formatoFecha.format(fecha));
            }
        });

        colTotal.setCellFactory(col -> new TableCell<Venta, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                setText(empty || total == null ? null : String.format(Locale.US, "Q%.2f", total));
            }
        });

        colEstado.setCellFactory(col -> new TableCell<Venta, String>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    setAlignment(Pos.CENTER);
                    if (estado.equalsIgnoreCase("Pagada")) {
                        setStyle("-fx-text-fill: #2e7d32; -fx-background-color: #c8e6c9; "
                               + "-fx-background-radius: 12; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #e65100; -fx-background-color: #ffe0b2; "
                               + "-fx-background-radius: 12; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(new Callback<TableColumn<Venta, Void>, TableCell<Venta, Void>>() {
            @Override
            public TableCell<Venta, Void> call(TableColumn<Venta, Void> param) {
                return new TableCell<Venta, Void>() {
                    private final Button btnVerDetalle = new Button("🔍");
                    private final Button btnFactura = new Button("🖨");
                    private final HBox contenedor = new HBox(6, btnVerDetalle, btnFactura);

                    {
                        contenedor.setAlignment(Pos.CENTER);
                        btnVerDetalle.setOnAction(e -> mostrarDetalleVenta(getTableView().getItems().get(getIndex())));
                        btnFactura.setOnAction(e -> abrirFactura(getTableView().getItems().get(getIndex())));
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Venta venta = getTableView().getItems().get(getIndex());
                            btnFactura.setDisable(venta.getRutaFactura() == null || venta.getRutaFactura().isBlank());
                            setGraphic(contenedor);
                        }
                    }
                };
            }
        });
    }

    private void configurarDobleClicEnFila() {
        tblVentas.setRowFactory(tv -> {
            TableRow<Venta> fila = new TableRow<>();
            fila.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !fila.isEmpty()) {
                    mostrarDetalleVenta(fila.getItem());
                }
            });
            return fila;
        });
    }

    private void abrirFactura(Venta venta) {
        if (venta.getRutaFactura() == null || venta.getRutaFactura().isBlank()) {
            return;
        }
        try {
            File archivo = new File(venta.getRutaFactura());
            if (archivo.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            } else {
                new Alert(Alert.AlertType.WARNING, "No se encontro el archivo de la factura").showAndWait();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo abrir la factura: " + e.getMessage()).showAndWait();
        }
    }

    private void cargarDatos() {
        ObservableList<Venta> ventas = FXCollections.observableArrayList(historialVentasService.listarHistorial());
        tblVentas.setItems(ventas);
    }

    private void mostrarDetalleVenta(Venta venta) {
        List<DetalleVenta> items = historialVentasService.obtenerDetalleVenta(venta.getIdVenta());

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalle de venta " + venta.getNumeroFactura());

        ButtonType tipoCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType tipoReimprimir = new ButtonType("Reimprimir Ticket", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(tipoCerrar, tipoReimprimir);

        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(10));
        contenido.setPrefWidth(500.0);

        Label titulo = new Label("Detalle de Venta " + venta.getNumeroFactura() + " - " + venta.getCliente());
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        HBox filaInfo1 = new HBox(30,
                new Label("Factura: " + venta.getNumeroFactura()),
                new Label("Fecha: " + (venta.getFecha() == null ? "" : formatoFecha.format(venta.getFecha()))));
        HBox filaInfo2 = new HBox(30,
                new Label("Cliente: " + venta.getCliente()),
                new Label("Vendedor: " + venta.getVendedor()));

        TableView<DetalleVenta> tablaItems = new TableView<>(FXCollections.observableArrayList(items));
        tablaItems.setPrefHeight(180.0);

        TableColumn<DetalleVenta, String> colCodigo = new TableColumn<>("Cód. Producto");
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));

        TableColumn<DetalleVenta, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        TableColumn<DetalleVenta, Integer> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        TableColumn<DetalleVenta, Double> colPrecioUnit = new TableColumn<>("P. Unit (Q)");
        colPrecioUnit.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecioUnit.setCellFactory(col -> celdaMoneda());

        TableColumn<DetalleVenta, Double> colSubtotalItem = new TableColumn<>("Subtotal (Q)");
        colSubtotalItem.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colSubtotalItem.setCellFactory(col -> celdaMoneda());

        tablaItems.getColumns().addAll(colCodigo, colDescripcion, colCantidad, colPrecioUnit, colSubtotalItem);

        VBox resumen = new VBox(4);
        resumen.setAlignment(Pos.CENTER_RIGHT);
        resumen.getChildren().addAll(
                new Label(String.format(Locale.US, "Subtotal: Q%.2f", venta.getSubtotal())),
                new Label(String.format(Locale.US, "Impuesto (12%%): Q%.2f", venta.getImpuesto())),
                boldLabel(String.format(Locale.US, "Total: Q%.2f", venta.getTotal()))
        );

        contenido.getChildren().addAll(titulo, filaInfo1, filaInfo2, tablaItems, resumen);
        dialog.getDialogPane().setContent(contenido);

        if (sceneManager != null) {
            sceneManager.estilizarDialogo(dialog);
        }

        dialog.setResultConverter(boton -> {
            if (boton == tipoReimprimir) {
                abrirFactura(venta);
            }
            return null;
        });

        dialog.showAndWait();
    }

    private TableCell<DetalleVenta, Double> celdaMoneda() {
        return new TableCell<DetalleVenta, Double>() {
            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);
                setText(empty || valor == null ? null : String.format(Locale.US, "Q%.2f", valor));
            }
        };
    }

    private Label boldLabel(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        return label;
    }
}