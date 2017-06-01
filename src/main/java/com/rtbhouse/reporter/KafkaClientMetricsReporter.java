package com.rtbhouse.reporter;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.MetricsReporter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

public class KafkaClientMetricsReporter implements MetricsReporter {

    private static final String PREFIX = "kafka.server";

    private GraphiteReporter graphiteReporter;
    private boolean isInitialized = false;
    private MetricRegistry metricRegistry;

    @Override
    public synchronized void configure(Map<String, ?> configs) {
        String graphiteDomain = getOrDefault(configs, ConfigurationConsts.REPORTER_GRAPHITE_DOMAIN, "metrics");
        String graphitHost = getOrDefault(configs, ConfigurationConsts.REPORTER_GRAPHITE_HOST, "");
        Integer graphitePort = Integer
                .valueOf(getOrDefault(configs, ConfigurationConsts.REPORTER_GRAPHITE_PORT, "2003"));
        buildMetricsReporter(graphiteDomain, graphitHost, graphitePort);
    }

    private <T> T getOrDefault(Map<String, ?> configs, String key, T defaultValue) {
        Object value = configs.get(key);
        return value != null ? (T) value : defaultValue;
    }

    private void buildMetricsReporter(String graphiteDomain, String graphitHost, Integer graphitePort) {
        metricRegistry = new MetricRegistry();
        Graphite graphite = new Graphite(new InetSocketAddress(graphitHost, graphitePort));
        graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith(graphiteDomain)
                .convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(graphite);
    }

    @Override
    public synchronized void init(List<KafkaMetric> metrics) {
        if (!isInitialized) {
            for (KafkaMetric metric : metrics) {
                updateMetric(metric);
            }
            metricRegistry.register("jvm", new JvmMonitoringMetricSet());
            graphiteReporter.start(1, TimeUnit.MINUTES);
            isInitialized = true;
        }
    }

    @Override
    public synchronized void metricChange(KafkaMetric metric) {
        updateMetric(metric);
    }

    @Override
    public synchronized void metricRemoval(KafkaMetric metric) {
        removeMetric(metric);
    }

    @Override
    public synchronized void close() {
        graphiteReporter.close();
    }

    private void updateMetric(KafkaMetric metric) {
        removeMetric(metric);
        metricRegistry.register(getMetricName(metric), new GaugeAdapter(metric));
    }

    private void removeMetric(KafkaMetric metric) {
        String metricName = getMetricName(metric);
        if (metricRegistry.getMetrics().containsKey(metricName)) {
            metricRegistry.remove(metricName);
        }
    }

    private String getMetricName(KafkaMetric metric) {
        StringBuilder sb = new StringBuilder(PREFIX + ".");
        sb.append(metric.metricName().group());
        for (Map.Entry<String, String> entry : metric.metricName().tags().entrySet()) {
            if (entry.getKey().length() > 0 || entry.getValue().length() > 0) {
                sb.append("." + entry.getValue());
            }
        }
        sb.append("." + metric.metricName().name());
        return sb.toString();
    }

}
