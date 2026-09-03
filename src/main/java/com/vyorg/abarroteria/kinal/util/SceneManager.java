package main.java.com.vyorg.abarroteria.kinal.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.com.vyorg.abarroteria.kinal.controller.LoginController;
import main.java.com.vyorg.abarroteria.kinal.repository.AuthRepository;
import main.java.com.vyorg.abarroteria.kinal.service.AuthService;

public class SceneManager {
    private final Stage stage;
    
    //constructor
    public SceneManager(Stage stage){
        this.stage = stage;
    }
    
    public void showLoginView() throws Exception{
        FXMLLoader loader= new FXMLLoader(getClass().getResource("/main/resources/view/login-view.fxml"));
        
        loader.setControllerFactory(
        clazz->{
            if(clazz == LoginController.class){
                AuthRepository authRepository = new AuthRepository();
            AuthService authService = new AuthService(authRepository);
            return new LoginController(authService,this);
        }
            try{
                return clazz.getDeclaredConstructor().newInstance();
                
            }catch(Exception e){
                throw new RuntimeException("error al crear el constructor" + e.getMessage());
            }
        }
        );
        Parent root = loader.load();
        Scene scene = new Scene(root,600,600);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
