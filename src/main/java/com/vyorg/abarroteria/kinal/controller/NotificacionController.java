package main.java.com.vyorg.abarroteria.kinal.controller;

import  main.java.com.vyorg.abarroteria.kinal.model.Notificacion;
import  main.java.com.vyorg.abarroteria.kinal.service.NotificacionService;
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

public class NotificacionController implements Initializable {

    @FXML private TableView<Notificacion> tblNotificaciones;
    @FXML private TableColumn<Notificacion, Integer> colId;
    @FXML private TableColumn<Notificacion, String> colTitulo;
    @FXML private TableColumn<Notificacion, String> colMensaje;
    @FXML private TableColumn<Notificacion, Timestamp> colFecha;

    private final NotificacionService notificacionService = new NotificacionService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("idNotificacion"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        cargarDatos();
    }

    private void cargarDatos() {
        ObservableList<Notificacion> datos = FXCollections.observableArrayList(notificacionService.listarNotificaciones());
        tblNotificaciones.setItems(datos);
    }
}