/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventario.controller;
import com.inventario.database.ConexionBD;
import com.inventario.view.LoginView;
import java.math.BigDecimal;
import java.sql.*;

/**
 *
 * @author Luis Rivera
 */
public class SalidaController {
    public String registrarSalida(String productoNombre, String loteCodigo, int cantidad, String sucursalNombre, int usuarioId) {
        try (Connection conn = ConexionBD.conectar()) {
            conn.setAutoCommit(false);

            int productoId = getProductoId(conn, productoNombre);
            int loteId = getLoteId(conn, loteCodigo);
            int sucursalId = getSucursalId(conn, sucursalNombre);

            int stockDisponible = getStock(conn, loteId);
            if (cantidad > stockDisponible) throw new Exception("Stock insuficiente para el lote seleccionado.");

            String insertSalida = "INSERT INTO salidas (usuario_id, sucursal_id, total) VALUES (?, ?, 0)";
            PreparedStatement psSalida = conn.prepareStatement(insertSalida, Statement.RETURN_GENERATED_KEYS);
            psSalida.setInt(1, usuarioId);
            psSalida.setInt(2, sucursalId);
            psSalida.executeUpdate();
            ResultSet rsSalida = psSalida.getGeneratedKeys();
            rsSalida.next();
            int salidaId = rsSalida.getInt(1);

            BigDecimal precioUnitario = BigDecimal.valueOf(200);
            String insertDetalle = "INSERT INTO detalles_salida (salida_id, lote_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            PreparedStatement psDetalle = conn.prepareStatement(insertDetalle);
            psDetalle.setInt(1, salidaId);
            psDetalle.setInt(2, loteId);
            psDetalle.setInt(3, cantidad);
            psDetalle.setBigDecimal(4, precioUnitario);
            psDetalle.executeUpdate();

            String updateStock = "UPDATE lotes SET stock = stock - ? WHERE id = ?";
            PreparedStatement psStock = conn.prepareStatement(updateStock);
            psStock.setInt(1, cantidad);
            psStock.setInt(2, loteId);
            psStock.executeUpdate();

            BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            PreparedStatement psTotal = conn.prepareStatement("UPDATE salidas SET total = ? WHERE id = ?");
            psTotal.setBigDecimal(1, total);
            psTotal.setInt(2, salidaId);
            psTotal.executeUpdate();

            conn.commit();
            return "ok";

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private int getProductoId(Connection conn, String nombre) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM productos WHERE nombre = ?");
        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("id");
    }

    private int getLoteId(Connection conn, String codigoConTexto) throws SQLException {
        String codigoLimpio = codigoConTexto.split(" ")[0];
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM lotes WHERE codigo_lote = ?");
        ps.setString(1, codigoLimpio);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("id");
    }

    private int getSucursalId(Connection conn, String nombre) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM sucursales WHERE nombre = ?");
        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("id");
    }

    private int getStock(Connection conn, int loteId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT stock FROM lotes WHERE id = ?");
        ps.setInt(1, loteId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("stock");
    }
}
