package com.yiyang.demo.config;

import com.google.common.util.concurrent.AtomicDouble;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class TrafficMetrics implements MeterBinder {

    public static AtomicInteger atomicInteger = new AtomicInteger(0);
    public Gauge rdsTotalCount;

    public static AtomicLong size = new AtomicLong(0);
    public Gauge rdsTotalSize;

    public static AtomicDouble avgSize = new AtomicDouble(0.00);
    public Gauge rdsAvgSize;

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        this.rdsTotalCount = Gauge.builder("rds_total_count", atomicInteger, c -> atomicInteger.get())
                .tags(new String[]{"name", "rds_total_count"})
                .description("rds_total_count execute count").register(meterRegistry);

        this.rdsTotalSize = Gauge.builder("rds_total_size", size, c -> size.get())
                .tags(new String[]{"name", "rds_total_size"})
                .description("rds_total_size execute size").register(meterRegistry);

        this.rdsAvgSize = Gauge.builder("rds_avg_size", avgSize, c -> avgSize.get())
                .tags(new String[]{"name", "rds_avg_size"})
                .description("rds_avg_size execute size").register(meterRegistry);
    }
}
