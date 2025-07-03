
package com.inventario.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String DATABASE = "defaultdb";
    private static final String HOST = "mysql-2c009a5c-uth-23a1.d.aivencloud.com";
    private static final String PORT = "10194";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_J2-FWzXEmKhNybBO38F";
    private static final String SSL = "true";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
                                      "?useSSL=" + SSL + "&requireSSL=true&verifyServerCertificate=false";

    public static Connection conectar() {
System.out.println(URL);
    	try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            return conexion;
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos.");
            e.printStackTrace();
        }
        return null;
    }

}
