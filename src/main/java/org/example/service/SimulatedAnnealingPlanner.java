package org.example.service;

import org.example.model.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class SimulatedAnnealingPlanner {

    // Parámetros de ajuste del algoritmo
    private double temperaturaInicial = 10000.0;
    private double temperaturaMinima = 1.0;
    private double factorEnfriamiento = 0.95;

    // Diccionario para buscar vuelos rápido: { "LIM": [Vuelo1, Vuelo2...], "BOG": [...] }
    private Map<String, List<Vuelo>> vuelosPorOrigen;

    public SimulatedAnnealingPlanner(List<Vuelo> todosLosVuelos) {
        this.vuelosPorOrigen = agruparVuelos(todosLosVuelos);
    }

    private Map<String, List<Vuelo>> agruparVuelos(List<Vuelo> vuelos) {
        Map<String, List<Vuelo>> mapa = new HashMap<>();
        for (Vuelo v : vuelos) {
            mapa.computeIfAbsent(v.getAeropuertoOrigen().getCodigoIATA(), k -> new ArrayList<>()).add(v);
        }
        return mapa;
    }

    /**
     * MÉTODO PRINCIPAL DEL ALGORITMO
     */
    public PlanViaje planificarRuta(Pedido pedido) {
        System.out.println("--- Iniciando Simulated Annealing para Pedido: " + pedido.getExternalId() + " ---");

        // 1. Generar una solución inicial (Puede ser subóptima)
        List<Vuelo> rutaActual = generarRutaInicialAleatoria(pedido);
        double energiaActual = calcularEnergia(rutaActual, pedido);

        // Variables para guardar la mejor ruta histórica
        List<Vuelo> mejorRuta = new ArrayList<>(rutaActual);
        double mejorEnergia = energiaActual;

        double temperatura = temperaturaInicial;
        Random random = new Random();
        int iteraciones = 0;

        // 2. Bucle de Recocido Simulado
        while (temperatura > temperaturaMinima) {
            // Generar un vecino (pequeña mutación a la ruta actual)
            List<Vuelo> rutaVecina = generarVecino(rutaActual, pedido);
            double energiaVecina = calcularEnergia(rutaVecina, pedido);

            // Calcular diferencia de energía (Si es negativo, el vecino es MEJOR)
            double deltaE = energiaVecina - energiaActual;

            // 3. Criterio de Aceptación
            if (deltaE < 0) {
                // Aceptamos la mejora automáticamente
                rutaActual = new ArrayList<>(rutaVecina);
                energiaActual = energiaVecina;

                // ¿Es la mejor que hemos visto en toda la historia?
                if (energiaActual < mejorEnergia) {
                    mejorRuta = new ArrayList<>(rutaActual);
                    mejorEnergia = energiaActual;
                }
            } else {
                // Si es peor, la aceptamos con una probabilidad que depende de la Temperatura
                double probabilidadAceptacion = Math.exp(-deltaE / temperatura);
                if (probabilidadAceptacion > random.nextDouble()) {
                    rutaActual = new ArrayList<>(rutaVecina);
                    energiaActual = energiaVecina;
                }
            }

            // 4. Enfriar el sistema
            temperatura *= factorEnfriamiento;
            iteraciones++;
        }

        System.out.println("SA Terminado en " + iteraciones + " iteraciones. Mejor Energía: " + mejorEnergia);
        return construirPlanViaje(pedido, mejorRuta);
    }

    /**
     * FUNCIÓN OBJETIVO (Fitness)
     * Evalúa qué tan buena es una ruta. A menor energía, mejor.
     */
    private double calcularEnergia(List<Vuelo> ruta, Pedido pedido) {
        if (ruta == null || ruta.isEmpty()) return Double.MAX_VALUE; // Ruta inválida

        double energia = 0.0;

        // 1. Validar conexión física (Que el destino de V1 sea origen de V2)
        for (int i = 0; i < ruta.size() - 1; i++) {
            Vuelo vActual = ruta.get(i);
            Vuelo vSiguiente = ruta.get(i+1);

            //Navegamos dentro del objeto Aeropuerto
            if (!vActual.getAeropuertoDestino().getCodigoIATA().equals(vSiguiente.getAeropuertoOrigen().getCodigoIATA())) {
                return 999999.0; // Penalidad letal por teletransportación
            }

            //  Usamos getHoraSalida() y getHoraLlegada() (LocalTime)
            // Validamos que el vuelo 2 salga DESPUÉS de que llegue el vuelo 1 (asumiendo mismo día para este MVP)
            if (vSiguiente.getHoraSalida().isBefore(vActual.getHoraLlegada())) {
                return 999999.0; // Penalidad letal por viaje en el tiempo
            }
        }

        // 3. Calcular tiempo total del viaje
        LocalTime salidaInicial = ruta.get(0).getHoraSalida();
        LocalTime llegadaFinal = ruta.get(ruta.size() - 1).getHoraLlegada();
        long horasViaje = java.time.temporal.ChronoUnit.HOURS.between(salidaInicial, llegadaFinal);
        if (horasViaje < 0) {
            horasViaje += 24;
        }
        energia += horasViaje; // La base de la energía son las horas

        // 4. Penalidad por SLA (24h o 48h) de Tasf.B2B
        long limiteSLA = Duration.between(pedido.getFechaPedido(), pedido.getFechaLimiteEntrega()).toHours();
        if (horasViaje > limiteSLA) {
            energia += 5000.0; // Fuerte penalidad si no llega a tiempo
        }

        return energia;
    }

    // --- MÉTODOS AUXILIARES  ---
    private List<Vuelo> generarRutaInicialAleatoria(Pedido pedido) {
        String origen = pedido.getAeropuertoOrigenCodigo();
        String destino = pedido.getAeropuertoDestinoCodigo();

        List<Vuelo> salidas = vuelosPorOrigen.getOrDefault(origen, new ArrayList<>());
        if (salidas.isEmpty()) return new ArrayList<>();

        // 1. Intentar vuelo directo rápido
        for (Vuelo v : salidas) {
            if (v.getAeropuertoDestino().getCodigoIATA().equals(destino)) {
                return new ArrayList<>(Collections.singletonList(v));
            }
        }

        // 2. Si no hay directo, buscar 1 escala aleatoria
        List<Vuelo> salidasAleatorias = new ArrayList<>(salidas);
        Collections.shuffle(salidasAleatorias); // Mezclar para dar variedad

        for (Vuelo v1 : salidasAleatorias) {
            String escala = v1.getAeropuertoDestino().getCodigoIATA();
            List<Vuelo> conexiones = vuelosPorOrigen.getOrDefault(escala, new ArrayList<>());

            List<Vuelo> conexionesAleatorias = new ArrayList<>(conexiones);
            Collections.shuffle(conexionesAleatorias);

            for (Vuelo v2 : conexionesAleatorias) {
                if (v2.getAeropuertoDestino().getCodigoIATA().equals(destino)) {
                    // Verificación súper básica de hora para el MVP (asume mismo día)
                    if (v2.getHoraSalida().isAfter(v1.getHoraLlegada())) {
                        return Arrays.asList(v1, v2);
                    }
                }
            }
        }
        return new ArrayList<>(); // Retorna vacío si de plano no encuentra ruta
    }

    private List<Vuelo> generarVecino(List<Vuelo> rutaActual, Pedido pedido) {
        // Para este MVP rápido, un "vecino" será simplemente buscar OTRA
        // ruta aleatoria. Como usamos shuffle() arriba, nos dará una distinta.
        List<Vuelo> nuevaRuta = generarRutaInicialAleatoria(pedido);

        // Si por algún motivo no encuentra otra, devolvemos la misma para no romper el ciclo
        if (nuevaRuta.isEmpty()) {
            return rutaActual;
        }
        return nuevaRuta;
    }

    // Nota: Cambia "PlanViaje" por "Ruta" aquí si decidiste usar la clase Ruta
    private PlanViaje construirPlanViaje(Pedido pedido, List<Vuelo> mejorRuta) {
        PlanViaje plan = new PlanViaje(); // O Ruta plan = new Ruta();
        plan.setAlgoritmoUsado("Simulated Annealing V1");

        if (mejorRuta == null || mejorRuta.isEmpty()) {
            plan.setEstado("CANCELADO");
            return plan;
        }

        // Llenar datos básicos para imprimir
        plan.setEstado("COMPLETADO");
        plan.setNumeroVuelos(mejorRuta.size());

        System.out.println("✅ RUTA ENCONTRADA:");
        for (int i = 0; i < mejorRuta.size(); i++) {
            Vuelo v = mejorRuta.get(i);
            System.out.println("   [Vuelo " + (i+1) + "] " +
                    v.getAeropuertoOrigen().getCodigoIATA() + " -> " +
                    v.getAeropuertoDestino().getCodigoIATA() +
                    " | Salida: " + v.getHoraSalida());
        }

        return plan;
    }

}
