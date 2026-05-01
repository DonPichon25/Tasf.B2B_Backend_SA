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
    private Map<String, List<Vuelo>> vuelosPorOrigen; //para la respuesta

    public SimulatedAnnealingPlanner(List<Vuelo> todosLosVuelos) {
        this.vuelosPorOrigen = agruparVuelos(todosLosVuelos);
    }

    private Map<String, List<Vuelo>> agruparVuelos(List<Vuelo> vuelos) { //devuelve un map con lista de vuelos
        Map<String, List<Vuelo>> mapa = new HashMap<>();
        for (Vuelo v : vuelos) {
            mapa.computeIfAbsent(v.getAeropuertoOrigen().getCodigoIATA(), k -> new ArrayList<>()).add(v);//k es la llave
            //búscame la lista de vuelos para este código, Si buscas 'LIM' y no hay, crea una lista vacía, luego agregas el vuelo a la lista
        }
        return mapa;
    }

    /**
     * MÉTODO PRINCIPAL DEL ALGORITMO
     */
    public PlanViaje planificarRuta(Pedido pedido) {//recibe un pedido no más
        System.out.println("--- Iniciando Simulated Annealing para Pedido: " + pedido.getExternalId() + " ---");
        // --- 1. NUEVO: Obtenemos la cantidad de maletas de este pedido ---
        int maletasDelPedido = pedido.getMaletas().size();
        // 1. Generar una solución inicial (Puede ser subóptima)
        List<Vuelo> rutaActual = generarRutaInicialAleatoria(pedido); //se genera una solución
        double energiaActual = calcularEnergia(rutaActual, pedido);

        // Variables para guardar la mejor ruta histórica
        List<Vuelo> mejorRuta = new ArrayList<>(rutaActual); //se guarda la mejor ruta
        double mejorEnergia = energiaActual; // Se guarda la mejor energía

        double temperatura = temperaturaInicial;
        Random random = new Random();// máquina generadora de números aleatorios
        int iteraciones = 0;

        // 2. Bucle de Recocido Simulado
        while (temperatura > temperaturaMinima) { // si la temperatura(10000) es mayor que la minima(1)
            // Generar un vecino (pequeña mutación a la ruta actual)
            List<Vuelo> rutaVecina = generarVecino(rutaActual, pedido);//Se le pasa la ruta actual (lista de vuelos)
            double energiaVecina = calcularEnergia(rutaVecina, pedido);//calcula la energía de la nueva ruta

            // Calcular diferencia de energía (Si es negativo, el vecino es MEJOR)
            double deltaE = energiaVecina - energiaActual;// compara las energías, el menor es mejor

            // 3. Criterio de Aceptación
            if (deltaE < 0) {//es mejor el vecino
                // Aceptamos la mejora automáticamente
                rutaActual = new ArrayList<>(rutaVecina);// se sobreescribe con el vecino (lista de vuelos)
                energiaActual = energiaVecina;// se sobreescribe con el vecino

                // ¿Es la mejor que hemos visto en toda la historia?
                if (energiaActual < mejorEnergia) {// si la energia del vecino que ahora es actual es mejor que el histórico
                    mejorRuta = new ArrayList<>(rutaActual); // ahora la mejor ruta es ese vecino
                    mejorEnergia = energiaActual; // mejor energía ese vecino
                }
            } else {// el vecino empeora, delta es +
                // Si es peor, la aceptamos con una probabilidad que depende de la Temperatura
                double probabilidadAceptacion = Math.exp(-deltaE / temperatura);//probabilidad para aceptar solución peor
                //delta que tan mala, temperatura que tanto me arriesgo
                if (probabilidadAceptacion > random.nextDouble()) {//random.nextDouble() → número entre 0 y 1
                    rutaActual = new ArrayList<>(rutaVecina);//random < 0.000000000000000000000000000000000000000000045 muy pequeño para aceptar soluciones trágicas al final
                    energiaActual = energiaVecina; //Se acpeta como actuañ
                }
            }

            // 4. Enfriar el sistema
            temperatura *= factorEnfriamiento; //la temperatura va bajando
            iteraciones++; //la cantidad de iteraciones dentro del while aumenta
        }

        System.out.println("SA Terminado en " + iteraciones + " iteraciones. Mejor Energía: " + mejorEnergia);
        // --- 2. NUEVO: DESCONTAR CAPACIDAD FÍSICA ---
        // Solo descontamos la capacidad en la "mejorRuta" final
        if (mejorRuta != null && !mejorRuta.isEmpty() && mejorEnergia < 999999.0) {
            for (Vuelo v : mejorRuta) {
                // Sumamos las maletas del pedido a la capacidad que ya estaba usada
                int nuevaCapacidadUsada = v.getCapacidadUsada() + maletasDelPedido;
                v.setCapacidadUsada(nuevaCapacidadUsada);

                System.out.println("   [Ajuste Capacidad] Ocupación actualizada " +
                        "(Usada/Max): " + nuevaCapacidadUsada + "/" + v.getCapacidadMaxima());
            }
        }
        return construirPlanViaje(pedido, mejorRuta,mejorEnergia); //Se construye la mejorRuta para el pedio
    }

    /**
     * FUNCIÓN OBJETIVO (Fitness)
     * Evalúa qué tan buena es una ruta. A menor energía, mejor.
     */
    private double calcularEnergia(List<Vuelo> ruta, Pedido pedido) {
        if (ruta == null || ruta.isEmpty()) return Double.MAX_VALUE;

        double energia = 0.0;

        // 1. Validaciones letales (Las mismas reglas estrictas para ambos)
        for (int i = 0; i < ruta.size() - 1; i++) {
            Vuelo vActual = ruta.get(i);
            Vuelo vSiguiente = ruta.get(i+1);

            // Evitar teletransportación
            if (!vActual.getAeropuertoDestino().getCodigoIATA().equals(vSiguiente.getAeropuertoOrigen().getCodigoIATA())) {
                return 999999.0;
            }

            // Regla de Conexión Física: Mínimo 10 minutos de transbordo
            long minutosEscala = java.time.temporal.ChronoUnit.MINUTES.between(
                    vActual.getHoraLlegada(), vSiguiente.getHoraSalida());
            if (minutosEscala < 0) {
                minutosEscala += (24 * 60);
            }
            if (minutosEscala < 10) {
                return 999999.0; // Castigo letal: Física imposible
            }
        }

        // --- 2. CÁLCULO DE TIEMPO EN MINUTOS (Igualando la escala del Tabu Search) ---
        double minutosTotalesViaje = 0.0;

        for (int i = 0; i < ruta.size(); i++) {
            Vuelo vActual = ruta.get(i);

            // A. Sumar tiempo físico en el aire
            // Como tu getTiempoTransporte() devuelve horas (ej: 1.5), lo multiplicamos por 60 para tener minutos (90.0)
            minutosTotalesViaje += (vActual.getTiempoTransporte() * 60.0);

            // B. Sumar tiempo de espera en la escala
            if (i < ruta.size() - 1) {
                Vuelo vSiguiente = ruta.get(i + 1);
                long minutosEscala = java.time.temporal.ChronoUnit.MINUTES.between(
                        vActual.getHoraLlegada(), vSiguiente.getHoraSalida());

                if (minutosEscala < 0) {
                    minutosEscala += (24 * 60);
                }
                minutosTotalesViaje += minutosEscala; // Sumado directo en minutos
            }
        }

        // C. Regla 6: Sumar 10 minutos de recojo en almacén
        minutosTotalesViaje += 10.0;

        // La base del fitness (energía) ahora son los minutos reales
        energia += minutosTotalesViaje;

        // --- 3. PENALIZACIÓN POR SLA ---
        // Obtenemos los minutos máximos permitidos calculando la diferencia entre tus fechas
        long limiteSLAMinutos = java.time.Duration.between(pedido.getFechaPedido(), pedido.getFechaLimiteEntrega()).toMinutes();

        if (minutosTotalesViaje > limiteSLAMinutos) {
            // Aplicamos exactamente el mismo castigo que tu compañera
            energia += 5000.0;
        }

        return energia;
    }

    // --- MÉTODOS AUXILIARES  ---
    private List<Vuelo> generarRutaInicialAleatoria(Pedido pedido) {
        String origen = pedido.getAeropuertoOrigenCodigo();
        String destino = pedido.getAeropuertoDestinoCodigo();
        int maletasDelPedido = pedido.getMaletas().size();
        List<Vuelo> rutaEncontrada = new ArrayList<>();
        Set<String> aeropuertosVisitados = new HashSet<>();

        // Marcamos el origen como visitado para no volver a él
        aeropuertosVisitados.add(origen);

        // Llamamos a la función recursiva. Le damos un límite de 5 vuelos máximo
        // para que no haga viajes absurdamente largos por todo el mundo.
        int limiteVuelos = 5;

        boolean exito = buscarRutaRecursiva(origen, destino, null, aeropuertosVisitados, rutaEncontrada, limiteVuelos,maletasDelPedido);

        if (exito) {
            return rutaEncontrada;
        } else {
            return new ArrayList<>(); // Retorna vacío si de plano no encuentra ruta
        }
    }
    private boolean buscarRutaRecursiva(String aeropuertoActual, String destinoFinal,
                                        LocalTime horaLlegadaPrevia, Set<String> visitados,
                                        List<Vuelo> rutaActual, int vuelosMaximos, int maletasDelPedido) {

        // 1. CASO BASE (Éxito): Si llegamos al destino, terminamos la búsqueda.
        if (aeropuertoActual.equals(destinoFinal)) {
            return true;
        }

        // 2. LÍMITE (Fracaso controlado): Si ya tomamos muchos vuelos y no llegamos, abortamos esta rama.
        if (rutaActual.size() >= vuelosMaximos) {
            return false;
        }

        // Obtener vuelos desde el aeropuerto actual
        List<Vuelo> salidas = vuelosPorOrigen.getOrDefault(aeropuertoActual, new ArrayList<>());
        if (salidas.isEmpty()) return false;

        // Barajar para que el Simulated Annealing explore diferentes rutas en cada iteración
        List<Vuelo> salidasAleatorias = new ArrayList<>(salidas);
        Collections.shuffle(salidasAleatorias);

        // 3. EXPLORACIÓN
        for (Vuelo vuelo : salidasAleatorias) {
            String siguienteAeropuerto = vuelo.getAeropuertoDestino().getCodigoIATA();

            // REGLA A: Evitar bucles (no volar a un aeropuerto que ya visitamos en esta ruta)
            if (visitados.contains(siguienteAeropuerto)) {
                continue;
            }

            // REGLA B: El vuelo debe salir DESPUÉS de que el vuelo anterior haya llegado
            // --- 👇 REGLA B MODIFICADA (Reemplazo) 👇 ---
            // El vuelo de conexión debe respetar los 10 minutos de transbordo (MCT)
            if (horaLlegadaPrevia != null) {
                long minutosEscala = java.time.temporal.ChronoUnit.MINUTES.between(
                        horaLlegadaPrevia, vuelo.getHoraSalida());

                // Ajuste por si la escala cruza la medianoche
                if (minutosEscala < 0) {
                    minutosEscala += (24 * 60);
                }

                // Si hay menos de 10 minutos entre llegada y salida, saltamos este vuelo
                if (minutosEscala < 10) {
                    continue;
                }
            }
            // --- 3. NUEVO: VALIDACIÓN ESTRICTA DE CAPACIDAD ---
            // Calculamos el espacio disponible restando lo usado a lo máximo
            int capacidadDisponible = vuelo.getCapacidadMaxima() - vuelo.getCapacidadUsada();

            // Si no hay suficiente espacio para todas las maletas del pedido, saltamos este vuelo
            if (capacidadDisponible < maletasDelPedido) {
                continue;
            }
            // --- AVANZAR (Hacer el movimiento) ---
            rutaActual.add(vuelo);
            visitados.add(siguienteAeropuerto);

            // Llamada recursiva: saltamos al siguiente aeropuerto a ver si desde ahí llegamos al destino
            if (buscarRutaRecursiva(siguienteAeropuerto, destinoFinal, vuelo.getHoraLlegada(), visitados, rutaActual, vuelosMaximos,maletasDelPedido)) {
                return true; // ¡Encontramos el camino hasta el final!
            }

            // --- BACKTRACKING (Deshacer el movimiento) ---
            // Si el camino nos llevó a un callejón sin salida, retrocedemos:
            // sacamos el vuelo de la lista, borramos la visita, y el bucle for probará con el siguiente vuelo.
            rutaActual.remove(rutaActual.size() - 1);
            visitados.remove(siguienteAeropuerto);
        }

        return false; // Si probamos todas las salidas y ninguna funcionó, esta ruta es un callejón sin salida.
    }

    private List<Vuelo> generarVecino(List<Vuelo> rutaActual, Pedido pedido) {
        // Para este MVP rápido, un "vecino" será simplemente buscar OTRA
        // ruta aleatoria. Como usamos shuffle() arriba, nos dará una distinta.
        List<Vuelo> nuevaRuta = generarRutaInicialAleatoria(pedido); //Se genera una ruta inicial aleatoria
        // Si por algún motivo no encuentra otra, devolvemos la misma para no romper el ciclo
        if (nuevaRuta.isEmpty()) {// Si no se encuentra una, devuelve la misma
            return rutaActual;
        }
        return nuevaRuta;
    }

    // Nota: Cambia "PlanViaje" por "Ruta" aquí si decidiste usar la clase Ruta
    private PlanViaje construirPlanViaje(Pedido pedido, List<Vuelo> mejorRuta,double energiaFinal) {
        PlanViaje plan = new PlanViaje(); // O Ruta plan = new Ruta();
        plan.setAlgoritmoUsado("Simulated Annealing V1");
        // GUARDAMOS LA ENERGÍA PARA EL REPORTE
        plan.setEnergiaCalculada(energiaFinal);
        if (mejorRuta == null || mejorRuta.isEmpty()|| energiaFinal >= 999999.0) {
            plan.setEstado("CANCELADO");
            plan.setDuracionTotalHoras(0.0);
            return plan;
        }

        // Llenar datos básicos para imprimir
        plan.setEstado("COMPLETADO");
        plan.setNumeroVuelos(mejorRuta.size());

        // CALCULAMOS Y GUARDAMOS LAS HORAS TOTALES PARA EL REPORTE
        // Calcular duración exacta sumando tiempos de transporte y escalas reales
        double horasViaje = 0.0;
        for (int i = 0; i < mejorRuta.size(); i++) {
            Vuelo vActual = mejorRuta.get(i);
            horasViaje += vActual.getTiempoTransporte();

            if (i < mejorRuta.size() - 1) {
                Vuelo vSiguiente = mejorRuta.get(i + 1);
                long minutosEscala = java.time.temporal.ChronoUnit.MINUTES.between(
                        vActual.getHoraLlegada(), vSiguiente.getHoraSalida());

                if (minutosEscala < 0) {
                    minutosEscala += (24 * 60);
                }
                horasViaje += (minutosEscala / 60.0);
            }
        }

        plan.setDuracionTotalHoras(horasViaje);
        ///////////////////////////////////////////////////

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
