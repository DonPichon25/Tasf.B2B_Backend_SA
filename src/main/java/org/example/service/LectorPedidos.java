package org.example.service;

import org.example.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class LectorPedidos {
    private final String directorioDatos;
    private final List<Aeropuerto> aeropuertos;
    private final Map<String, Aeropuerto> mapaAeropuertos;
    // Caché de clientes para evitar búsquedas repetidas
    private Map<Long, Cliente> cacheClientes = new HashMap<>();
    private List<Pedido> listaMaestraPedidos = new ArrayList<>();//vamos a revisar
    public List<Pedido> getPedidos() {
        return listaMaestraPedidos;
    }
    public LectorPedidos(String directorioDatos, List<Aeropuerto> aeropuertos) {
        this.directorioDatos = directorioDatos;
        this.aeropuertos = aeropuertos;
        this.mapaAeropuertos = crearMapaAeropuertos();
    }

    private Map<String, Aeropuerto> crearMapaAeropuertos() {
        Map<String, Aeropuerto> mapa = new HashMap<>();
        for (Aeropuerto a : aeropuertos) {
            if (a.getCodigoIATA() != null) {
                mapa.put(a.getCodigoIATA().trim().toUpperCase(), a);
            }
        }
        return mapa;
    }
    public static class ResultadoCargaPedidos {
        public boolean exito;
        public String mensajeError;
        public LocalDateTime tiempoInicio;
        public LocalDateTime tiempoFin;
        public long duracionSegundos;
        public int pedidosCargados;
        public int pedidosCreados;
        public int pedidosFiltrados;
        public int erroresParseo;
        public int erroresArchivos;
    }

    /**
     * Lee y guarda pedidos desde todos los archivos _pedidos_{AIRPORT}_
     *
     * @param horaInicioSimulacion Opcional: solo cargar pedidos después de esta hora
     * @param horaFinSimulacion    Opcional: solo cargar pedidos antes de esta hora
     * @return Resultado con estadísticas de la carga
     */
    public ResultadoCargaPedidos leerYGuardarPedidos(
            LocalDateTime horaInicioSimulacion,
            LocalDateTime horaFinSimulacion) {

        ResultadoCargaPedidos resultado = new ResultadoCargaPedidos();
        File directorio = new File(directorioDatos);

        if (!directorio.exists() || !directorio.isDirectory()) {
            resultado.exito = false;
            resultado.mensajeError = "Directorio no encontrado: " + directorioDatos;
            System.err.println("ERROR: " + resultado.mensajeError);
            return resultado;
        }

        // Buscar todos los archivos con patrón _pedidos_{AIRPORT}_ o _pedidos_{AIRPORT}_.txt
        File[] archivosPedidos = directorio.listFiles((dir, nombre) ->
                nombre.startsWith("_envios_") &&
                        (nombre.endsWith("_") || nombre.endsWith("_.txt") || nombre.endsWith(".txt"))
        );

        if (archivosPedidos == null || archivosPedidos.length == 0) {
            resultado.exito = false;
            resultado.mensajeError = "No se encontraron archivos con patrón _pedidos_{AIRPORT}_";
            System.err.println("WARNING: " + resultado.mensajeError);
            return resultado;
        }

        System.out.println("========================================");
        System.out.println("CARGANDO PEDIDOS DESDE ARCHIVOS");
        System.out.println("Directorio: " + directorioDatos);
        System.out.println("Archivos encontrados: " + archivosPedidos.length);
        if (horaInicioSimulacion != null && horaFinSimulacion != null) {
            System.out.println("Ventana de tiempo: " + horaInicioSimulacion + " a " + horaFinSimulacion);
        } else {
            System.out.println("Ventana de tiempo: TODOS LOS PEDIDOS (sin filtrado)");
        }
        System.out.println("========================================");

        LocalDateTime tiempoInicio = LocalDateTime.now();


        // Procesar cada archivo
        for (File archivo : archivosPedidos) {
            String nombreArchivo = archivo.getName();
            // Extraer código de aeropuerto del nombre
            // Ejemplo: _pedidos_SPIM_ -> SPIM o _pedidos_EBCI_.txt -> EBCI
             String codigoOrigen = nombreArchivo
                     .replace("_envios_", "")
                     .replace(".txt", "")
                     .replace("_", "")
                     .trim()
                     .toUpperCase();

            Aeropuerto aeropuertoOrigen = mapaAeropuertos.get(codigoOrigen);
             if (aeropuertoOrigen == null) {
                 System.err.println("  ⚠️ SALTANDO ARCHIVO: Aeropuerto origen no existe en tu BD: " + codigoOrigen);
                 resultado.erroresArchivos++;
                 continue;
            }

            System.out.println("\nProcesando archivo: " + nombreArchivo);

            try {
                procesarArchivoPedidos(archivo, horaInicioSimulacion, horaFinSimulacion, resultado, aeropuertoOrigen);
            } catch (Exception e) {
                System.err.println("ERROR procesando archivo " + nombreArchivo + ": " + e.getMessage());
                e.printStackTrace();
                resultado.erroresArchivos++;
            }
        }

        resultado.tiempoFin = LocalDateTime.now();
        resultado.duracionSegundos = ChronoUnit.SECONDS.between(tiempoInicio, resultado.tiempoFin);
        resultado.exito = resultado.erroresArchivos == 0 && resultado.pedidosCargados > 0;

        System.out.println("\n========================================");
        System.out.println("RESUMEN DE CARGA DE PEDIDOS");
        System.out.println("Total de pedidos cargados: " + resultado.pedidosCargados);
        System.out.println("Total de pedidos creados: " + resultado.pedidosCreados);
        System.out.println("Pedidos filtrados (fuera de ventana): " + resultado.pedidosFiltrados);
        System.out.println("Errores de parseo: " + resultado.erroresParseo);
        System.out.println("Errores de archivos: " + resultado.erroresArchivos);
        System.out.println("Duración: " + resultado.duracionSegundos + " segundos");
        System.out.println("========================================");

        return resultado;
    }

    private void procesarArchivoPedidos(
            File archivo,
            LocalDateTime horaInicio,
            LocalDateTime horaFin,
            ResultadoCargaPedidos resultado,
            Aeropuerto aeropuertoOrigen) throws IOException {
        List<Pedido> pedidosDelArchivo = new ArrayList<>();
        //int i = 1;
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int numeroLinea = 0;
            int contador = 0; ////////////////por memoria
            while ((linea = reader.readLine() ) != null && contador < 10000) {
                numeroLinea++;
                linea = linea.trim();

                if (linea.isEmpty()) {
                    continue;
                }

                try {
                    Pedido pedido = parsearLineaPedido(linea,aeropuertoOrigen);
                    contador++; ////////////////por memoria
                    resultado.pedidosCargados++;
                    //System.out.println("  ➕ Pedido parseado: " + i);
                    // Filtrar por ventana de tiempo si se especificó
                    if (horaInicio != null || horaFin != null) {
                        boolean fueraDeVentana = false;

                        // Si solo hay horaInicio, filtrar pedidos antes de esa hora
                        if (horaInicio != null && horaFin == null) {
                            if (pedido.getFechaPedido().isBefore(horaInicio)) {
                                fueraDeVentana = true;
                            }
                        }
                        // Si solo hay horaFin, filtrar pedidos después de esa hora
                        else if (horaInicio == null && horaFin != null) {
                            if (pedido.getFechaPedido().isAfter(horaFin)) {
                                fueraDeVentana = true;
                            }
                        }
                        // Si hay ambos, filtrar pedidos fuera del rango
                        else if (horaInicio != null && horaFin != null) {
                            if (pedido.getFechaPedido().isBefore(horaInicio) ||
                                    pedido.getFechaPedido().isAfter(horaFin)) {
                                fueraDeVentana = true;
                            }
                        }

                        if (fueraDeVentana) {
                            resultado.pedidosFiltrados++;
                            continue;
                        }
                    }

                    pedidosDelArchivo.add(pedido);
                } catch (Exception e) {
                    resultado.erroresParseo++;
                    System.err.println("Error parseando línea " + numeroLinea + ": " + e.getMessage());
                }
            }

            this.listaMaestraPedidos.addAll(pedidosDelArchivo); //vamos a revisar
            resultado.pedidosCreados += pedidosDelArchivo.size();
            System.out.println("  Líneas procesadas: " + numeroLinea);
            System.out.println("  Pedidos listos en memoria: " + pedidosDelArchivo.size());
        }
    }

    /**
     * Parsea una línea del archivo en formato V2
     * Formato: id_pedido-aaaammdd-hh-mm-dest-###-IdClien
     * Ejemplo: 000000001-20250102-01-18-SPIM-003-0027081
     */
    private Pedido parsearLineaPedido(String linea,Aeropuerto aeropuertoOrigen) {

        String[] partes = linea.split("-");
        if (partes.length != 7) {
            System.err.println("  ❌ [NORMAL] Formato inválido: esperado 7 campos, encontrado " + partes.length);
            throw new IllegalArgumentException("Formato inválido: esperado 7 campos, encontrado " + partes.length);
        }

        // Parsear campos
        String idPedidoStr = partes[0];
        String fechaStr = partes[1];  // aaaammdd
        int hora = Integer.parseInt(partes[2]);
        int minuto = Integer.parseInt(partes[3]);
        String codigoAeropuertoDestino = partes[4].trim().toUpperCase();
        int cantidadMaletas = Integer.parseInt(partes[5]);
        String idClienteStr = partes[6];

        // Parsear fecha (aaaammdd -> LocalDateTime)
        int anio = Integer.parseInt(fechaStr.substring(0, 4));
        int mes = Integer.parseInt(fechaStr.substring(4, 6));
        int dia = Integer.parseInt(fechaStr.substring(6, 8));
        LocalDateTime fechaPedido = LocalDateTime.of(anio, mes, dia, hora, minuto, 0);
        //System.out.println("     - Fecha parseada: " + fechaPedido);

        // Buscar aeropuerto destino
        Aeropuerto aeropuertoDestino = mapaAeropuertos.get(codigoAeropuertoDestino);
        if (aeropuertoDestino == null) {
            throw new IllegalArgumentException("Aeropuerto destino desconocido: " + codigoAeropuertoDestino);
        }

        // Calcular plazo de entrega
        // TODO: Implementar lógica basada en continentes como en el Backend
        boolean mismoContinente = aeropuertoOrigen.getCiudad().getContinente() == aeropuertoDestino.getCiudad().getContinente();
        int diasPlazo = mismoContinente ? 1 : 2;
        LocalDateTime fechaLimiteEntrega = fechaPedido.plusDays(diasPlazo);

        // Obtener o crear cliente
        Cliente cliente = obtenerOCrearCliente(idClienteStr, aeropuertoDestino.getCiudad());

        // Crear pedido
        Pedido pedido = new Pedido();

        // Generar externalId compuesto: {AIRPORT_ORIGIN}-{FILE_ORDER_ID}
        String externalId = aeropuertoOrigen.getCodigoIATA() + "-" + idPedidoStr + "-0";
        pedido.setExternalId(externalId);
        //System.out.println("     - ExternalId generado: " + externalId);

        pedido.setNombre("PEDIDO-" + idPedidoStr + "-" + codigoAeropuertoDestino);
        pedido.setCliente(cliente);
        pedido.setAeropuertoOrigenCodigo(aeropuertoOrigen.getCodigoIATA());
        pedido.setAeropuertoDestinoCodigo(aeropuertoDestino.getCodigoIATA());
        pedido.setFechaPedido(fechaPedido);
        pedido.setFechaLimiteEntrega(fechaLimiteEntrega);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        // Calcular prioridad
        double prioridad = calcularPrioridad(fechaPedido, fechaLimiteEntrega);
        pedido.setPrioridad(prioridad);

        // ⚠️ CRÍTICO: Sincronizar cantidadProductos//maletas ANTES de crear productos
        //pedido.setCantidadProductos(cantidadProductos);

        // Crear Maletas
        ArrayList<Maleta> maletas = crearMaletas(cantidadMaletas, pedido);
        pedido.setMaletas(maletas);
       // pedido.setTipoData(0);
        return pedido;
    }
    private ArrayList<Maleta> crearMaletas(int cantidad, Pedido pedido) {
        ArrayList<Maleta> productos = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            Maleta maleta = new Maleta();
            maleta.setNombre("Maleta-" + (i + 1));
            maleta.setEstado(EstadoMaleta.EN_ALMACEN);
            maleta.setPedido(pedido);
            productos.add(maleta);
        }
        return productos;
    }

    private Cliente obtenerOCrearCliente(String idClienteStr, Ciudad ciudadRecojo) {
        long idCliente = Long.parseLong(idClienteStr);
        // ✅ CACHE FIRST: Verificar caché primero (evita query a BD)
        if (cacheClientes.containsKey(idCliente)) {
            return cacheClientes.get(idCliente);
        }
        // ⚠️ Cliente NO está en caché ni en BD, crear nueva instancia en memoria
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setId(idCliente);
        // 🚀 OPTIMIZACIÓN: Agregar a lista de pendientes en lugar de insertar inmediatamente
        // Se guardarán todos juntos en batch antes de guardar los pedidos
        // ✅ Guardar en caché inmediatamente (aunque no tenga ID de BD aún)
        // Esto permite que los pedidos referencien la misma instancia
        cacheClientes.put(idCliente, nuevoCliente);

        return nuevoCliente;
    }

    private double calcularPrioridad(LocalDateTime fechaPedido, LocalDateTime plazoEntrega) {
        long horas = ChronoUnit.HOURS.between(fechaPedido, plazoEntrega);
        // Reglas estrictas de Tasf.B2B
        if (horas <= 24) {
            // Pedidos del mismo continente tienen el menor tiempo, por ende MÁXIMA prioridad
            return 1.0;
        } else if (horas <= 48) {
            // Pedidos intercontinentales tienen más holgura (prioridad alta, pero no crítica)
            return 0.75;
        } else {
            // ⚠️ Red de seguridad: Según el caso esto no debería pasar nunca,
            // pero si un dato llega mal en el TXT, le bajamos la prioridad.
            System.err.println("⚠️ Advertencia: Se detectó un pedido con plazo mayor a 48h.");
            return 0.5;
        }
    }
}
