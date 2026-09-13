package main.java.com.vyorg.abarroteria.kinal.util;

import java.io.File;
import java.math.RoundingMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.java.com.vyorg.abarroteria.kinal.model.Producto;

public class ProductoCardFactory {

    private static final String IMAGEN_POR_DEFECTO = "/main/resources/img/login-logo.png";
    private static final int UMBRAL_STOCK_BAJO = 10;

    private ProductoCardFactory() {
    }

    public static VBox crear(Producto producto) {
        ImageView imageView = new ImageView(cargarImagen(producto.getRutaImagen()));
        imageView.setFitWidth(56.0);
        imageView.setFitHeight(56.0);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        StackPane marcoImagen = new StackPane(imageView);
        marcoImagen.getStyleClass().add("producto-imagen-marco");
        marcoImagen.setPrefSize(64.0, 64.0);
        marcoImagen.setMinSize(64.0, 64.0);
        marcoImagen.setMaxSize(64.0, 64.0);

        Label lblId = new Label(producto.getIdProducto());
        lblId.getStyleClass().add("producto-id");

        Label lblCategoria = new Label(producto.getCategoria());
        lblCategoria.getStyleClass().add("producto-categoria-badge");

        HBox filaSuperior = new HBox(6.0, lblId, lblCategoria);
        filaSuperior.setAlignment(Pos.CENTER_LEFT);

        Label lblNombre = new Label(producto.getNombreProducto());
        lblNombre.getStyleClass().add("producto-nombre");
        lblNombre.setWrapText(true);
        lblNombre.setMaxWidth(150.0);

        Label lblPrecio = new Label("Precio  Q" + producto.getPrecio().setScale(2, RoundingMode.HALF_UP));
        lblPrecio.getStyleClass().add("producto-detalle");

        boolean stockBajo = producto.getStock() > 0 && producto.getStock() <= UMBRAL_STOCK_BAJO;
        boolean sinStock = producto.getStock() <= 0;

        Label lblStock = new Label("Stock  " + producto.getStock());
        lblStock.getStyleClass().add("producto-detalle");

        VBox textos = new VBox(2.0, filaSuperior, lblNombre, lblPrecio, lblStock);

        if (stockBajo || sinStock) {
            Label etiqueta = new Label(sinStock ? "Agotado" : "¡Últimas unidades!");
            etiqueta.getStyleClass().add("etiqueta-stock-bajo");
            textos.getChildren().add(etiqueta);
        }

        HBox contenido = new HBox(10.0, marcoImagen, textos);
        contenido.setAlignment(Pos.CENTER_LEFT);

        VBox tarjeta = new VBox(contenido);
        tarjeta.getStyleClass().add("producto-card");
        if (stockBajo || sinStock) {
            tarjeta.getStyleClass().add("producto-card-stock-bajo");
        }
        tarjeta.setPadding(new Insets(10.0));
        tarjeta.setPrefWidth(215.0);
        tarjeta.setUserData(producto);

        return tarjeta;
    }

    public static Image cargarImagen(String rutaImagen) {
        if (rutaImagen != null && !rutaImagen.isBlank()) {
            File archivo = new File(rutaImagen);
            if (archivo.exists()) {
                return new Image(archivo.toURI().toString(), 64, 64, true, true);
            }
        }
        return new Image(ProductoCardFactory.class.getResourceAsStream(IMAGEN_POR_DEFECTO), 64, 64, true, true);
    }
}