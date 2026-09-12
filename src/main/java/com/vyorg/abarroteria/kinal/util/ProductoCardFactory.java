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

/**
 * Construye la tarjeta visual (imagen + datos) que se usa tanto
 * en el dashboard normal como en el dashboard admin.
 */
public class ProductoCardFactory {

    private static final String IMAGEN_POR_DEFECTO = "/main/resources/img/login-logo.png";

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

        Label lblNombre = new Label(producto.getNombreProducto());
        lblNombre.getStyleClass().add("producto-nombre");
        lblNombre.setWrapText(true);
        lblNombre.setMaxWidth(130.0);

        Label lblPrecio = new Label("Precio  Q" + producto.getPrecio().setScale(2, RoundingMode.HALF_UP));
        lblPrecio.getStyleClass().add("producto-detalle");

        Label lblStock = new Label("Stock  " + producto.getStock());
        lblStock.getStyleClass().add("producto-detalle");

        VBox textos = new VBox(2.0, lblId, lblNombre, lblPrecio, lblStock);

        HBox contenido = new HBox(10.0, marcoImagen, textos);
        contenido.setAlignment(Pos.CENTER_LEFT);

        VBox tarjeta = new VBox(contenido);
        tarjeta.getStyleClass().add("producto-card");
        tarjeta.setPadding(new Insets(10.0));
        tarjeta.setPrefWidth(230.0);
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