package com.inventario.view;

import com.inventario.controller.MovimientoController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class HistorialMovimientosView extends JFrame {

    private JTable tablaMovimientos;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cbSucursal;
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JButton btnBuscar;
    private JButton btnRecibir;
    private JLabel lblMensaje;

    private int idUsuario;
    private MovimientoController controller;

    public HistorialMovimientosView(int idUsuario) {
        this.idUsuario = idUsuario;
        this.controller = new MovimientoController();
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Historial de Movimientos");

        controller.cargarSucursales(cbSucursal);

        // Autocargar últimos 30 días si hay sucursales disponibles
        if (cbSucursal.getItemCount() > 0) {
            LocalDate hoy = LocalDate.now();
            LocalDate desde = hoy.minusDays(30);
            LocalDate hoysumado = hoy.plusDays(3);
            txtFechaDesde.setText(desde.toString());
            txtFechaHasta.setText(hoysumado.toString());
            buscarMovimientos();
        }
    }

    private void initComponents() {
        cbSucursal = new JComboBox<>();
        txtFechaDesde = new JTextField(10);
        txtFechaHasta = new JTextField(10);
        btnBuscar = new JButton("Buscar");
        btnRecibir = new JButton("Recibir Selección");
        lblMensaje = new JLabel(" ");

        modeloTabla = new DefaultTableModel(new String[]{
            "No. Salida", "Fecha", "Unidades", "Total", "Estado", "Recibido por", "Fecha Recepción", "Enviado por"
        }, 0);

        tablaMovimientos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaMovimientos);

        JPanel filtros = new JPanel(new FlowLayout());
        filtros.add(new JLabel("Desde (YYYY-MM-DD):"));
        filtros.add(txtFechaDesde);
        filtros.add(new JLabel("Hasta:"));
        filtros.add(txtFechaHasta);
        filtros.add(new JLabel("Sucursal:"));
        filtros.add(cbSucursal);
        filtros.add(btnBuscar);

        JPanel inferior = new JPanel(new FlowLayout());
        inferior.add(btnRecibir);
        inferior.add(lblMensaje);

        btnBuscar.addActionListener(e -> buscarMovimientos());
        btnRecibir.addActionListener(e -> recibirMovimiento());
        

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(filtros, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(inferior, BorderLayout.SOUTH);

        setSize(950, 520);
    }

    private void buscarMovimientos() {
        String desde = txtFechaDesde.getText().trim();
        String hasta = txtFechaHasta.getText().trim();
        String sucursal = (String) cbSucursal.getSelectedItem();

        if (desde.isEmpty() || hasta.isEmpty() || sucursal == null) {
            lblMensaje.setText("Complete los filtros.");
            return;
        }

        controller.buscarMovimientos(modeloTabla, desde, hasta, sucursal);
        lblMensaje.setText("Resultados actualizados.");
    }

    private void recibirMovimiento() {
        int fila = tablaMovimientos.getSelectedRow();
        if (fila == -1) {
            lblMensaje.setText("Seleccione una salida primero.");
            return;
        }

        String estado = tablaMovimientos.getValueAt(fila, 4).toString();
        if ("Recibo en Sucursal".equals(estado)) {
            lblMensaje.setText("Esta salida ya fue recibida.");
            return;
        }

        int salidaId = Integer.parseInt(tablaMovimientos.getValueAt(fila, 0).toString());
        boolean exito = controller.registrarRecepcion(salidaId, idUsuario);

        if (exito) {
            lblMensaje.setText("Recepción registrada correctamente.");
            buscarMovimientos();
        } else {
            lblMensaje.setText("Error al registrar recepción.");
        }
    }
    private void salida(){
    this.dispose();}
}