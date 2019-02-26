package com.rtbhouse.reporter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.reporting.GraphiteReporter;

import kafka.utils.VerifiableProperties;

public class KafkaMetricsReporter implements kafka.metrics.KafkaMetricsReporter {
    private final static Logger logger = LoggerFactory.getLogger(KafkaMetricsReporter.class);

    @Override
    public void init(VerifiableProperties props) {

        try {
            logger.info("Starting kafka reporter");
            GraphiteConfig config = new GraphiteConfig(props.props());
            GraphiteReporter graphiteReporter = new RTBGraphiteReporter(
                    Metrics.defaultRegistry(),
                    config.getString(GraphiteConfig.REPORTER_GRAPHITE_HOST),
                    config.getInt(GraphiteConfig.REPORTER_GRAPHITE_PORT),
                    config.getString(GraphiteConfig.REPORTER_GRAPHITE_DOMAIN));

            // disabling duplicate jvm metrics, which also cause problems with JDK9+
            graphiteReporter.printVMMetrics = false;
            graphiteReporter.start(1, TimeUnit.MINUTES);
        } catch (IOException e) {
            logger.error("Cannot start KafkaMetricsReporter.");
        }
    }

    private static class RTBGraphiteReporter extends GraphiteReporter {

        public RTBGraphiteReporter(MetricsRegistry metricsRegistry, String host, int port, String prefix)
                throws IOException {
            super(metricsRegistry, host, port, prefix);
        }

        @Override
        protected String sanitizeName(MetricName name) {
            StringBuilder resultBuilder = new StringBuilder().append(name.getGroup())
                    .append('.').append(name.getType())
                    .append('.').append(name.getName());

            String[] nameParts = name.getMBeanName().split(",");

            // the first two parts go for group:type and name
            for (int i = 2; i < nameParts.length; i++) {
                String[] splittedNamePart = nameParts[i].split("=");
                if (splittedNamePart.length > 0) {
                    String newNamePart = splittedNamePart[splittedNamePart.length - 1]
                            .replace('.', '_');
                    resultBuilder.append("." + newNamePart);
                }
            }

            return resultBuilder.toString().replace(' ', '_');
        }

    }

}
