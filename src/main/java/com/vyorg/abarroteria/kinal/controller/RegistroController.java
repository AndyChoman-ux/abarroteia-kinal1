package main.java.com.vyorg.abarroteria.kinal.controller;

import main.java.com.vyorg.abarroteria.kinal.service.AuthService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.com.vyorg.abarroteria.kinal.service.AuthService;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;
import java.util.logging.Logger;
import java.util.logging.Level;

public class RegistroController implements Initializable {
private final AuthService authService;
    private final SceneManager sceneManager;
    
    
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;

    public RegistroController(AuthService authService, SceneManager sceneManager) {
        this.authService = authService;
        this.sceneManager = sceneManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }    

  @FXML
private void handleRegistrar() {
    try {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
            sceneManager.showAlertInfo("Campos vacíos", "Por favor completa todos los campos",
                    "Inténtalo de nuevo", Alert.AlertType.WARNING);
            return;
        }

        boolean exito = authService.registrar(nombre, apellido, email, password);

        if (exito) {
            mostrarMensaje("¡Usuario registrado con éxito!", true);
            limpiarCampos();
        } else {
            mostrarMensaje("No se pudo completar el registro.", false);
        }
    } catch (Exception e) {
        // Log técnico completo para depuración (no se muestra al usuario)
        Logger.getLogger(RegistroController.class.getName())
              .log(Level.SEVERE, "Error al registrar usuario", e);
        mostrarMensaje("Ocurrió un error al registrar. Intenta de nuevo más tarde.", false);
    }
}

private void mostrarMensaje(String texto, boolean exito) {
    if (lblMensaje != null) {
        lblMensaje.setStyle(exito ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        lblMensaje.setText(texto);
    }
}

    @FXML
private void handleVolverLogin() {
    try {
        sceneManager.showLoginView();
    } catch (Exception e) {
        sceneManager.showAlertInfo("Error", "No se pudo volver al login", e.getMessage(), Alert.AlertType.ERROR);
    }
}
    
    
    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
        txtPassword.clear();
    }
}