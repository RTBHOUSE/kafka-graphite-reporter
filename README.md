Kafka Graphite Reporter
=======================

This is a simple kafka plugin reporting several metrics to graphite. It work with kafka 0.10.x.

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

	kafka.metrics.reporters=com.rtbhouse.reporter.KafkaMetricsReporter
	metric.reporters=com.rtbhouse.reporter.KafkaClientMetricsReporter

You should also configure the reporter, by setting the following properties:

	reporter.graphite.domain
	reporter.graphite.host
	reporter.graphite.port

