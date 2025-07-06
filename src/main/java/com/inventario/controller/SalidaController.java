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
        //int loteId = getLoteId(conn, loteCodigo);
        //Comento la linea para no utilizar el 1=1
        int sucursalId = getSucursalId(conn, sucursalNombre);
        
        //int stockDisponible = getStock(conn, loteId);
        //if (cantidad > stockDisponible) throw new Exception("Stock insuficiente para el lote seleccionado.");
        //Linea comentada para no hacer esa validacion
        

        String insertSalida = "INSERT INTO salidas (usuario_id, sucursal_id, total) VALUES (?, ?, 0)";
        PreparedStatement psSalida = conn.prepareStatement(insertSalida, Statement.RETURN_GENERATED_KEYS);
        psSalida.setInt(1, usuarioId);
        psSalida.setInt(2, sucursalId);
        psSalida.executeUpdate();
        ResultSet rsSalida = psSalida.getGeneratedKeys();
        rsSalida.next();
        int salidaId = rsSalida.getInt(1);

        //consulta cambiada, ya que no solo toma o iguala el 1=1 si no que toma todos los lotes y los ordena por vencimiento.
        String sql = "SELECT id, stock, codigo_lote FROM lotes WHERE producto_id = ? AND stock > 0 ORDER BY fecha_vencimiento ASC";
        PreparedStatement psLotes = conn.prepareStatement(sql);
        psLotes.setInt(1, productoId);
        ResultSet rs = psLotes.executeQuery();

        int restante = cantidad;
        BigDecimal precioUnitario = BigDecimal.valueOf(200);
        BigDecimal totalSalida = BigDecimal.ZERO;
        
        
        //Aqui esta el ciclo que le mencionaba para que siga tomando los id de los lotes proximos a vencer
        while (rs.next() && restante > 0) {
            int loteId = rs.getInt("id");
            int disponible = rs.getInt("stock");
            String codigo = rs.getString("codigo_lote");

            int usar = Math.min(disponible, restante);

            // Insertar detalle de salida
            PreparedStatement psDetalle = conn.prepareStatement(
                "INSERT INTO detalles_salida (salida_id, lote_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)");
            psDetalle.setInt(1, salidaId);
            psDetalle.setInt(2, loteId);
            psDetalle.setInt(3, usar);
            psDetalle.setBigDecimal(4, precioUnitario);
            psDetalle.executeUpdate();

            // Actualizar stock
            PreparedStatement psStock = conn.prepareStatement("UPDATE lotes SET stock = stock - ? WHERE id = ?");
            psStock.setInt(1, usar);
            psStock.setInt(2, loteId);
            psStock.executeUpdate();

            restante -= usar;
            totalSalida = totalSalida.add(precioUnitario.multiply(BigDecimal.valueOf(usar)));
        }

        if (restante > 0) {
            conn.rollback();
            return "Stock insuficiente en todos los lotes disponibles.";
        }

        PreparedStatement psTotal = conn.prepareStatement("UPDATE salidas SET total = ? WHERE id = ?");
        psTotal.setBigDecimal(1, totalSalida);
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
