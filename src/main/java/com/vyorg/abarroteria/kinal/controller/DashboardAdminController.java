package main.java.com.vyorg.abarroteria.kinal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;

public class DashboardAdminController {

    private final SceneManager sceneManager = new SceneManager();

    @FXML
    private void handleVerNotificaciones(ActionEvent event) {
        try {
            sceneManager.showNotificacionesPopup((Node) event.getSource());
        } catch (Exception e) {
            sceneManager.showAlertInfo("Error", "No se pudo abrir notificaciones", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}