package main.java.com.vyorg.abarroteria.kinal.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.com.vyorg.abarroteria.kinal.model.Producto;
import main.java.com.vyorg.abarroteria.kinal.service.DashboardService;
import main.java.com.vyorg.abarroteria.kinal.util.SceneManager;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;


public class DashboardController implements Initializable {

    private DashboardService dashboardService;
    private SceneManager sceneManager;
    @FXML
    private TableView<Producto> tableProducto;
    @FXML
    private TableColumn<Producto,String> tableColumnIdProducto;
    @FXML
    private TableColumn<Producto,String>tableColumnNombre;
    @FXML
    private TableColumn<Producto,Integer> tableColumnStock;
    @FXML
    private TableColumn<Producto,BigDecimal> tableColumnPrecio;
    @FXML
private Button btnEliminarProducto;
    @FXML
private Button btnCerrarSesion;

private ObservableList<Producto> listaProductos;
    
    public DashboardController (DashboardService dashboardService,SceneManager sceneManager){
        this.dashboardService=dashboardService;
        this.sceneManager = sceneManager;
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handleLoadDataTableView();
    }    
    
    private void handleLoadDataTableView(){
        tableColumnIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
    tableColumnNombre.setCellValueFactory(new PropertyValueFactory("nombreProducto"));
    tableColumnStock.setCellValueFactory(new PropertyValueFactory("stock"));
    tableColumnPrecio.setCellValueFactory(new PropertyValueFactory("precio"));
    listaProductos = dashboardService.findProducto();      // <-- NUEVO
    tableProducto.setItems(listaProductos);   
    
    }
    
    @FXML
private void handleEliminarProducto(){
    Producto productoSeleccionado = tableProducto.getSelectionModel().getSelectedItem();

    if(productoSeleccionado == null){
        sceneManager.showAlertInfo("Ningun producto seleccionado", "Seleccione un producto",
                "Debe seleccionar un producto de la tabla para poder eliminarlo", Alert.AlertType.WARNING);
        return;
    }

    boolean confirmado = sceneManager.showConfirmation(
            "Eliminar " + productoSeleccionado.getNombreProducto(),
            "Confirmar eliminacion",
            "Esta accion no se puede deshacer. ¿Desea eliminar este producto?");

    if(!confirmado){
        return;
    }

    try{
        dashboardService.eliminarProducto(productoSeleccionado.getIdProducto());
        listaProductos.remove(productoSeleccionado);
        sceneManager.showAlertInfo("Producto eliminado", "Listo",
                "El producto se elimino correctamente", Alert.AlertType.INFORMATION);
    }catch(RuntimeException e){
        sceneManager.showAlertInfo("Error al eliminar", "No se pudo eliminar el producto",
                e.getMessage(), Alert.AlertType.ERROR);
    }
}

@FXML
private void handleCerrarSesion(){
    boolean confirmado = sceneManager.showConfirmation(
            "Cerrar sesion",
            "Confirmar cierre de sesion",
            "¿Desea cerrar sesion y volver a la pantalla de inicio?");

    if(!confirmado){
        return;
    }

    try{
        sceneManager.showLoginView();
    }catch(Exception e){
        sceneManager.showAlertInfo("Error al cerrar sesion", "No se pudo cerrar sesion",
                e.getMessage(), Alert.AlertType.ERROR);
    }
}

}
