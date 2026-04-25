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

import java.util.ArrayList;
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

            // 5.1 Instanciamos el algoritmo pasándole todos los vuelos del mundo
            SimulatedAnnealingPlanner sa = new SimulatedAnnealingPlanner(vuelos);

            // 5.2 Para esta primera prueba sacamos solo los primeros 100
            for (Pedido pedido : todosLosPedidos) {

                System.out.println("\n>> 🚀 Buscando ruta para: " + pedido.getNombre() +
                        " (" + pedido.getAeropuertoOrigenCodigo() +
                        " -> " + pedido.getAeropuertoDestinoCodigo() + ")");

                PlanViaje solucion = sa.planificarRuta(pedido);
            }

        } else {
            System.err.println("❌ No hay pedidos en la memoria para procesar. Revisa la carga.");
        }

    }

}