package main.java.com.vyorg.abarroteria.kinal.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import main.java.com.vyorg.abarroteria.kinal.controller.DashboardAdminController;

public class SceneManager {

    private Stage stage;
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
    stage.sizeToScene();
    stage.centerOnScreen();
    stage.show();
}
    public void showDashboardAdminView() throws Exception {
    stage.getIcons().add(new Image(getClass().getResourceAsStream("/main/resources/img/login-logo.png")));
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/view/dashboard-admin.fxml"));
    AuthService authService = new AuthService(new AuthRepository());
    DashboardService dashboardService = new DashboardService(new ProductoRepository());
    loader.setControllerFactory(c -> new DashboardAdminController(authService, dashboardService, this));

    Parent root = loader.load();
    stage.setScene(new Scene(root));
    stage.setTitle("Dashboard Admin - Abarroteria Kinal");
    stage.sizeToScene();
    stage.centerOnScreen();
    stage.show();
}

    /** Mantiene compatibilidad con lugares donde no se necesita refrescar un contador (ej. dashboard admin). */
    public void showNotificacionesPopup(Node anchor) {
        showNotificacionesPopup(anchor, null);
    }

    public void showNotificacionesPopup(Node anchor, Runnable actualizarBadge) {
        NotificacionService notificacionService = new NotificacionService();

        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(15));
        contenedor.setPrefWidth(320);
        contenedor.setStyle("-fx-background-color: white; -fx-background-radius: 12; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 4);");

        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.getContent().add(contenedor);

        refrescarContenidoNotificaciones(popup, contenedor, notificacionService, actualizarBadge);

        if (actualizarBadge != null) {
            popup.setOnHidden(e -> actualizarBadge.run());
        }

        Bounds bounds = anchor.localToScreen(anchor.getBoundsInLocal());
        popup.show(anchor, bounds.getMinX() - 270, bounds.getMaxY() + 8);
    }

    private void refrescarContenidoNotificaciones(Popup popup, VBox contenedor,
                                                   NotificacionService notificacionService, Runnable actualizarBadge) {
        contenedor.getChildren().clear();

        List<Notificacion> notificaciones = notificacionService.listarNotificaciones();

        HBox encabezado = new HBox();
        encabezado.setAlignment(Pos.CENTER_LEFT);

        Label titulo = new Label("Notificaciones recientes");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        HBox.setHgrow(titulo, Priority.ALWAYS);

        Button btnEliminarTodas = new Button("Eliminar todas");
        btnEliminarTodas.setStyle("-fx-background-color: transparent; -fx-text-fill: #E53935; "
                + "-fx-font-size: 11px; -fx-underline: true; -fx-cursor: hand;");
        btnEliminarTodas.setDisable(notificaciones.isEmpty());
        btnEliminarTodas.setOnAction(e -> {
            popup.setAutoHide(false);
            boolean confirmado = showConfirmation(
                    "Eliminar notificaciones",
                    "Eliminar todas las notificaciones",
                    "Esta accion no se puede deshacer. ¿Desea eliminar todas las notificaciones?");
            popup.setAutoHide(true);

            if (confirmado) {
                notificacionService.eliminarTodas();
                refrescarContenidoNotificaciones(popup, contenedor, notificacionService, actualizarBadge);
                if (actualizarBadge != null) actualizarBadge.run();
            }
        });

        encabezado.getChildren().addAll(titulo, btnEliminarTodas);
        contenedor.getChildren().add(encabezado);

        if (notificaciones.isEmpty()) {
            Label vacio = new Label("No hay notificaciones");
            vacio.setStyle("-fx-text-fill: #757575;");
            contenedor.getChildren().add(vacio);
        } else {
            for (Notificacion n : notificaciones) {
                contenedor.getChildren().add(crearItemNotificacion(popup, n, notificacionService, contenedor, actualizarBadge));
            }
        }
    }

    private HBox crearItemNotificacion(Popup popup, Notificacion n, NotificacionService notificacionService,
                                        VBox contenedorPadre, Runnable actualizarBadge) {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.TOP_LEFT);
        fila.setPadding(new Insets(6));
        fila.setStyle(n.isLeida()
                ? "-fx-cursor: hand;"
                : "-fx-cursor: hand; -fx-background-color: #F1F8E9; -fx-background-radius: 8;");

        Label icono = new Label(iconoParaTitulo(n.getTitulo()));
        icono.setStyle("-fx-font-size: 18px;");

        VBox textos = new VBox(2);
        Label tituloLbl = new Label(n.getTitulo());
        tituloLbl.setStyle("-fx-font-weight: bold;");
        Label mensajeLbl = new Label(n.getMensaje());
        mensajeLbl.setWrapText(true);
        mensajeLbl.setMaxWidth(170);
        mensajeLbl.setStyle("-fx-text-fill: #616161; -fx-font-size: 12px;");
        Label tiempo = new Label(tiempoTranscurrido(n.getFecha()));
        tiempo.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 11px;");
        textos.getChildren().addAll(tituloLbl, mensajeLbl, tiempo);
        HBox.setHgrow(textos, Priority.ALWAYS);

        Button btnEliminar = new Button("🗑");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        btnEliminar.setOnMouseClicked(e -> e.consume());
        btnEliminar.setOnAction(e -> {
            notificacionService.eliminarNotificacion(n.getIdNotificacion());
            refrescarContenidoNotificaciones(popup, contenedorPadre, notificacionService, actualizarBadge);
            if (actualizarBadge != null) actualizarBadge.run();
        });

        fila.getChildren().addAll(icono, textos, btnEliminar);
        fila.setOnMouseClicked(e -> mostrarDetalleNotificacion(popup, n, notificacionService, contenedorPadre, actualizarBadge));

        return fila;
    }

   private void mostrarDetalleNotificacion(Popup popup, Notificacion n, NotificacionService notificacionService,
                                         VBox contenedorPadre, Runnable actualizarBadge) {
    // En vez de abrir una ventana (Dialog) aparte, se reemplaza el contenido
    // del mismo popup por la vista de detalle, manteniendo los mismos botones.
    contenedorPadre.getChildren().clear();

    HBox encabezado = new HBox(8);
    encabezado.setAlignment(Pos.CENTER_LEFT);

    Button btnVolver = new Button("←");
    btnVolver.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");
    btnVolver.setOnAction(e -> {
        notificacionService.marcarComoLeida(n.getIdNotificacion());
        refrescarContenidoNotificaciones(popup, contenedorPadre, notificacionService, actualizarBadge);
        if (actualizarBadge != null) actualizarBadge.run();
    });

    Label encabezadoTitulo = new Label("Detalle de notificacion");
    encabezadoTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    encabezado.getChildren().addAll(btnVolver, encabezadoTitulo);
    contenedorPadre.getChildren().add(encabezado);

    VBox contenido = new VBox(10);
    contenido.setPadding(new Insets(10, 0, 10, 0));

    Label titulo = new Label(n.getTitulo());
    titulo.setWrapText(true);
    titulo.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

    Label producto = new Label("Producto: " + (n.getProductoNombre() == null ? "N/A" : n.getProductoNombre()));
    producto.setWrapText(true);
    Label idProducto = new Label("ID producto: " + (n.getIdProducto() == null ? "N/A" : n.getIdProducto()));
    Label mensaje = new Label(n.getMensaje());
    mensaje.setWrapText(true);
    mensaje.setMaxWidth(280);
    Label fecha = new Label("Fecha: " + (n.getFecha() == null ? "" : formatoFecha.format(n.getFecha())));

    contenido.getChildren().addAll(titulo, producto, idProducto, mensaje, fecha);
    contenedorPadre.getChildren().add(contenido);

    HBox botones = new HBox(10);
    botones.setAlignment(Pos.CENTER_RIGHT);

    Button btnCerrar = new Button("Cerrar");
    btnCerrar.setStyle("-fx-background-radius: 22; -fx-padding: 9 22; -fx-font-size: 13px; "
            + "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-color: #F5C89A; -fx-text-fill: #8D4E1F;");
    btnCerrar.setOnAction(e -> {
        notificacionService.marcarComoLeida(n.getIdNotificacion());
        refrescarContenidoNotificaciones(popup, contenedorPadre, notificacionService, actualizarBadge);
        if (actualizarBadge != null) actualizarBadge.run();
    });

    Button btnEliminar = new Button("Eliminar notificacion");
    btnEliminar.setStyle("-fx-background-radius: 22; -fx-padding: 9 22; -fx-font-size: 13px; "
            + "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-color: #C0522A; -fx-text-fill: white;");
    btnEliminar.setOnAction(e -> {
        notificacionService.eliminarNotificacion(n.getIdNotificacion());
        refrescarContenidoNotificaciones(popup, contenedorPadre, notificacionService, actualizarBadge);
        if (actualizarBadge != null) actualizarBadge.run();
    });

    botones.getChildren().addAll(btnCerrar, btnEliminar);
    contenedorPadre.getChildren().add(botones);
}

   public void showHistorialVentas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/resources/view/historial-ventas-view.fxml"));

            Parent root = loader.load();

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