package com.yiyang.demo.config;

import com.yiyang.demo.utils.HBaseUtils;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = HbaseConfig.CONF_PREFIX)
public class HbaseConfig {

    public static final String CONF_PREFIX = "hbase.conf";

    private Map<String,String> confMaps;

    public Map<String, String> getConfMaps() {
        return confMaps;
    }
    public void setConfMaps(Map<String, String> confMaps) {
        this.confMaps = confMaps;
    }

    @Bean
    public HBaseUtils instance() {
        org.apache.hadoop.conf.Configuration config = HBaseConfiguration.create();
        //将hbase配置类中定义的配置加载到连接池中每个连接里
        Map<String, String> confMap = getConfMaps();
        for (Map.Entry<String,String> confEntry : confMap.entrySet()) {
            config.set(confEntry.getKey(), confEntry.getValue());
        }
        return new HBaseUtils(config);
    }
}
