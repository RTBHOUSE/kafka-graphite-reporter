package com.rtbhouse.reporter;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.MetricsReporter;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public class KafkaClientMetricsReporter implements MetricsReporter {

    private static final Logger logger = getLogger(KafkaClientMetricsReporter.class);

    private GraphiteConfig config;

    private MetricRegistry metricRegistry;
    private GraphiteReporter graphiteReporter;

    private boolean isInitialized = false;

    @Override
    public synchronized void configure(Map<String, ?> configs) {
        config = new GraphiteConfig(configs);
        metricRegistry = new MetricRegistry();
        graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith(getDomainPrefix())
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
        if (hasNumberValue(metric)) {
            metricRegistry.register(getMetricName(metric), new GaugeDouble(metric));
        } else {
            logger.warn("{} of value type [{}] cannot be registered as Gauge<Double> (skip metric registration)",
                    metric.metricName(),
                    metric.metricValue().getClass().getName());
        }
    }

    private boolean hasNumberValue(KafkaMetric metric) {
        return metric.metricValue() instanceof Number;
    }

    private void removeMetric(KafkaMetric metric) {
        String metricName = getMetricName(metric);
        if (metricRegistry.getMetrics().containsKey(metricName)) {
            metricRegistry.remove(metricName);
        }
    }

    private String getDomainPrefix() {
        String prefix = config.getString(GraphiteConfig.REPORTER_GRAPHITE_DOMAIN);
        if (config.getBoolean(GraphiteConfig.REPORTER_GRAPHITE_USE_HOST)) {
            prefix += "." + HostNameHelper.getShortHostName();
        }
        return prefix;
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
