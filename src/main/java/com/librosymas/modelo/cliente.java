package com.librosymas.modelo;

public class cliente {
    private String documento;
    private String nombre;
    private String telefono;
    private String direccion;
    private boolean tieneLibroPrestado;


    public void Cliente(String documento, String nombre, String telefono, String direccion, boolean tieneLibroPrestado) {
        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.tieneLibroPrestado = tieneLibroPrestado;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isTieneLibroPrestado() {
        return tieneLibroPrestado;
    }

    public void setTieneLibroPrestado(boolean tieneLibroPrestado) {
        this.tieneLibroPrestado = tieneLibroPrestado;
    }
    public void marcarLibroPrestado(){
        this.tieneLibroPrestado = true;
    }
}
