package com.yiyang.demo.controller;

import com.github.pagehelper.PageInfo;
import com.yiyang.demo.model.DocBean;
import com.yiyang.demo.service.IElasticService;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/es")
public class ESController {

    @Resource
    private IElasticService iElasticService;

    @PostMapping
    public void save(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "100") int size) {
        int first = (page - 1) * size;
        for (int i = first; i < size + first; i++) {
            DocBean docBean = new DocBean(i +1, "zs_" + UUID.randomUUID().toString().substring(0, 8), "first", "second", 1);
            try {
                iElasticService.save(docBean);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping
    public PageInfo search(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            return iElasticService.search(new SearchSourceBuilder(), page ,size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageInfo();
    }

    @GetMapping("/searchAfter")
    public PageInfo searchUserSearchAfter(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            return iElasticService.searchAfter(new SearchSourceBuilder(), page ,size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageInfo();
    }
}
