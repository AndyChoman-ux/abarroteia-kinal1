package main.java.com.vyorg.abarroteria.kinal.repository;

import main.java.com.vyorg.abarroteria.kinal.config.DataBaseConnection;
import main.java.com.vyorg.abarroteria.kinal.model.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VentaRepository {

    public List<Venta> obtenerTodasLasVentas() {
        List<Venta> listaVentas = new ArrayList<>();
        String sql = "SELECT id_venta, fecha, total, cliente FROM ventas ORDER BY fecha DESC";

        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Venta venta = new Venta(
                    rs.getInt("id_venta"),
                    rs.getTimestamp("fecha"),
                    rs.getDouble("total"),
                    rs.getString("cliente")
                );
                listaVentas.add(venta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al consultar el historial de ventas: " + e.getMessage());
        }

        return listaVentas;
    }
}
    

