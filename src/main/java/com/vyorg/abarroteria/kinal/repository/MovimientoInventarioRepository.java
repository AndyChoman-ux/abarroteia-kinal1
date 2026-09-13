package main.java.com.vyorg.abarroteria.kinal.repository;

import main.java.com.vyorg.abarroteria.kinal.config.DataBaseConnection;
import main.java.com.vyorg.abarroteria.kinal.model.MovimientoInventario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimientoInventarioRepository {

    public void insertarMovimiento(String idProducto, String nombreProducto, String tipoMovimiento, String detalle, String usuario) {
        String sql = "insert into movimientos_inventario (id_producto, nombre_producto, tipo_movimiento, detalle, usuario) values (?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = DataBaseConnection.getDataBaseConnection().prepareStatement(sql)) {
            pstm.setString(1, idProducto);
            pstm.setString(2, nombreProducto);
            pstm.setString(3, tipoMovimiento);
            pstm.setString(4, detalle);
            pstm.setString(5, usuario);
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar el movimiento de inventario: " + e.getMessage());
        }
    }

    public List<MovimientoInventario> obtenerTodos() {
        String sql = "select * from movimientos_inventario order by fecha desc";
        List<MovimientoInventario> lista = new ArrayList<>();
        try (PreparedStatement pstm = DataBaseConnection.getDataBaseConnection().prepareStatement(sql)) {
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                lista.add(new MovimientoInventario(
                        rs.getInt("id_movimiento"),
                        rs.getString("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getString("tipo_movimiento"),
                        rs.getString("detalle"),
                        rs.getString("usuario"),
                        rs.getTimestamp("fecha")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar los movimientos de inventario: " + e.getMessage());
        }
        return lista;
    }
}