package com.rtbhouse.reporter;

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

public class GraphiteConfig extends AbstractConfig {

    public static final String REPORTER_GRAPHITE_PORT = "reporter.graphite.port";
    private static final String REPORTER_GRAPHITE_PORT_DOC = "Graphite reporter port number";
    private static final int REPORTER_GRAPHITE_PORT_DEFAULT = 2003;

    public static final String REPORTER_GRAPHITE_HOST = "reporter.graphite.host";
    private static final String REPORTER_GRAPHITE_HOST_DOC = "Graphite reporter host name";

    public static final String REPORTER_GRAPHITE_DOMAIN = "reporter.graphite.domain";
    private static final String REPORTER_GRAPHITE_DOMAIN_DOC = "Graphite reporter root path";
    private static final String REPORTER_GRAPHITE_DOMAIN_DEFAULT = "metrics";

    public static final String REPORTER_GRAPHITE_PREFIX = "reporter.graphite.prefix";
    private static final String REPORTER_GRAPHITE_PREFIX_DOC = "Prefix added to all metric names";
    private static final String REPORTER_GRAPHITE_PREFIX_DEFAULT = "kafka.server";

    private static final ConfigDef CONFIG;

    static {
        CONFIG = new ConfigDef()
                .define(REPORTER_GRAPHITE_PORT,
                        Type.INT,
                        REPORTER_GRAPHITE_PORT_DEFAULT,
                        Importance.MEDIUM,
                        REPORTER_GRAPHITE_PORT_DOC)
                .define(REPORTER_GRAPHITE_HOST,
                        Type.STRING,
                        Importance.HIGH,
                        REPORTER_GRAPHITE_HOST_DOC)
                .define(REPORTER_GRAPHITE_DOMAIN,
                        Type.STRING,
                        REPORTER_GRAPHITE_DOMAIN_DEFAULT,
                        Importance.MEDIUM,
                        REPORTER_GRAPHITE_DOMAIN_DOC)
                .define(REPORTER_GRAPHITE_PREFIX,
                        Type.STRING,
                        REPORTER_GRAPHITE_PREFIX_DEFAULT,
                        Importance.MEDIUM,
                        REPORTER_GRAPHITE_PREFIX_DOC);
    }

    public GraphiteConfig(final Map<?, ?> props) {
        super(CONFIG, props);
    }

}
