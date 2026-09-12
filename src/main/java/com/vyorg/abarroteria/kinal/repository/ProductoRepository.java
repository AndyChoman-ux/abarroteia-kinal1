package main.java.com.vyorg.abarroteria.kinal.repository;

import javafx.collections.ObservableList;
import main.java.com.vyorg.abarroteria.kinal.config.DataBaseConnection;
import main.java.com.vyorg.abarroteria.kinal.model.Producto;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Connection;



public class ProductoRepository {

    public ObservableList<Producto> findAll() {
        String sql = "select * from productos";
        try (PreparedStatement pstm = DataBaseConnection.getDataBaseConnection().prepareStatement(sql)) {
            ResultSet rs = pstm.executeQuery();
            ObservableList<Producto> lista = FXCollections.observableArrayList();
            while (rs.next()) {
                lista.add(new Producto(
                        rs.getString("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getInt("stock"),
                        rs.getBigDecimal("precio")
                ));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en la consulta");
        }
    }

    public boolean eliminarProducto(String idProducto) {
        String sql = "delete from productos where id_producto = ?";
        try (PreparedStatement pstm = DataBaseConnection.getDataBaseConnection().prepareStatement(sql)) {
            pstm.setString(1, idProducto);
            int filasAfectadas = pstm.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("No se puede eliminar este producto");
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el producto: " + e.getMessage());
        }
    }

    public boolean descontarStock(String idProducto, int cantidad) {
        String sql = "update productos set stock = stock - ? where id_producto = ? and stock >= ?";
        try (PreparedStatement pstm = DataBaseConnection.getDataBaseConnection().prepareStatement(sql)) {
            pstm.setInt(1, cantidad);
            pstm.setString(2, idProducto);
            pstm.setInt(3, cantidad);
            int filasAfectadas = pstm.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el stock: " + e.getMessage());
        }
    }
public boolean guardarProducto(Producto producto) {
        String sql = "INSERT INTO productos (id_producto, nombre_producto, stock, precio) VALUES (?, ?, ?, ?)";
        try (Connection connection = DataBaseConnection.getDataBaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setString(1, producto.getIdProducto());
            preparedStatement.setString(2, producto.getNombreProducto());
            preparedStatement.setInt(3, producto.getStock());
            preparedStatement.setBigDecimal(4, producto.getPrecio());
            
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE productos SET nombre_producto = ?, stock = ?, precio = ? WHERE id_producto = ?";
        try (Connection connection = DataBaseConnection.getDataBaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setString(1, producto.getNombreProducto());
            preparedStatement.setInt(2, producto.getStock());
            preparedStatement.setBigDecimal(3, producto.getPrecio());
            preparedStatement.setString(4, producto.getIdProducto());
            
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}