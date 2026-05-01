package org.example;
import org.example.model.Pedido;
import org.example.model.PlanViaje;
import org.example.service.*;
import org.example.model.Aeropuerto;
import org.example.model.Vuelo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Main implements CommandLineRunner {

    // Aquí "inyectamos" el servicio que creamos antes
    //  @Autowired
    //  private DataLoaderService dataLoader;

    public static void main(String[] args) {
        // Esta línea arranca todo el ecosistema de Spring
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==============================================");
        System.out.println("   INICIANDO SISTEMA DE TRASLADO TASF.B2B     ");
        System.out.println("==============================================");

        // 1. Definimos las rutas de tus archivos (Ajusta si cambiaste el nombre de la carpeta)
        String rutaAeros = "src/main/resources/data/aeropuertos.txt";
        String rutaVuelos = "src/main/resources/data/planesVuelos.txt";
        String directorioPedidos = "src/main/resources/data/pedidos";
        // 2. Intentamos cargar los aeropuertos
        System.out.println("Cargando base de datos de aeropuertos...");
        LectorAeropuerto lector = new LectorAeropuerto(rutaAeros);
        ArrayList<Aeropuerto> aeropuertos = lector.leerAeropuertos();
        // 3. Intentamos cargar los vuelos usando los aeropuertos previos
        System.out.println("Cargando base de datos de vuelos...");
        // Instanciamos el lector pasándole la ruta Y la lista de aeropuertos
        LectorVuelos lectorVuelos = new LectorVuelos(rutaVuelos, aeropuertos);
        // Ejecutamos la lectura
        ArrayList<Vuelo> vuelos = lectorVuelos.leerVuelos();
        System.out.println(">>> ÉXITO: Se han cargado " + vuelos.size() + " vuelos.");
        System.out.println("==============================================");
        System.out.println("SISTEMA LISTO PARA RECIBIR ÓRDENES DE PEDIDOS");
        // 4. Intentamos cargar los pedidos
        System.out.println("\nCargando base de datos de pedidos...");
        LectorPedidos lectorPedidos = new LectorPedidos(directorioPedidos, aeropuertos);
        // Ejecutamos la lectura pasando null, null para no filtrar por fecha (lee todo el archivo)
        LectorPedidos.ResultadoCargaPedidos resultadoPedidos = lectorPedidos.leerYGuardarPedidos(null, null);

        if (resultadoPedidos.exito) {
            System.out.println(">>> ÉXITO: Se ha cargado la demanda de maletas correctamente.");
        } else {
            System.err.println(">>> ADVERTENCIA: Hubo errores cargando los pedidos. Revisa los logs.");
        }
        System.out.println("==============================================");
        System.out.println(" ENTORNO VIRTUAL CARGADO 100% EN MEMORIA RAM  ");
        System.out.println(" LISTO PARA EJECUTAR ALGORITMO METAHEURÍSTICO ");
        System.out.println("==============================================");
        System.out.println("\nIniciando motor de Recocido Simulado...");

        // OJO: Cambia 'getPedidos()' por el nombre real del método que tengas
        // en tu LectorPedidos para obtener la lista que guardaste en RAM.
        List<Pedido> todosLosPedidos = lectorPedidos.getPedidos();

        if (todosLosPedidos != null && !todosLosPedidos.isEmpty()) {
            System.out.println(">> Total de pedidos en cola para enrutamiento: " + todosLosPedidos.size());

            // --- NUEVO: ORDENAR LA COLA DE PEDIDOS (Estrategia Greedy) ---
            // --- ORDENAMIENTO INTELIGENTE (Estrategia EDF: Earliest Deadline First) ---
            System.out.println(">> Ordenando pedidos por urgencia de entrega (SLA)...");

            todosLosPedidos.sort((p1, p2) -> {
                // 1er Criterio Absoluto: La Fecha Límite de Entrega.
                // El paquete que caduca antes, pasa primero al frente de la fila.
                int comparacionFechaLimite = p1.getFechaLimiteEntrega().compareTo(p2.getFechaLimiteEntrega());

                if (comparacionFechaLimite != 0) {
                    return comparacionFechaLimite;
                }

                // 2do Criterio (Desempate): Si mágicamente dos pedidos vencen en el mismo
                // milisegundo, le damos prioridad al que tiene el SLA más estricto (1.0).
                return Double.compare(p2.getPrioridad(), p1.getPrioridad());
            });
            // -------------------------------------------------------------

            // 5.1 Instanciamos el algoritmo pasándole todos los vuelos del mundo
            SimulatedAnnealingPlanner sa = new SimulatedAnnealingPlanner(vuelos);
            // 👉 LÍNEA NUEVA: Iniciamos el cronómetro justo antes del for
            long tiempoInicioAlgoritmo = System.currentTimeMillis();
            // 5.2 Para esta primera prueba sacamos solo los primeros 100
            for (Pedido pedido : todosLosPedidos) {

                System.out.println("\n>> 🚀 Buscando ruta para: " + pedido.getNombre() +
                        " (" + pedido.getAeropuertoOrigenCodigo() +
                        " -> " + pedido.getAeropuertoDestinoCodigo() + ")");
                PlanViaje solucion = sa.planificarRuta(pedido);
                // 👉 LÍNEAS NUEVAS: Guardamos la solución DENTRO del pedido
                if (solucion != null) {
                    pedido.setPlanesViaje(Collections.singletonList(solucion));
                }
            }
            long tiempoFinAlgoritmo = System.currentTimeMillis();
            /////////////////////////////////GENERAR REPORTE EXPERIMENTACIÓN /////////////////////////////////////
            ReporteGlobal reporte = new ReporteGlobal();
            reporte.nombreAlgoritmo = "Recocido Simulado V1";
            reporte.tiempoEjecucionMs = tiempoFinAlgoritmo - tiempoInicioAlgoritmo;
            reporte.totalPedidos = todosLosPedidos.size();

            for (Pedido p : todosLosPedidos) {
                // Suponiendo que guardas el PlanViaje o Ruta dentro del Pedido
                PlanViaje plan = p.getPlanesViaje() != null && !p.getPlanesViaje().isEmpty() ? p.getPlanesViaje().get(0) : null;

                if (plan != null && plan.getEstado().equals("COMPLETADO")) {
                    reporte.pedidosPlanificados++;

                    // 1. Calcular horas de vuelo de este pedido
                    double horasViaje = plan.getDuracionTotalHoras(); // Asegúrate de tener este método
                    reporte.horasTotalesVuelo += horasViaje;

                    // 2. Verificar si llegó a tiempo (SLA)
                    // 2. Verificar si llegó a tiempo (SLA) - ¡AHORA EN MINUTOS!
                    long limiteSLAMinutos = java.time.Duration.between(p.getFechaPedido(), p.getFechaLimiteEntrega()).toMinutes();
                    double minutosViaje = horasViaje * 60.0;

                    if (minutosViaje <= limiteSLAMinutos) {
                        reporte.entregasATiempo++;
                    } else {
                        // 🚨 ¡ATRAPAMOS AL CULPABLE! Imprimimos quién es para investigarlo
                        System.out.println("\n🚨 [ALERTA SLA] Pedido Tarde: " + p.getNombre() +
                                " | Ruta: " + p.getAeropuertoOrigenCodigo() + " -> " + p.getAeropuertoDestinoCodigo());
                        System.out.println("   - Límite SLA : " + (limiteSLAMinutos / 60.0) + " horas");
                        System.out.println("   - Tiempo Real: " + horasViaje + " horas");
                    }

                    // 3. Sumar al fitness global (Suponiendo que guardas la energía en el plan)
                    reporte.fitnessTotal += plan.getEnergiaCalculada(); // Asegúrate de guardar y obtener la energía
                } else {
                    // Si el pedido no se planificó, se suma una penalidad letal al fitness global
                    reporte.fitnessTotal += 999999.0;
                }
            }

            // Calcular Uso de Capacidad recorriendo los vuelos
            for (Vuelo v : vuelos) {
                if (v.getCapacidadUsada() > 0) {
                    reporte.vuelosUtilizados++;
                    reporte.capacidadTotalUtilizada += v.getCapacidadUsada();
                    reporte.capacidadTotalDisponibleEnVuelosUsados += v.getCapacidadMaxima();
                }
            }

            // Mostrar resultados
            reporte.imprimirReporte();

        } else {
            System.err.println("❌ No hay pedidos en la memoria para procesar. Revisa la carga.");
        }

    }

}