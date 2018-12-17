package com.rtbhouse.reporter;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.apache.kafka.common.metrics.KafkaMetric;
import org.slf4j.Logger;

import com.codahale.metrics.Gauge;

public class GaugeAdapter implements Gauge<Double> {

    private static final Logger logger = getLogger(GaugeAdapter.class);

    private final KafkaMetric metric;

    public GaugeAdapter(KafkaMetric metric) {
        this.metric = metric;
    }

    @Override
    public Double getValue() {
        Object metricValue = null;
        try {
            metricValue = metric.metricValue();
            double value = (double) Optional.ofNullable(metricValue).orElse(0.0);
            return Double.isNaN(value) ? 0.0 : value;
        } catch (ClassCastException e) {
            logger.warn("Cannot cast metric [{}] value [{}] (return 0.0)", metric.metricName(),  metricValue, e);
            return 0.0;
        }
    }

}
