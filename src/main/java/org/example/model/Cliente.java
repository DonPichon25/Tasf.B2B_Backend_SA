package org.example.model;

public class Cliente {
    private long id;
    private String nombres;
    private String apellidos;
    private String numeroDocumento;
    private String correo;
    private String telefono; // Guardar en formato internacional E.164, ej: +51987654321
    private Ciudad ciudadRecojo;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Ciudad getCiudadRecojo() {
        return ciudadRecojo;
    }

    public void setCiudadRecojo(Ciudad ciudadRecojo) {
        this.ciudadRecojo = ciudadRecojo;
    }

}
