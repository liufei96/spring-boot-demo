package com.yiyang.demo.task;

import com.yiyang.demo.config.TrafficMetrics;
import com.yiyang.demo.mapper.RecordTrafficMapper;
import com.yiyang.demo.service.RecordTrafficService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
@EnableScheduling
public class TrafficMonitorTask {

    @Resource
    private TrafficMetrics trafficMetrics;

    @Resource
    private RecordTrafficService recordTrafficService;

    @Resource
    private RecordTrafficMapper recordTrafficMapper;


    @Async("main")
    @Scheduled(fixedDelay = 10000)
    public void task() {
        int count = recordTrafficService.count();
        TrafficMetrics.atomicInteger.set(count);
        System.out.println("rds的数量:" + trafficMetrics.rdsTotalCount.value());
        // size
        long size = recordTrafficMapper.size();
        TrafficMetrics.size.set(size);
        System.out.println("rds的大小:" + trafficMetrics.size.get());

        // avgSize
        BigDecimal bigDecimal = new BigDecimal(size * 1D / count);
        double avgSize = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        TrafficMetrics.avgSize.set(avgSize);
        System.out.println("rds的平均流量大小:" + trafficMetrics.avgSize.get());

        trafficMetrics.rdsTest.increment(10);
        System.out.println("rds测试:" + trafficMetrics.rdsTest.count());
    }
}
