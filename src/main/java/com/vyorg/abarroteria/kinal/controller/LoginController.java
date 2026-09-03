package main.java.com.vyorg.abarroteria.kinal.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.com.vyorg.abarroteria.kinal.dto.request.LoginDTORequest;
import main.java.com.vyorg.abarroteria.kinal.dto.response.LoginDTOResponse;
import main.java.com.vyorg.abarroteria.kinal.service.AuthService;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;

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
    public void handleLogin(){
    if(txtFieldEmail.getText().isEmpty() || txtFieldPassword.getText().isEmpty() ){
        throw new RuntimeException("");
    }else{
       LoginDTOResponse response=  authService.login(new LoginDTORequest(txtFieldEmail.getText(), txtFieldPassword.getText()));
     System.out.println("nombre del usuario que inició sesión: " + response.getNombre() + " " + response.getIdRol());
    }
}
    
}
