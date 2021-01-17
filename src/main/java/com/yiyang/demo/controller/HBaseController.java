package com.yiyang.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.yiyang.demo.service.RecordTrafficService;
import com.yiyang.demo.utils.HBaseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hbase")
public class HBaseController {

    @Resource
    private HBaseUtils hBaseUtils;

    @Resource(name = "hbase")
    private RecordTrafficService recordTrafficService;

    @GetMapping("/getByRowKey")
    public String getByRowKey(String tableName, String rowKey) {
        Map<String, String> byRowKey = hBaseUtils.getByRowKey(tableName, rowKey);
        return JSONObject.toJSONString(byRowKey);
    }

    @GetMapping("/create")
    public String create(String tableName, String[] columnFamily) {
        hBaseUtils.createTable(tableName, Arrays.asList(columnFamily));
        return "SUCCESS";
    }

    @GetMapping("/getAll")
    public List getAll(String tableName, int size) {
        try {
            return hBaseUtils.scanAllRecord(tableName, size);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList();
    }

    @GetMapping("/getDataByPage")
    public JSONObject getDataByPage(String startRow, String stopRow,
                                    String objKey, Integer page, Integer size) {
        return recordTrafficService.findByConditionPage(startRow, stopRow, objKey, page, size);
    }

    @GetMapping("/getTotal")
    public long getTotal(String tableName) {
        return hBaseUtils.getTotal(tableName);
    }
}
