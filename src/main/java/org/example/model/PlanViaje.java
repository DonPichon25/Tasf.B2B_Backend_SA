package org.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlanViaje {
    private Integer id;
    private LocalDateTime fechaPlanificacion;
    private String estado; // "PENDIENTE", "EN_PROGRESO", "COMPLETADO", "CANCELADO"
    private String algoritmoUsado; //
    private String versionDatos; // Para tracking de qué dataset se usó
    private Double costoTotal;
    private Double tiempoTotalHoras;
    private Integer numeroVuelos;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Pedido pedido;
    private List<SegmentoVuelo> segmentosVuelo = new ArrayList<>();

    private double energiaCalculada;
    private double duracionTotalHoras;

    public double getEnergiaCalculada() {
        return energiaCalculada;
    }

    public void setEnergiaCalculada(double energiaCalculada) {
        this.energiaCalculada = energiaCalculada;
    }

    public double getDuracionTotalHoras() {
        return duracionTotalHoras;
    }

    public void setDuracionTotalHoras(double duracionTotalHoras) {
        this.duracionTotalHoras = duracionTotalHoras;
    }
    public void agregarSegmento(SegmentoVuelo segmento) {
        segmentosVuelo.add(segmento);
        segmento.setPlanViaje(this);
    }

    public void removerSegmento(SegmentoVuelo segmento) {
        segmentosVuelo.remove(segmento);
        segmento.setPlanViaje(null);
    }


    public List<SegmentoVuelo> getSegmentosVuelo() {
        return segmentosVuelo;
    }

    public void setSegmentosVuelo(List<SegmentoVuelo> segmentosVuelo) {
        this.segmentosVuelo = segmentosVuelo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFechaPlanificacion() {
        return fechaPlanificacion;
    }

    public void setFechaPlanificacion(LocalDateTime fechaPlanificacion) {
        this.fechaPlanificacion = fechaPlanificacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAlgoritmoUsado() {
        return algoritmoUsado;
    }

    public void setAlgoritmoUsado(String algoritmoUsado) {
        this.algoritmoUsado = algoritmoUsado;
    }

    public String getVersionDatos() {
        return versionDatos;
    }

    public void setVersionDatos(String versionDatos) {
        this.versionDatos = versionDatos;
    }

    public Double getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(Double costoTotal) {
        this.costoTotal = costoTotal;
    }

    public Integer getNumeroVuelos() {
        return numeroVuelos;
    }

    public void setNumeroVuelos(Integer numeroVuelos) {
        this.numeroVuelos = numeroVuelos;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getTiempoTotalHoras() {
        return tiempoTotalHoras;
    }

    public void setTiempoTotalHoras(Double tiempoTotalHoras) {
        this.tiempoTotalHoras = tiempoTotalHoras;
    }

}
