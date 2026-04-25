package org.example.model;
import java.time.LocalDateTime;
import java.util.List;

public class Pedido {
    private Integer id;
    private String externalId;
    private String nombre;
    private Cliente cliente;
    private LocalDateTime fechaPedido;
    private String hora; // hh:mm
    private String minuto;
    private String aeropuertoOrigenCodigo;  //Solo es el código
    private String aeropuertoDestinoCodigo; //Solo es el código
    private LocalDateTime fechaLimiteEntrega;
    private String idCliente;
    private EstadoPedido estado;
    private LocalDateTime fechaRegistro;
    private Ruta ruta; // La ruta que el algoritmo le asignará
    private int cantidadMaletas;
    private List<PlanViaje> planesViaje;
    private double prioridad;
    private List<Maleta> maletas;

    public Pedido() {}
    private String idPedido;

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMinuto() {
        return minuto;
    }

    public void setMinuto(String minuto) {
        this.minuto = minuto;
    }

    public String getAeropuertoOrigenCodigo() {
        return aeropuertoOrigenCodigo;
    }

    public void setAeropuertoOrigenCodigo(String aeropuertoOrigenCodigo) {
        this.aeropuertoOrigenCodigo = aeropuertoOrigenCodigo;
    }

    public String getAeropuertoDestinoCodigo() {
        return aeropuertoDestinoCodigo;
    }

    public void setAeropuertoDestinoCodigo(String aeropuertoDestinoCodigo) {
        this.aeropuertoDestinoCodigo = aeropuertoDestinoCodigo;
    }

    public LocalDateTime getFechaLimiteEntrega() {
        return fechaLimiteEntrega;
    }

    public void setFechaLimiteEntrega(LocalDateTime fechaLimiteEntrega) {
        this.fechaLimiteEntrega = fechaLimiteEntrega;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public int getCantidadMaletas() {
        return cantidadMaletas;
    }

    public void setCantidadMaletas(int cantidadMaletas) {
        this.cantidadMaletas = cantidadMaletas;
    }
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public double getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(double prioridad) {
        this.prioridad = prioridad;
    }

    public List<Maleta> getMaletas() {
        return maletas;
    }

    public void setMaletas(List<Maleta> maletas) {
        this.maletas = maletas;
    }

    public List<PlanViaje> getPlanesViaje() {
        return planesViaje;
    }

    public void setPlanesViaje(List<PlanViaje> planesViaje) {
        this.planesViaje = planesViaje;
    }
}
