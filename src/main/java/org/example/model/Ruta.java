package org.example.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class Ruta {
    private List<Vuelo> vuelos; // Lista de vuelos (ej: Vuelo1 -> Vuelo2)
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private double duracionTotalHoras;
    private boolean esOptima;
    private boolean replanificada;
    private LocalDateTime fechaReplanificacion;

    public Ruta() {
        this.vuelos = new ArrayList<>(); // Es buena práctica inicializar las listas
    }

    public void agregarVuelo(Vuelo vuelo) {
        this.vuelos.add(vuelo);
    }
    private int id;
    private String idPedido;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getDuracionTotalHoras() {
        return duracionTotalHoras;
    }

    public void setDuracionTotalHoras(double duracionTotalHoras) {
        this.duracionTotalHoras = duracionTotalHoras;
    }

    public boolean isEsOptima() {
        return esOptima;
    }

    public void setEsOptima(boolean esOptima) {
        this.esOptima = esOptima;
    }

    public boolean isReplanificada() {
        return replanificada;
    }

    public void setReplanificada(boolean replanificada) {
        this.replanificada = replanificada;
    }

    public LocalDateTime getFechaReplanificacion() {
        return fechaReplanificacion;
    }

    public void setFechaReplanificacion(LocalDateTime fechaReplanificacion) {
        this.fechaReplanificacion = fechaReplanificacion;
    }

    public List<Vuelo> getVuelos() {
        return vuelos;
    }
}
