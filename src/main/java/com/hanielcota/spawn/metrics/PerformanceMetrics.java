package com.hanielcota.spawn.metrics;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe para medir métricas de desempenho de operações específicas.
 */
@Getter
public class PerformanceMetrics {

    private final Map<String, OperationMetrics> metrics = new ConcurrentHashMap<>();
    private final boolean isEnabled; // Nova flag para ativar/desativar métricas

    /**
     * Construtor que define se as métricas estão ativadas.
     *
     * @param isEnabled Define se o monitoramento de métricas está ativo
     */
    public PerformanceMetrics(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * Inicia a medição de uma operação.
     *
     * @param operationName Nome da operação a ser medida
     * @return O timestamp inicial em nanosegundos, ou -1 se desativado
     */
    public long startMeasurement(String operationName) {
        return isEnabled ? System.nanoTime() : -1; // Retorna -1 se desativado
    }

    /**
     * Finaliza a medição de uma operação e registra o tempo gasto, se ativado.
     *
     * @param operationName Nome da operação
     * @param startTime     Timestamp inicial retornado por startMeasurement
     */
    public void endMeasurement(String operationName, long startTime) {
        if (!isEnabled || startTime == -1) {
            return; // Não faz nada se desativado ou startTime inválido
        }

        long endTime = System.nanoTime();
        long durationNanos = endTime - startTime;
        double durationSeconds = durationNanos / 1_000_000_000.0; // Converte para segundos

        OperationMetrics operation = metrics.computeIfAbsent(operationName, k -> new OperationMetrics());
        operation.record(durationNanos);

        // Loga a medição atual em segundos
        Bukkit.getLogger().info(String.format("[SpawnPlugin] Operação '%s' levou %.6f s", operationName, durationSeconds));
    }

    /**
     * Exibe um relatório com todas as métricas coletadas, se ativado.
     */
    public void printMetricsReport() {
        if (!isEnabled) {
            return; // Não exibe relatório se desativado
        }

        Bukkit.getLogger().info("[SpawnPlugin] Relatório de Métricas de Desempenho:");
        if (metrics.isEmpty()) {
            Bukkit.getLogger().info("  Nenhuma métrica registrada.");
            return;
        }

        for (Map.Entry<String, OperationMetrics> entry : metrics.entrySet()) {
            String operationName = entry.getKey();
            OperationMetrics stats = entry.getValue();
            Bukkit.getLogger().info(String.format(
                    "  Operação: %s | Contagem: %d | Média: %.6f s | Mín: %.6f s | Máx: %.6f s",
                    operationName,
                    stats.getCount(),
                    stats.getAverageSeconds(),
                    stats.getMinSeconds(),
                    stats.getMaxSeconds()
            ));
        }
    }

    /**
     * Classe interna para armazenar métricas de uma operação específica.
     */
    private static class OperationMetrics {
        private final AtomicLong count = new AtomicLong(0);
        private final AtomicLong totalTimeNanos = new AtomicLong(0);
        private long minTimeNanos = Long.MAX_VALUE;
        private long maxTimeNanos = Long.MIN_VALUE;

        public synchronized void record(long durationNanos) {
            count.incrementAndGet();
            totalTimeNanos.addAndGet(durationNanos);
            minTimeNanos = Math.min(minTimeNanos, durationNanos);
            maxTimeNanos = Math.max(maxTimeNanos, durationNanos);
        }

        public long getCount() {
            return count.get();
        }

        public double getAverageSeconds() {
            long total = totalTimeNanos.get();
            long cnt = count.get();
            return cnt > 0 ? (total / 1_000_000_000.0) / cnt : 0.0;
        }

        public double getMinSeconds() {
            return minTimeNanos == Long.MAX_VALUE ? 0.0 : minTimeNanos / 1_000_000_000.0;
        }

        public double getMaxSeconds() {
            return maxTimeNanos == Long.MIN_VALUE ? 0.0 : maxTimeNanos / 1_000_000_000.0;
        }
    }
}