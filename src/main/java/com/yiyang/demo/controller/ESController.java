package com.yiyang.demo.controller;

import com.github.pagehelper.PageInfo;
import com.yiyang.demo.model.DocBean;
import com.yiyang.demo.service.IElasticService;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/es")
public class ESController {

    @Resource
    private IElasticService iElasticService;

    @PostMapping
    public void save(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "100") int size) {
        long begin = System.currentTimeMillis();
        int first = (page - 1) * size;
        for (int i = first; i < size + first; i++) {
            DocBean docBean = new DocBean(String.valueOf(i + 1), "zs_" + UUID.randomUUID().toString().substring(0, 8), "first", "second", 1);
            try {
                iElasticService.save(docBean);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("一个一个添加的消耗时间：" + (end - begin));
    }

    @PostMapping("/batchSave")
    public void batchSave(@RequestParam(defaultValue = "100") int size) {
        long begin = System.currentTimeMillis();
        List<DocBean> docBeanList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            DocBean docBean = new DocBean(String.valueOf(i +1), "batch_" + UUID.randomUUID().toString().substring(0, 8), "batch first", "batch second", 1);
            docBeanList.add(docBean);
        }
        try {
            iElasticService.batchSave(docBeanList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("批量添加的消耗时间：" + (end - begin));
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

    @GetMapping("/searchAfter/query")
    public PageInfo searchUserSearchAfter(String query, String searchAfterId, @RequestParam(defaultValue = "10") int size) {
        try {
            return iElasticService.searchAfter(query, searchAfterId ,size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageInfo();
    }
}
