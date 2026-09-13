package main.java.com.vyorg.abarroteria.kinal.controller;

import main.java.com.vyorg.abarroteria.kinal.model.MovimientoInventario;
import main.java.com.vyorg.abarroteria.kinal.repository.ProductoRepository;
import main.java.com.vyorg.abarroteria.kinal.service.DashboardService;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;
import main.java.com.vyorg.abarroteria.kinal.util.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class HistorialInventarioController implements Initializable {

    @FXML
    private TableView<MovimientoInventario> tblMovimientos;
    @FXML
    private TableColumn<MovimientoInventario, Timestamp> colFecha;
    @FXML
    private TableColumn<MovimientoInventario, String> colTipo;
    @FXML
    private TableColumn<MovimientoInventario, String> colIdProducto;
    @FXML
    private TableColumn<MovimientoInventario, String> colNombreProducto;
    @FXML
    private TableColumn<MovimientoInventario, String> colDetalle;
    @FXML
    private TableColumn<MovimientoInventario, String> colUsuario;

    private final DashboardService dashboardService = new DashboardService(new ProductoRepository());
    private SceneManager sceneManager;
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void handleRegresarAction() {
        if (sceneManager == null) {
            return;
        }
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMovimiento"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("detalle"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setCellFactory(col -> new TableCell<MovimientoInventario, Timestamp>() {
            @Override
            protected void updateItem(Timestamp fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText(empty || fecha == null ? null : formatoFecha.format(fecha));
            }
        });
    }

    private void cargarDatos() {
        List<MovimientoInventario> movimientos = dashboardService.obtenerMovimientos();
        ObservableList<MovimientoInventario> lista = FXCollections.observableArrayList(movimientos);
        tblMovimientos.setItems(lista);
    }
}