package com.yiyang.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.yiyang.demo.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("/kafka")
public class ProducerController {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("/send")
    public String send(String msg) {
        kafkaTemplate.send("yiyang", msg); //使用kafka模板发送信息
        return "success";
    }

    @GetMapping("/send/user")
    public String sendUser(@RequestParam(defaultValue = "10") Integer count) {
        for (int i = 0; i < count ; i++) {
            User user = new User();
            user.setId(i + 1l);
            user.setUsername("liufei_" + UUID.randomUUID().toString().substring(0, 8));
            user.setRemark("这是备注：" + UUID.randomUUID().toString());
            String userStr = JSONObject.toJSONString(user);
            kafkaTemplate.send("yiyang", userStr); //使用kafka模板发送信息
        }
        return "success";
    }
}
