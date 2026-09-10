package main.java.com.vyorg.abarroteria.kinal.controller;

import main.java.com.vyorg.abarroteria.kinal.model.Venta;
import main.java.com.vyorg.abarroteria.kinal.service.HistorialVentasService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;

public class HistorialVentasController implements Initializable {

    @FXML
    private TableView<Venta> tblVentas;

    @FXML
    private TableColumn<Venta, Integer> colId;

    @FXML
    private TableColumn<Venta, Timestamp> colFecha;

    @FXML
    private TableColumn<Venta, Double> colTotal;

    @FXML
    private TableColumn<Venta, String> colCliente;

    private final HistorialVentasService historialVentasService = new HistorialVentasService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        // Vincula los atributos de la clase Venta con las columnas de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
    }

    private void cargarDatos() {
        ObservableList<Venta> ventas = FXCollections.observableArrayList(historialVentasService.listarHistorial());
        tblVentas.setItems(ventas);
    }
}
