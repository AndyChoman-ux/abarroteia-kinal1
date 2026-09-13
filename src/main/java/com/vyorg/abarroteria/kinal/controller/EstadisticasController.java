package main.java.com.vyorg.abarroteria.kinal.controller;

import main.java.com.vyorg.abarroteria.kinal.model.ProductoMasVendido;
import main.java.com.vyorg.abarroteria.kinal.model.VentaPorDia;
import main.java.com.vyorg.abarroteria.kinal.service.HistorialVentasService;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;
import main.java.com.vyorg.abarroteria.kinal.util.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EstadisticasController implements Initializable {

    @FXML
    private LineChart<String, Number> lineChartVentas;
    @FXML
    private CategoryAxis ejeXVentas;
    @FXML
    private NumberAxis ejeYVentas;
    @FXML
    private BarChart<String, Number> barChartProductos;
    @FXML
    private CategoryAxis ejeXProductos;
    @FXML
    private NumberAxis ejeYProductos;
    

    private final HistorialVentasService historialVentasService = new HistorialVentasService();
    private SceneManager sceneManager;

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
        cargarVentasPorDia();
        cargarTopProductos();
    }

    private void cargarVentasPorDia() {
        List<VentaPorDia> ventas = historialVentasService.obtenerVentasPorDia(7);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ventas (Q)");

        for (VentaPorDia venta : ventas) {
            serie.getData().add(new XYChart.Data<>(venta.getFecha(), venta.getTotal()));
        }

        lineChartVentas.setData(FXCollections.observableArrayList(serie));
    }

    private void cargarTopProductos() {
        List<ProductoMasVendido> productos = historialVentasService.obtenerTopProductos(5);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Unidades vendidas");

        for (ProductoMasVendido producto : productos) {
            serie.getData().add(new XYChart.Data<>(producto.getNombreProducto(), producto.getCantidadVendida()));
        }

        barChartProductos.setData(FXCollections.observableArrayList(serie));
    }
}