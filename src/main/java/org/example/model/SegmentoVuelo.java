package org.example.model;

import java.time.LocalDateTime;

public class SegmentoVuelo {
    private Integer id;
    private Integer ordenSegmento; // Orden en la secuencia (1, 2, 3, etc.)
    private LocalDateTime horaSalidaEstimada;
    private LocalDateTime horaLlegadaEstimada;
    private Integer capacidadReservada;
    private String codigoOrigen; // IATA del aeropuerto origen
    private String codigoDestino; // IATA del aeropuerto destino
    private Double duracionHoras;
    private LocalDateTime createdAt;
    private PlanViaje planViaje;
    private Vuelo vuelo;
    private Pedido pedido;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrdenSegmento() {
        return ordenSegmento;
    }

    public void setOrdenSegmento(Integer ordenSegmento) {
        this.ordenSegmento = ordenSegmento;
    }

    public LocalDateTime getHoraLlegadaEstimada() {
        return horaLlegadaEstimada;
    }

    public void setHoraLlegadaEstimada(LocalDateTime horaLlegadaEstimada) {
        this.horaLlegadaEstimada = horaLlegadaEstimada;
    }

    public LocalDateTime getHoraSalidaEstimada() {
        return horaSalidaEstimada;
    }

    public void setHoraSalidaEstimada(LocalDateTime horaSalidaEstimada) {
        this.horaSalidaEstimada = horaSalidaEstimada;
    }

    public Integer getCapacidadReservada() {
        return capacidadReservada;
    }

    public void setCapacidadReservada(Integer capacidadReservada) {
        this.capacidadReservada = capacidadReservada;
    }

    public String getCodigoOrigen() {
        return codigoOrigen;
    }

    public void setCodigoOrigen(String codigoOrigen) {
        this.codigoOrigen = codigoOrigen;
    }

    public String getCodigoDestino() {
        return codigoDestino;
    }

    public void setCodigoDestino(String codigoDestino) {
        this.codigoDestino = codigoDestino;
    }

    public Double getDuracionHoras() {
        return duracionHoras;
    }

    public void setDuracionHoras(Double duracionHoras) {
        this.duracionHoras = duracionHoras;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PlanViaje getPlanViaje() {
        return planViaje;
    }

    public void setPlanViaje(PlanViaje planViaje) {
        this.planViaje = planViaje;
    }

    public Vuelo getVuelo() {
        return vuelo;
    }

    public void setVuelo(Vuelo vuelo) {
        this.vuelo = vuelo;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

}
