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

    public Counter job1Counter;
    public Counter job2Counter;
    public Counter job3Counter;
    public Counter job4Counter;

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

        this.job1Counter = Counter.builder("size_lt_1_mb")
                .tags(new String[]{"name", "size < 1mb"})
                .description("size_lt_1_mb execute count").register(meterRegistry);

        this.job2Counter = Counter.builder("size_lt_5_mb")
                .tags(new String[]{"name", "1mb <= size < 5mb"})
                .description("1mb <= size < 5mb counter2 execute count ").register(meterRegistry);

        this.job3Counter = Counter.builder("size_lt_10_mb")
                .tags(new String[]{"name", "5mb <= size < 10mb"})
                .description("size_lt_10_mb counter2 execute count ").register(meterRegistry);

        this.job4Counter = Counter.builder("size_gt_10_mb")
                .tags(new String[]{"name", "size >= 10mb"})
                .description("size_gt_10_mb execute count ").register(meterRegistry);
    }

    public void addSizeCount(long length) {
        if (length >= 1024 * 1024 * 10) {
            job4Counter.increment();
        } else if (length >= 1024 * 1024 * 5) {
            job3Counter.increment();
        } else if (length >= 1024 * 1024 * 1) {
            job2Counter.increment();
        } else {
            job1Counter.increment();
        }
    }
}
