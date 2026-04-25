package org.example.model;
import java.time.LocalTime;

public class Vuelo {
    private Aeropuerto aeropuertoOrigen;
    private Aeropuerto aeropuertoDestino;
    private LocalTime horaSalida;
    private LocalTime horaLlegada;
    private double frecuenciaPorDia; //observar
    private int duracionVueloMinutos;
    private double tiempoTransporte;
    private double costo;
    private int capacidadMaxima;
    private int capacidadUsada;
    private boolean esIntercontinental;
    private EstadoVuelo estado;
    private double latitudActual;
    private double longitudActual;
    //private List<Ruta> rutas;
    public Vuelo() {}
    private int id;
    private String codigo;
    private int tipoData;

    public int getTipoData() {
        return tipoData;
    }

    public void setTipoData(int tipoData) {
        this.tipoData = tipoData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Aeropuerto getAeropuertoOrigen() {
        return aeropuertoOrigen;
    }

    public void setAeropuertoOrigen(Aeropuerto aeropuertoOrigen) {
        this.aeropuertoOrigen = aeropuertoOrigen;
    }

    public Aeropuerto getAeropuertoDestino() {
        return aeropuertoDestino;
    }

    public void setAeropuertoDestino(Aeropuerto aeropuertoDestino) {
        this.aeropuertoDestino = aeropuertoDestino;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public LocalTime getHoraLlegada() {
        return horaLlegada;
    }

    public void setHoraLlegada(LocalTime horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    public double getFrecuenciaPorDia() {
        return frecuenciaPorDia;
    }

    public void setFrecuenciaPorDia(double frecuenciaPorDia) {
        this.frecuenciaPorDia = frecuenciaPorDia;
    }

    public double getTiempoTransporte() {
        return tiempoTransporte;
    }

    public void setTiempoTransporte(double tiempoTransporte) {
        this.tiempoTransporte = tiempoTransporte;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getDuracionVueloMinutos() {
        return duracionVueloMinutos;
    }

    public void setDuracionVueloMinutos(int duracionVueloMinutos) {
        this.duracionVueloMinutos = duracionVueloMinutos;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }
    public int getCapacidadUsada() {
        return capacidadUsada;
    }
    public void setCapacidadUsada(int capacidadUsada) {
        this.capacidadUsada = capacidadUsada;
    }
    public boolean isEsIntercontinental() {
        return esIntercontinental;
    }

    public void setEsIntercontinental(boolean esIntercontinental) {
        this.esIntercontinental = esIntercontinental;
    }

    public EstadoVuelo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVuelo estado) {
        this.estado = estado;
    }

    public double getLatitudActual() {
        return latitudActual;
    }

    public void setLatitudActual(double latitudActual) {
        this.latitudActual = latitudActual;
    }

    public double getLongitudActual() {
        return longitudActual;
    }

    public void setLongitudActual(double longitudActual) {
        this.longitudActual = longitudActual;
    }

    public String getIdentificadorVuelo() {
        if (aeropuertoOrigen == null || aeropuertoDestino == null || horaSalida == null) {
            return null;
        }
        return String.format("%s-%s-%02d:%02d",
                aeropuertoOrigen.getCodigoIATA(),
                aeropuertoDestino.getCodigoIATA(),
                horaSalida.getHour(),
                horaSalida.getMinute());
    }
}
