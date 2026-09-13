package main.java.com.vyorg.abarroteria.kinal;

import javafx.application.Application;
import javafx.stage.Stage;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;

public class MainApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.showSplashScreen();
    }

    public static void main(String[] args) {
        launch();
    }

}