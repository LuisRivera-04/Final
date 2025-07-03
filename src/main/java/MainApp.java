/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.inventario.view.LoginView;
import javax.swing.SwingUtilities;
/**
 *
 * @author Luis Rivera
 */
public class MainApp {
     public static void main(String[] args) {
        // Ejecutar en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}
