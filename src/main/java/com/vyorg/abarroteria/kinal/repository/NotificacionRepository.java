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
        String sql = "select id_notificacion, titulo, mensaje, fecha, leida from notificaciones order by fecha desc";

        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Notificacion n = new Notificacion(
                    rs.getInt("id_notificacion"),
                    rs.getString("titulo"),
                    rs.getString("mensaje"),
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

    public void insertar(String titulo, String mensaje) {
        String sql = "insert into notificaciones (titulo, mensaje, fecha, leida) values (?, ?, now(), false)";
        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            stmt.setString(2, mensaje);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}