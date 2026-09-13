package main.java.com.vyorg.abarroteria.kinal.repository;

import javafx.collections.ObservableList;
import main.java.com.vyorg.abarroteria.kinal.config.DataBaseConnection;
import main.java.com.vyorg.abarroteria.kinal.model.Producto;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import java.sql.SQLIntegrityConstraintViolationException;

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
                        rs.getBigDecimal("precio"),
                        rs.getString("ruta_imagen"),
                        rs.getString("categoria")
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

    public boolean agregarProducto(Producto producto) {
        String sql = "insert into productos (id_producto, nombre_producto, stock, precio, ruta_imagen, categoria) values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = DataBaseConnection.getDataBaseConnection().prepareStatement(sql)) {
            pstm.setString(1, producto.getIdProducto());
            pstm.setString(2, producto.getNombreProducto());
            pstm.setInt(3, producto.getStock());
            pstm.setBigDecimal(4, producto.getPrecio());
            pstm.setString(5, producto.getRutaImagen());
            pstm.setString(6, producto.getCategoria());
            int filasAfectadas = pstm.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Ya existe un producto con ese ID");
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar el producto: " + e.getMessage());
        }
    }

    public boolean actualizarProducto(Producto producto) {
        String sql = "update productos set nombre_producto = ?, stock = ?, precio = ?, ruta_imagen = ?, categoria = ? where id_producto = ?";
        try (PreparedStatement pstm = DataBaseConnection.getDataBaseConnection().prepareStatement(sql)) {
            pstm.setString(1, producto.getNombreProducto());
            pstm.setInt(2, producto.getStock());
            pstm.setBigDecimal(3, producto.getPrecio());
            pstm.setString(4, producto.getRutaImagen());
            pstm.setString(5, producto.getCategoria());
            pstm.setString(6, producto.getIdProducto());
            int filasAfectadas = pstm.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el producto: " + e.getMessage());
        }
    }
}