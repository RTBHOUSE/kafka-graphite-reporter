package com.rtbhouse.reporter;

import org.apache.kafka.common.metrics.KafkaMetric;

import com.codahale.metrics.Gauge;

public class GaugeAdapter implements Gauge<Double> {

    private final KafkaMetric metric;

    public GaugeAdapter(KafkaMetric metric) {
        this.metric = metric;
    }

    @Override
    public Double getValue() {
        double value = this.metric.value();

        return Double.isNaN(value) ? 0. : value;
    }

}
