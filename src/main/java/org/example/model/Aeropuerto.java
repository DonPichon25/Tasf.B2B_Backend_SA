package org.example.model;
import org.example.model.EstadoAeropuerto;
public class Aeropuerto {
    private String codigoIATA;
    private String alias;
    private Integer zonaHorariaUTC;
    private String latitud;
    private String longitud;
    private Ciudad ciudad;
    private EstadoAeropuerto estado;
    private Almacen almacen;
    private int tipoData; // 0: Original, 1: Generado
    // Métodos de conveniencia para compatibilidad con código existente
    // Delegan al Almacen asociado
    public Integer getCapacidadMaxima() {
        return almacen != null ? almacen.getCapacidadMaxima() : 0;
    }
    public Integer getCapacidadActual() {
        return almacen != null ? almacen.getCapacidadUsada() : 0;
    }
    public void setCapacidadActual(Integer capacidad) {
        if (almacen != null) {
            almacen.setCapacidadUsada(capacidad);
        }
    }
    public void setCapacidadMaxima(Integer capacidad) {
        if (almacen != null) {
            almacen.setCapacidadMaxima(capacidad);
        }
    }
    private Integer id;

    public String getCodigoIATA() {
        return codigoIATA;
    }

    public void setCodigoIATA(String codigoIATA) {
        this.codigoIATA = codigoIATA;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getZonaHorariaUTC() {
        return zonaHorariaUTC;
    }

    public void setZonaHorariaUTC(Integer zonaHorariaUTC) {
        this.zonaHorariaUTC = zonaHorariaUTC;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public EstadoAeropuerto getEstado() {
        return estado;
    }

    public void setEstado(EstadoAeropuerto estado) {
        this.estado = estado;
    }

    public Almacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacen almacen) {
        this.almacen = almacen;
    }

    public int getTipoData() {
        return tipoData;
    }

    public void setTipoData(int tipoData) {
        this.tipoData = tipoData;
    }

}


