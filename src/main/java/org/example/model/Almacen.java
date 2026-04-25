package org.example.model;

public class Almacen {
    private String nombre;
    private Integer capacidadMaxima;
    private Integer capacidadUsada = 0;
    private Boolean esAlmacenPrincipal = false;
    private Aeropuerto aeropuerto;
    private int tipoData;
    private Integer id;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }
    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }
    public Integer getCapacidadUsada() {
        return capacidadUsada;
    }
    public void setCapacidadUsada(Integer capacidadUsada) {
        this.capacidadUsada = capacidadUsada;
    }
    public Boolean getEsAlmacenPrincipal() {
        return esAlmacenPrincipal;
    }
    public void setEsAlmacenPrincipal(Boolean esAlmacenPrincipal) {
        this.esAlmacenPrincipal = esAlmacenPrincipal;
    }
    public int getTipoData() {
        return tipoData;
    }
    public void setTipoData(int tipoData) {
        this.tipoData = tipoData;
    }
    public Aeropuerto getAeropuerto() {
        return aeropuerto;
    }
    public void setAeropuerto(Aeropuerto aeropuerto) {
        this.aeropuerto = aeropuerto;
    }
}

