package org.example.service;

import org.example.model.Aeropuerto;
import org.example.model.Vuelo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectorVuelos {

    private ArrayList<Vuelo> vuelos;
    private final String rutaArchivo;
    private ArrayList<Aeropuerto> aeropuertos;

    public LectorVuelos(String rutaArchivo, ArrayList<Aeropuerto> aeropuertos) {
        this.rutaArchivo = rutaArchivo;
        this.vuelos = new ArrayList<>();
        this.aeropuertos = aeropuertos;
    }

    public ArrayList<Vuelo> leerVuelos() {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            Map<String, Aeropuerto> mapaAeropuertos = crearMapaAeropuertos();
            boolean primerLinea = true;

            while ((linea = reader.readLine()) != null) {
                // Saltar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Saltar cabecera si existe
                if (primerLinea && linea.contains("Codigo")) {
                    primerLinea = false;
                    continue;
                }
                primerLinea = false;
                // Formato 2 (antiguo): ORIGEN-DESTINO-SALIDA-LLEGADA-CAPACIDAD
                Vuelo vuelo = null;

                // Formato antiguo con guiones
                String[] partes = linea.split("-");
                if (partes.length == 5) {
                    String codigoOrigen = partes[0];
                    String codigoDestino = partes[1];
                    String horaSalida = partes[2];
                    String horaLlegada = partes[3];
                    int capacidadMaxima = Integer.parseInt(partes[4]);

                    // Buscar aeropuertos por código IATA
                    Aeropuerto aeropuertoOrigen = mapaAeropuertos.get(codigoOrigen);
                    Aeropuerto aeropuertoDestino = mapaAeropuertos.get(codigoDestino);

                    if (aeropuertoOrigen != null && aeropuertoDestino != null) {
                        // Parsear horas
                        LocalTime horaSalidaParsed = parsearHora(horaSalida);
                        LocalTime horaLlegadaParsed = parsearHora(horaLlegada);

                        // Calcular tiempo de transporte en horas
                        double tiempoTransporte = calcularTiempoTransporte(horaSalida, horaLlegada);

                        // Calcular costo
                        double costo = calcularCostoVuelo(aeropuertoOrigen, aeropuertoDestino, capacidadMaxima);
                        // Crear objeto Vuelo
                        vuelo = new Vuelo();
                        // El identificador se genera automáticamente via getIdentificadorVuelo()
                        vuelo.setFrecuenciaPorDia(1.0);
                        vuelo.setAeropuertoOrigen(aeropuertoOrigen);
                        vuelo.setAeropuertoDestino(aeropuertoDestino);
                        vuelo.setCapacidadMaxima(capacidadMaxima);
                        vuelo.setCapacidadUsada(0);
                        vuelo.setTiempoTransporte(tiempoTransporte);
                        vuelo.setCosto(costo);
                        vuelo.setHoraSalida(horaSalidaParsed);
                        vuelo.setHoraLlegada(horaLlegadaParsed);
                    }
                }
                if (vuelo != null) {
                    vuelos.add(vuelo);
                }
            }

        } catch (IOException e) {
            System.err.println("Error leyendo datos de vuelos: " + e.getMessage());
            e.printStackTrace();
        }

        return vuelos;
    }

    private Map<String, Aeropuerto> crearMapaAeropuertos() {
        return crearMapaAeropuertosStatic(aeropuertos);
    }

    /**
     * Crea un mapa de aeropuertos por código IATA para búsquedas rápidas O(1)
     */
    private static Map<String, Aeropuerto> crearMapaAeropuertosStatic(List<Aeropuerto> aeropuertos) {
        Map<String, Aeropuerto> mapa = new HashMap<>();
        for (Aeropuerto aeropuerto : aeropuertos) {
            mapa.put(aeropuerto.getCodigoIATA(), aeropuerto);
        }
        return mapa;
    }

    /**
     * REFACTORIZADO: Parsea una línea de vuelo y retorna un objeto Vuelo
     * Soporta ambos formatos: CSV y antiguo con guiones
     */

    private double calcularTiempoTransporte(String horaSalida, String horaLlegada) {
        return calcularTiempoTransporteStatic(horaSalida, horaLlegada);
    }

    private static double calcularTiempoTransporteStatic(String horaSalida, String horaLlegada) {
        LocalTime salida = parsearHoraStatic(horaSalida);
        LocalTime llegada = parsearHoraStatic(horaLlegada);

        // Calcular duración entre salida y llegada
        long minutos;
        if (llegada.isBefore(salida)) {
            // Vuelo cruza medianoche
            minutos = Duration.between(salida, LocalTime.of(23, 59, 59)).toMinutes() +
                    Duration.between(LocalTime.of(0, 0), llegada).toMinutes() + 1;
        } else {
            minutos = Duration.between(salida, llegada).toMinutes();
        }

        // Convertir minutos a horas
        return minutos / 60.0;
    }

    private LocalTime parsearHora(String horaStr) {
        return parsearHoraStatic(horaStr);
    }

    private static LocalTime parsearHoraStatic(String horaStr) {
        int horas = Integer.parseInt(horaStr.substring(0, 2));
        int minutos = Integer.parseInt(horaStr.substring(3, 5));
        return LocalTime.of(horas, minutos);
    }

    private double calcularCostoVuelo(Aeropuerto origen, Aeropuerto destino, int capacidad) {
        return calcularCostoVueloStatic(origen, destino, capacidad);
    }

    private static double calcularCostoVueloStatic(Aeropuerto origen, Aeropuerto destino, int capacidad) {
        // Modelo de costo simple basado en si los aeropuertos están en el mismo continente y capacidad
        boolean vueloMismoContinente = origen.getCiudad().getContinente() == destino.getCiudad().getContinente();

        double costoBase;
        if (vueloMismoContinente) {
            // 2 días mismo continente = 48 horas
            costoBase = 48 * 100;
        } else {
            // 3 días diferente continente = 72 horas
            costoBase = 72 * 150;
        }

        // Ajustar costo basado en capacidad
        double factorCapacidad = capacidad / 300.0;

        return costoBase * factorCapacidad;
    }
}

