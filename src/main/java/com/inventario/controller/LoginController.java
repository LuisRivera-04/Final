
package com.inventario.controller;

import com.inventario.database.ConexionBD;
import com.inventario.model.Usuario;
import java.sql.*;

public class LoginController {
    
    public Usuario validar(String usuario, String clave) {
        try (Connection conn = ConexionBD.conectar()) {
            if (conn == null) return null;
            String sql = "SELECT u.*, r.descripcion AS rol FROM usuarios u JOIN roles r ON u.rol_id = r.id WHERE usuario = ? AND contrasena = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, clave);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setRol(rs.getString("rol"));
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
