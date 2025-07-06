/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.inventario.view;

import com.inventario.controller.LoginController;
import com.inventario.model.Usuario;
import com.inventario.view.MenuPrincipalView;
import javax.swing.*;


/**
 *
 * @author Luis Rivera
 */
public class LoginView extends javax.swing.JFrame {
private Usuario usuarioActual;
    public LoginView() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        lblUsuario = new javax.swing.JLabel();
        lblContrasena = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        txtContrasena = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        lblMensaje = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Inicio de Sesión");

        lblUsuario.setText("Usuario:");
        lblContrasena.setText("Contraseña:");
        btnLogin.setText("Ingresar");

        // Acción al hacer clic en el botón
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        // Layout con GroupLayout (editor visual)
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(30)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblMensaje)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lblUsuario)
                                .addComponent(lblContrasena))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtUsuario)
                                .addComponent(txtContrasena)
                                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap(30, Short.MAX_VALUE))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(40)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblUsuario)
                        .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblContrasena)
                        .addComponent(txtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18)
                    .addComponent(btnLogin)
                    .addGap(18)
                    .addComponent(lblMensaje)
                    .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        String usuario = txtUsuario.getText();
        String clave = new String(txtContrasena.getPassword());

        LoginController controller = new LoginController();
        Usuario u = controller.validar(usuario, clave);

        if (u != null) {
            u.getId();
            JOptionPane.showMessageDialog(this, "ID del usuario autenticado: " + u.getId());
            new MenuPrincipalView(u).setVisible(true);
            this.dispose();
        } else {
            lblMensaje.setText("Credenciales incorrectas.");
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new LoginView().setVisible(true));
    }

    
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel lblContrasena;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JTextField txtUsuario;
}
