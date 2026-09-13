package main.java.com.vyorg.abarroteria.kinal.repository;

import main.java.com.vyorg.abarroteria.kinal.config.DataBaseConnection;
import main.java.com.vyorg.abarroteria.kinal.model.DetalleVenta;
import main.java.com.vyorg.abarroteria.kinal.model.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import main.java.com.vyorg.abarroteria.kinal.model.VentaPorDia;
import main.java.com.vyorg.abarroteria.kinal.model.ProductoMasVendido;

public class VentaRepository {

    public List<Venta> obtenerTodasLasVentas() {
        List<Venta> listaVentas = new ArrayList<>();
        String sql = "SELECT id_venta, fecha, total, cliente, metodo_pago, estado, vendedor, ruta_factura "
                   + "FROM ventas ORDER BY fecha DESC";

        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Venta venta = new Venta(
                    rs.getInt("id_venta"),
                    rs.getTimestamp("fecha"),
                    rs.getDouble("total"),
                    rs.getString("cliente"),
                    rs.getString("metodo_pago"),
                    rs.getString("estado"),
                    rs.getString("vendedor"),
                    rs.getString("ruta_factura")
                );
                listaVentas.add(venta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al consultar el historial de ventas: " + e.getMessage());
        }

        return listaVentas;
    }

    public int insertarVenta(String cliente, double total, String metodoPago, String vendedor) {
        String sql = "INSERT INTO ventas (fecha, total, cliente, metodo_pago, estado, vendedor) "
                   + "VALUES (NOW(), ?, ?, ?, 'Pagada', ?)";

        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, total);
            stmt.setString(2, cliente);
            stmt.setString(3, metodoPago);
            stmt.setString(4, vendedor);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al registrar la venta: " + e.getMessage());
        }

        return -1;
    }

    public void insertarDetalleVenta(int idVenta, List<DetalleVenta> detalles) {
        String sql = "INSERT INTO detalle_venta (id_venta, codigo_producto, descripcion, cantidad, precio_unitario, subtotal) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (DetalleVenta detalle : detalles) {
                stmt.setInt(1, idVenta);
                stmt.setString(2, detalle.getCodigoProducto());
                stmt.setString(3, detalle.getDescripcion());
                stmt.setInt(4, detalle.getCantidad());
                stmt.setDouble(5, detalle.getPrecioUnitario());
                stmt.setDouble(6, detalle.getSubtotal());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al registrar el detalle de la venta: " + e.getMessage());
        }
    }

    public List<DetalleVenta> obtenerDetalleVenta(int idVenta) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT id_detalle, id_venta, codigo_producto, descripcion, cantidad, precio_unitario, subtotal "
                   + "FROM detalle_venta WHERE id_venta = ?";

        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new DetalleVenta(
                        rs.getInt("id_detalle"),
                        rs.getInt("id_venta"),
                        rs.getString("codigo_producto"),
                        rs.getString("descripcion"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_unitario"),
                        rs.getDouble("subtotal")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al consultar el detalle de la venta: " + e.getMessage());
        }

        return detalles;
    }

    public void actualizarRutaFactura(int idVenta, String rutaFactura) {
        String sql = "UPDATE ventas SET ruta_factura = ? WHERE id_venta = ?";

        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rutaFactura);
            stmt.setInt(2, idVenta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al actualizar la ruta de la factura: " + e.getMessage());
        }
    }
        public List<VentaPorDia> obtenerVentasPorDia(int dias) {
        List<VentaPorDia> lista = new ArrayList<>();
        String sql = "SELECT DATE(fecha) as dia, SUM(total) as total FROM ventas "
                   + "WHERE fecha >= (CURDATE() - INTERVAL ? DAY) "
                   + "GROUP BY DATE(fecha) ORDER BY dia";

        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dias);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new VentaPorDia(rs.getDate("dia").toString(), rs.getDouble("total")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al consultar ventas por dia: " + e.getMessage());
        }
        return lista;
    }

    public List<ProductoMasVendido> obtenerTopProductos(int limite) {
        List<ProductoMasVendido> lista = new ArrayList<>();
        String sql = "SELECT descripcion, SUM(cantidad) as total_cantidad FROM detalle_venta "
                   + "GROUP BY descripcion ORDER BY total_cantidad DESC LIMIT ?";

        try (Connection conn = DataBaseConnection.getDataBaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ProductoMasVendido(rs.getString("descripcion"), rs.getInt("total_cantidad")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al consultar productos mas vendidos: " + e.getMessage());
        }
        return lista;
    }
}