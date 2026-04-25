package org.example.model;

import java.time.LocalDateTime;

public class Maleta {
    private Integer id;
    private String nombre;
    private Double peso;
    private Double volumen;
    private LocalDateTime fechaCreacion;
    private Pedido pedido;
    private EstadoMaleta estado;
    /**
     * ID de la instancia de vuelo asignada a este producto.
     * Formato: "FL-{vueloId}-DAY-{day}-{HHmm}"
     * Ejemplo: "FL-45-DAY-0-0800"
     *
     * Permite tracking específico de qué salida diaria transporta este producto.
     */
    private String instanciaVueloAsignada;

    /**
     * Tiempo de llegada estimado o real del producto al destino final.
     * Usado para calcular tiempos de entrega y actualizar estados.
     */
    private LocalDateTime fechaHoraLlegada;
    private int tipoData;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getVolumen() {
        return volumen;
    }

    public void setVolumen(Double volumen) {
        this.volumen = volumen;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getInstanciaVueloAsignada() {
        return instanciaVueloAsignada;
    }

    public void setInstanciaVueloAsignada(String instanciaVueloAsignada) {
        this.instanciaVueloAsignada = instanciaVueloAsignada;
    }

    public LocalDateTime getFechaHoraLlegada() {
        return fechaHoraLlegada;
    }

    public void setFechaHoraLlegada(LocalDateTime fechaHoraLlegada) {
        this.fechaHoraLlegada = fechaHoraLlegada;
    }

    public int getTipoData() {
        return tipoData;
    }

    public void setTipoData(int tipoData) {
        this.tipoData = tipoData;
    }
    public EstadoMaleta getEstado() {
        return estado;
    }

    public void setEstado(EstadoMaleta estado) {
        this.estado = estado;
    }
}
