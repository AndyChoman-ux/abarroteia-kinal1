package main.java.com.vyorg.abarroteria.kinal.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.UUID;
import main.java.com.vyorg.abarroteria.kinal.config.DataBaseConnection;
import main.java.com.vyorg.abarroteria.kinal.dto.request.LoginDTORequest;
import main.java.com.vyorg.abarroteria.kinal.dto.response.LoginDTOResponse;
import main.java.com.vyorg.abarroteria.kinal.model.Usuario;

public class AuthRepository {

    public LoginDTOResponse findUserByEmail(LoginDTORequest loginDTORequest) {
    String sql = "select u.nombre, u.apellido, u.contrasena_hash, u.id_rol, r.nombre_rol from usuarios as u\n" +
         "inner join roles as r\n" +
         "on u.id_rol = r.id_rol\n" +
         "where u.email = ? ";

    try (PreparedStatement pstm = DataBaseConnection.getDataBaseConnection().prepareStatement(sql)) {
        String emailLimpio = loginDTORequest.getEmail() != null ? loginDTORequest.getEmail().trim() : "";
        pstm.setString(1, emailLimpio);

        ResultSet rs = pstm.executeQuery();
        if (rs.next()) {
            return new LoginDTOResponse(
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("contrasena_hash"),
                rs.getInt("id_rol"),
                rs.getString("nombre_rol")
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error de conexion con la base de datos: " + e.getMessage());
    }
    return null;
}
      
   public boolean registrarUsuario(Usuario usuario){
    String sql = "insert into usuarios (id_usuario, nombre, apellido, email, contrasena_hash, id_rol) values (?, ?, ?, ?, ?, ?)";
    String uuid = UUID.randomUUID().toString();

    try (Connection conn = DataBaseConnection.getDataBaseConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, uuid);
        stmt.setString(2, usuario.getNombre());
        stmt.setString(3, usuario.getApellido());
        stmt.setString(4, usuario.getEmail());
        stmt.setString(5, usuario.getContrasena_hash());
        stmt.setInt(6, usuario.getIdRol());

        int filasAfectadas = stmt.executeUpdate();
        return filasAfectadas > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
   public boolean actualizarRolPorEmail(String email, int idRol) {
    String sql = "update usuarios set id_rol = ? where email = ?";

    try (Connection conn = DataBaseConnection.getDataBaseConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idRol);
        stmt.setString(2, email);

        int filasAfectadas = stmt.executeUpdate();
        return filasAfectadas > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error de conexion con la base de datos: " + e.getMessage());
    }
}
}