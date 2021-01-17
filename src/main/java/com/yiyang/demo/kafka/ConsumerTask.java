package com.yiyang.demo.kafka;


import com.alibaba.fastjson.JSONObject;
import com.yiyang.demo.model.RecordTrafficDO;
import com.yiyang.demo.service.RecordTrafficService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ConsumerTask {

    @Resource
    private RecordTrafficService recordTrafficService;

    @Resource(name = "hbase")
    private RecordTrafficService recordTrafficServiceHbase;

    /**
     * 定义此消费者接收topics = "demo"的消息，与controller中的topic对应上即可
     *
     * @param record 变量代表消息本身，可以通过ConsumerRecord<?,?>类型的record变量来打印接收的消息的各种信息
     */
    @KafkaListener(topics = "yiyang", groupId = "yiyang_group")
    public void listen(ConsumerRecord<?, ?> record) {
//        System.out.printf("topic is %s, offset is %d, value is %s \n", record.topic(), record.offset(), record.value());
        RecordTrafficDO recordTrafficDO = JSONObject.parseObject(record.value().toString(), RecordTrafficDO.class);
        // 保存数据库
        recordTrafficService.save(recordTrafficDO);
        // 保存hbase
        recordTrafficServiceHbase.save(recordTrafficDO);
    }
}
