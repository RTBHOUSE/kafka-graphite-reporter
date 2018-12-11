package com.rtbhouse.reporter;

import static org.slf4j.LoggerFactory.getLogger;

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
        try {
            double value = (double) this.metric.metricValue();
            return Double.isNaN(value) ? 0. : value;
        } catch (ClassCastException e) {
            logger.warn("Cannot cast (return 0.0)", e);
            return 0.;
        }
    }

}
