package org.example.model;

public class Ciudad {
    private String codigo; //DE 4 LETRAS SEGUN EL txt
    private String nombre; //nombre de la ciudad
    private String pais; //pais de la ciudad
    private Continente continente;
    private int tipoData;
    private Integer id;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }
    public Continente getContinente() {
        return continente;
    }
    public void setContinente(Continente continente) {
        this.continente = continente;
    }
    public int getTipoData() {
        return tipoData;
    }
    public void setTipoData(int tipoData) {
        this.tipoData = tipoData;
    }

}
