package main.java.com.vyorg.abarroteria.kinal.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.Optional;
import javafx.scene.image.Image;
import main.java.com.vyorg.abarroteria.kinal.controller.LoginController;
import main.java.com.vyorg.abarroteria.kinal.controller.DashboardController;
import main.java.com.vyorg.abarroteria.kinal.repository.AuthRepository;
import main.java.com.vyorg.abarroteria.kinal.repository.ProductoRepository;
import main.java.com.vyorg.abarroteria.kinal.service.AuthService;
import main.java.com.vyorg.abarroteria.kinal.service.DashboardService;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class SceneManager {

    private Stage stage;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public SceneManager() {
    }

    public void showLoginView() throws Exception {
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/img/login-logo.png")));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/view/login-view.fxml"));
        AuthService authService = new AuthService(new AuthRepository());
        loader.setControllerFactory(c -> new LoginController(authService, this));

        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Iniciar sesion - Abarroteria Kinal");
        stage.show();
    }

    public void showDashboardView() throws Exception {
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/img/login-logo.png")));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/view/dashboard-view.fxml"));
        DashboardService dashboardService = new DashboardService(new ProductoRepository());
        loader.setControllerFactory(c -> new DashboardController(dashboardService, this));

        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Dashboard - Abarroteria Kinal");
        stage.show();
    }
public void showAlertInfo(String title, String header, String content, AlertType alertType) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    estilizarDialogo(alert);
    alert.showAndWait();
}

public boolean showConfirmation(String title, String header, String content) {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    estilizarDialogo(alert);

    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == ButtonType.OK;
}
public void estilizarDialogo(Dialog<?> dialogo) {
    DialogPane panel = dialogo.getDialogPane();
    panel.getStylesheets().add(getClass().getResource("/main/resources/css/dialog-style.css").toExternalForm());
    panel.getStyleClass().add("dialog-pane");
    panel.getScene().setFill(Color.web("#BDBDBD"));

    ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/main/resources/img/login-logo.png")));
    logo.setFitWidth(60);
    logo.setFitHeight(60);
    logo.setPreserveRatio(true);
    dialogo.setGraphic(logo);

    Stage stageDialogo = (Stage) panel.getScene().getWindow();
    stageDialogo.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/img/login-logo.png")));
}
}