package org.example.service;

import java.util.List;
import java.time.temporal.ChronoUnit;

public class ReporteGlobal {
    public String nombreAlgoritmo;
    public long tiempoEjecucionMs;
    public int totalPedidos;
    public int pedidosPlanificados;
    public int entregasATiempo;
    public double fitnessTotal;
    public double horasTotalesVuelo;
    public int vuelosUtilizados;
    public int capacidadTotalUtilizada;
    public int capacidadTotalDisponibleEnVuelosUsados;

    public int mejorasEncontradasTotales; // Opcional, si lo extraes del SA

    public void imprimirReporte() {
        System.out.println("\n==================================================");
        System.out.println("  📊 REPORTE DE PLANIFICACIÓN: " + nombreAlgoritmo);
        System.out.println("==================================================");
        System.out.println("⏱️ Tiempo de Ejecución : " + (tiempoEjecucionMs / 1000.0) + " segundos");
        System.out.println("🎯 Fitness Global      : " + String.format("%.2f", fitnessTotal));
        System.out.println("📦 Pedidos Planificados: " + pedidosPlanificados + " / " + totalPedidos + " (" + calcularPorcentaje(pedidosPlanificados, totalPedidos) + "%)");
        System.out.println("✅ Entregas a Tiempo   : " + entregasATiempo + " / " + pedidosPlanificados + " (" + calcularPorcentaje(entregasATiempo, pedidosPlanificados) + "%)");
        System.out.println("⏳ Tiempo Prom. Entrega: " + (pedidosPlanificados > 0 ? String.format("%.2f", (horasTotalesVuelo / pedidosPlanificados)) : 0) + " horas");
        System.out.println("✈️ Vuelos Utilizados   : " + vuelosUtilizados);
        System.out.println("🧳 Uso de Capacidad    : " + capacidadTotalUtilizada + " / " + capacidadTotalDisponibleEnVuelosUsados + " (" + calcularPorcentaje(capacidadTotalUtilizada, capacidadTotalDisponibleEnVuelosUsados) + "%)");
        System.out.println("==================================================\n");
    }

    private String calcularPorcentaje(int parte, int total) {
        if (total == 0) return "0.00";
        return String.format("%.2f", ((double) parte / total) * 100);
    }
    public String getNombreAlgoritmo() {
        return nombreAlgoritmo;
    }

    public void setNombreAlgoritmo(String nombreAlgoritmo) {
        this.nombreAlgoritmo = nombreAlgoritmo;
    }

    public long getTiempoEjecucionMs() {
        return tiempoEjecucionMs;
    }

    public void setTiempoEjecucionMs(long tiempoEjecucionMs) {
        this.tiempoEjecucionMs = tiempoEjecucionMs;
    }

    public int getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(int totalPedidos) {
        this.totalPedidos = totalPedidos;
    }

    public int getPedidosPlanificados() {
        return pedidosPlanificados;
    }

    public void setPedidosPlanificados(int pedidosPlanificados) {
        this.pedidosPlanificados = pedidosPlanificados;
    }

    public int getEntregasATiempo() {
        return entregasATiempo;
    }

    public void setEntregasATiempo(int entregasATiempo) {
        this.entregasATiempo = entregasATiempo;
    }

    public double getHorasTotalesVuelo() {
        return horasTotalesVuelo;
    }

    public void setHorasTotalesVuelo(double horasTotalesVuelo) {
        this.horasTotalesVuelo = horasTotalesVuelo;
    }

    public double getFitnessTotal() {
        return fitnessTotal;
    }

    public void setFitnessTotal(double fitnessTotal) {
        this.fitnessTotal = fitnessTotal;
    }

    public int getCapacidadTotalUtilizada() {
        return capacidadTotalUtilizada;
    }

    public void setCapacidadTotalUtilizada(int capacidadTotalUtilizada) {
        this.capacidadTotalUtilizada = capacidadTotalUtilizada;
    }

    public int getVuelosUtilizados() {
        return vuelosUtilizados;
    }

    public void setVuelosUtilizados(int vuelosUtilizados) {
        this.vuelosUtilizados = vuelosUtilizados;
    }

    public int getCapacidadTotalDisponibleEnVuelosUsados() {
        return capacidadTotalDisponibleEnVuelosUsados;
    }

    public void setCapacidadTotalDisponibleEnVuelosUsados(int capacidadTotalDisponibleEnVuelosUsados) {
        this.capacidadTotalDisponibleEnVuelosUsados = capacidadTotalDisponibleEnVuelosUsados;
    }

    public int getMejorasEncontradasTotales() {
        return mejorasEncontradasTotales;
    }

    public void setMejorasEncontradasTotales(int mejorasEncontradasTotales) {
        this.mejorasEncontradasTotales = mejorasEncontradasTotales;
    }
}
