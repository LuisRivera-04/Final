
package com.inventario.model;

public class Usuario {
    private int id;
    private String nombre;
    private String rol;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
}
