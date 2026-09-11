package main.java.com.vyorg.abarroteria.kinal.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import main.java.com.vyorg.abarroteria.kinal.controller.LoginController;
import main.java.com.vyorg.abarroteria.kinal.controller.DashboardController;
import main.java.com.vyorg.abarroteria.kinal.controller.HistorialVentasController;
import main.java.com.vyorg.abarroteria.kinal.controller.RegistroController;
import main.java.com.vyorg.abarroteria.kinal.model.Notificacion;
import main.java.com.vyorg.abarroteria.kinal.repository.AuthRepository;
import main.java.com.vyorg.abarroteria.kinal.repository.ProductoRepository;
import main.java.com.vyorg.abarroteria.kinal.service.AuthService;
import main.java.com.vyorg.abarroteria.kinal.service.DashboardService;
import main.java.com.vyorg.abarroteria.kinal.service.NotificacionService;
import javafx.scene.control.DialogPane;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

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

    public void showRegistroView() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/view/registro-view.fxml"));
        AuthService authService = new AuthService(new AuthRepository());
        loader.setControllerFactory(c -> new RegistroController(authService, this));

        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Registro de Usuario - Abarroteria Kinal");
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

    public void showNotificacionesPopup(Node anchor) {
        List<Notificacion> notificaciones = new NotificacionService().listarNotificaciones();

        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(15));
        contenedor.setPrefWidth(300);
        contenedor.setStyle("-fx-background-color: white; -fx-background-radius: 12; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 4);");

        Label titulo = new Label("Notificaciones recientes");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        contenedor.getChildren().add(titulo);

        if (notificaciones.isEmpty()) {
            Label vacio = new Label("No hay notificaciones");
            vacio.setStyle("-fx-text-fill: #757575;");
            contenedor.getChildren().add(vacio);
        } else {
            for (Notificacion n : notificaciones) {
                contenedor.getChildren().add(crearItemNotificacion(n));
            }
        }

        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.getContent().add(contenedor);

        Bounds bounds = anchor.localToScreen(anchor.getBoundsInLocal());
        popup.show(anchor, bounds.getMinX() - 250, bounds.getMaxY() + 8);
    }

   public void showHistorialVentas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/view/historial-ventas-view.fxml"));
            
            // 1. Cargar el FXML primero para instanciar la vista y el controlador
            Parent root = loader.load();

            // 2. Obtener el controlador YA inicializado
            HistorialVentasController controller = loader.getController();
            if (controller != null) {
                controller.setSceneManager(this);
            }

            stage.setScene(new Scene(root));
            stage.setTitle("Historial de Ventas - Abarrotería Kinal");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox crearItemNotificacion(Notificacion n) {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.TOP_LEFT);

        Label icono = new Label(iconoParaTitulo(n.getTitulo()));
        icono.setStyle("-fx-font-size: 18px;");

        VBox textos = new VBox(2);
        Label tituloLbl = new Label(n.getTitulo());
        tituloLbl.setStyle("-fx-font-weight: bold;");
        Label mensajeLbl = new Label(n.getMensaje());
        mensajeLbl.setWrapText(true);
        mensajeLbl.setMaxWidth(200);
        mensajeLbl.setStyle("-fx-text-fill: #616161; -fx-font-size: 12px;");
        textos.getChildren().addAll(tituloLbl, mensajeLbl);
        HBox.setHgrow(textos, Priority.ALWAYS);

        Label tiempo = new Label(tiempoTranscurrido(n.getFecha()));
        tiempo.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 11px;");

        fila.getChildren().addAll(icono, textos, tiempo);
        return fila;
    }

    private String iconoParaTitulo(String titulo) {
        if (titulo == null) return "🔔";
        String t = titulo.toLowerCase();
        if (t.contains("stock")) return "⚠️";
        if (t.contains("venta")) return "✅";
        if (t.contains("inventario")) return "🔄";
        return "🔔";
    }

    private String tiempoTranscurrido(Timestamp fecha) {
        if (fecha == null) return "";
        long minutos = (System.currentTimeMillis() - fecha.getTime()) / 60000;
        if (minutos < 1) return "ahora";
        if (minutos < 60) return "hace " + minutos + "m";
        long horas = minutos / 60;
        if (horas < 24) return "hace " + horas + "h";
        long dias = horas / 24;
        return "hace " + dias + "d";
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