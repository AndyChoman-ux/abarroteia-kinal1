package main.java.com.vyorg.abarroteria.kinal.repository;

import main.java.com.vyorg.abarroteria.kinal.config.DataBaseConnection;
import main.java.com.vyorg.abarroteria.kinal.model.Notificacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificacionRepository {

    public List<Notificacion> obtenerTodas() {
        List<Notificacion> lista = new ArrayList<>();
        String sql = "select id_notificacion, titulo, mensaje, id_producto, producto_nombre, fecha, leida "
                + "from notificaciones order by fecha desc";

        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Notificacion n = new Notificacion(
                    rs.getInt("id_notificacion"),
                    rs.getString("titulo"),
                    rs.getString("mensaje"),
                    rs.getString("id_producto"),
                    rs.getString("producto_nombre"),
                    rs.getTimestamp("fecha"),
                    rs.getBoolean("leida")
                );
                lista.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void insertar(String titulo, String mensaje, String idProducto, String productoNombre) {
        String sql = "insert into notificaciones (titulo, mensaje, id_producto, producto_nombre, fecha, leida) "
                + "values (?, ?, ?, ?, now(), false)";
        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            stmt.setString(2, mensaje);
            stmt.setString(3, idProducto);
            stmt.setString(4, productoNombre);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Evita insertar la misma notificacion de stock bajo cada vez que
     * se recarga el dashboard sin que el stock haya cambiado.
     */
    public boolean existeNotificacionConMensaje(String mensaje) {
        String sql = "select 1 from notificaciones where mensaje = ? limit 1";
        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mensaje);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void marcarComoLeida(int idNotificacion) {
        String sql = "update notificaciones set leida = true where id_notificacion = ?";
        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idNotificacion);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminar(int idNotificacion) {
        String sql = "delete from notificaciones where id_notificacion = ?";
        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idNotificacion);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarTodas() {
        String sql = "delete from notificaciones";
        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int contarNoLeidas() {
        String sql = "select count(*) from notificaciones where leida = false";
        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}