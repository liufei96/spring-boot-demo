package com.yiyang.demo.controller;

import com.yiyang.demo.model.DocBean;
import com.yiyang.demo.service.IElasticService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/es")
public class ESController {

    @Resource
    private IElasticService iElasticService;

    @PostMapping
    public void save(int count) {
        for (int i = 0; i < count; i++) {
            DocBean docBean = new DocBean(UUID.randomUUID().toString().substring(0, 8), "zs_" + UUID.randomUUID().toString().substring(0, 8), "first", "second", 1);
            try {
                iElasticService.save(docBean);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
