Kafka Graphite Reporter
=======================

This is a simple kafka plugin reporting several metrics to graphite. It works with kafka 1.0.0.

There are several similar tools present on the market, but metrics reported by this plugin
are in line with metrics exposed by kafka through jmx. This feature makes transition from
tools like [jmxtrans](https://github.com/jmxtrans/jmxtrans) easier.

Installation On Broker
------------

1. Build the `kafka-graphite-reporter-1.*.jar` using `mvn package`.
2. Add `kafka-graphite-reporter-1.*.jar` to the `libs/` directory of your kafka broker installation
3. Add all dependencies of the kafka-graphite-reporter to the `libs/` directory of your kafka broker installation (some of them may be already present there)
4. Configure the broker (see the configuration section below)
5. Restart the broker

Configuration
------------

To activate the reporter add the following entries to the 'server.properties' file of you broker installation:

	metric.reporters=com.rtbhouse.reporter.KafkaClientMetricsReporter
	kafka.metrics.reporters=com.rtbhouse.reporter.KafkaMetricsReporter

The first one, metric.reporters setting is describe in kafka docs in [broker configs](https://kafka.apache.org/documentation/#brokerconfigs) section. The same reporter could be used not only for brokers but also for consumers and producers. The second one, kafka.metrics.reporters setting is not exposed directly in kafka docs but is used in [KafkaMetricsConfig](https://github.com/apache/kafka/blob/trunk/core/src/main/scala/kafka/metrics/KafkaMetricsConfig.scala) class.

You should also configure the reporter, by setting the following properties:

	reporter.graphite.host
	reporter.graphite.port

You could also set optional properties:

	reporter.graphite.domain     # default is "metrics"
	reporter.graphite.prefix     # default is "kafka.server"
	reporter.graphite.use.host   # default is false
