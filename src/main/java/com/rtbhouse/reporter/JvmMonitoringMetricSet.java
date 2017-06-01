package com.rtbhouse.reporter;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

public class JvmMonitoringMetricSet implements MetricSet {

    private BufferPoolMetricSet bufferPoolMetricSet;
    private GarbageCollectorMetricSet garbageCollectorMetricSet;
    private MemoryUsageGaugeSet memoryUsageGaugeSet;
    private FileDescriptorRatioGauge fileDescriptorRatioGauge;
    private ThreadStatesGaugeSet threadStatesGaugeSet;

    public JvmMonitoringMetricSet() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        bufferPoolMetricSet = new BufferPoolMetricSet(mBeanServer);
        garbageCollectorMetricSet = new GarbageCollectorMetricSet();
        memoryUsageGaugeSet = new MemoryUsageGaugeSet();
        fileDescriptorRatioGauge = new FileDescriptorRatioGauge();
        threadStatesGaugeSet = new ThreadStatesGaugeSet();
    }

    @Override
    public Map<String, Metric> getMetrics() {
        Map<String, Metric> map = new HashMap<String, Metric>();
        map.put("bufferPool", bufferPoolMetricSet);
        map.put("gc", garbageCollectorMetricSet);
        map.put("memory", memoryUsageGaugeSet);
        map.put("files", fileDescriptorRatioGauge);
        map.put("threads", threadStatesGaugeSet);
        return map;
    }

}
