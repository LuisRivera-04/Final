/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventario.controller;
import com.inventario.database.ConexionBD;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class MovimientoController {

    public void cargarSucursales(javax.swing.JComboBox<String> comboBox) {
        try (Connection conn = ConexionBD.conectar()) {
            String sql = "SELECT nombre FROM sucursales";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            comboBox.removeAllItems();
            while (rs.next()) {
                comboBox.addItem(rs.getString("nombre"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buscarMovimientos(DefaultTableModel modelo, String desde, String hasta, String sucursal) {
    modelo.setRowCount(0);
    try (Connection conn = ConexionBD.conectar()) {
        String sql =
            "SELECT s.id, s.fecha, SUM(d.cantidad) AS unidades, s.total, " +
            "r.id AS recepcion_id, r.fecha AS fecha_recepcion, " +
            "u.nombre AS recibido_por, u2.nombre AS enviado_por " +
            "FROM salidas s " +
            "JOIN detalles_salida d ON s.id = d.salida_id " +
            "JOIN sucursales su ON s.sucursal_id = su.id " +
            "JOIN usuarios u2 ON s.usuario_id = u2.id " +  // Quien hizo envio
            "LEFT JOIN recepciones r ON r.salida_id = s.id " +
            "LEFT JOIN usuarios u ON u.id = r.usuario_id " + // Quien recibe enviado
            "WHERE s.fecha BETWEEN ? AND ? AND su.nombre = ? " +
            "GROUP BY s.id, s.fecha, s.total, r.id, r.fecha, u.nombre, u2.nombre " +
            "ORDER BY s.fecha DESC";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(desde));
        ps.setDate(2, Date.valueOf(hasta));
        ps.setString(3, sucursal);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String fecha = rs.getString("fecha");
            int unidades = rs.getInt("unidades");
            double total = rs.getDouble("total");
            boolean recibido = rs.getInt("recepcion_id") != 0;
            String estado = recibido ? "Recibo en Sucursal" : "Enviado a Sucursal";
            String recibidoPor = rs.getString("recibido_por");
            String fechaRecepcion = rs.getString("fecha_recepcion");
            String enviadoPor = rs.getString("enviado_por");

            modelo.addRow(new Object[]{
                id,
                fecha,
                unidades,
                total,
                estado,
                recibidoPor != null ? recibidoPor : "",
                fechaRecepcion != null ? fechaRecepcion : "",
                enviadoPor != null ? enviadoPor : ""
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public boolean registrarRecepcion(int salidaId, int usuarioId) {
        try (Connection conn = ConexionBD.conectar()) {
            String sql = "INSERT INTO recepciones (salida_id, usuario_id, fecha) VALUES (?, ?, NOW())";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, salidaId);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

