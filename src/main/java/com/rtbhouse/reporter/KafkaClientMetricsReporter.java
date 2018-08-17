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

    private GraphiteConfig config;

    private MetricRegistry metricRegistry;
    private GraphiteReporter graphiteReporter;

    private boolean isInitialized = false;

    @Override
    public synchronized void configure(Map<String, ?> configs) {
        config = new GraphiteConfig(configs);
        metricRegistry = new MetricRegistry();
        graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith(config.getString(GraphiteConfig.REPORTER_GRAPHITE_DOMAIN))
                .convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(new Graphite(new InetSocketAddress(
                        config.getString(GraphiteConfig.REPORTER_GRAPHITE_HOST),
                        config.getInt(GraphiteConfig.REPORTER_GRAPHITE_PORT))));
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
        StringBuilder sb = new StringBuilder(config.getString(GraphiteConfig.REPORTER_GRAPHITE_PREFIX) + ".");
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
