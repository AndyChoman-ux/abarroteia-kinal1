package main.java.com.vyorg.abarroteria.kinal.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.com.vyorg.abarroteria.kinal.dto.request.LoginDTORequest;
import main.java.com.vyorg.abarroteria.kinal.dto.response.LoginDTOResponse;
import main.java.com.vyorg.abarroteria.kinal.service.AuthService;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;
import main.java.com.vyorg.abarroteria.kinal.util.SesionUsuario;

public class LoginController implements Initializable {
// atributos
   private final AuthService authService;
   private final SceneManager sceneManager;
   @FXML
   private Button btnIniciar;
   @FXML
   private Button btnRegistro;
   @FXML
    private TextField txtFieldEmail;
    @FXML
    private PasswordField txtFieldPassword;

   // constructor
   public LoginController(AuthService authService, SceneManager sceneManager){
    this.authService = authService;
    this.sceneManager = sceneManager;
   }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void handleLogin() throws Exception{
    if(txtFieldEmail.getText().isEmpty() || txtFieldPassword.getText().isEmpty() ){
        sceneManager.showAlertInfo("Hay campos sin llenar", "No puedes dejar espacios en blanco","Intente de nuevo", Alert.AlertType.INFORMATION);
    }else{
        try{
            LoginDTOResponse response = authService.login(new LoginDTORequest(txtFieldEmail.getText(),txtFieldPassword.getText()));
            SesionUsuario.iniciarSesion(response.getNombre());
            sceneManager.showAlertInfo("Bienvenido: " + response.getNombre(), "Es bueno verte:", "Inicio de sesion correcto", Alert.AlertType.INFORMATION);
             sceneManager.showDashboardView();
        }catch(RuntimeException e){
    sceneManager.showAlertInfo("Error al iniciar sesion", "Verifique los campos", e.getMessage(), Alert.AlertType.WARNING);
}
    }
}
    @FXML
private void handleIrRegistro() {
    try {
        sceneManager.showRegistroView();
    } catch (Exception e) {
        sceneManager.showAlertInfo("Error", "No se pudo abrir la ventana de registro", e.getMessage(), Alert.AlertType.ERROR);
    }
}


}