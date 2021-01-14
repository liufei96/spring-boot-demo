package com.yiyang.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.yiyang.demo.utils.HBaseUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/hbase")
public class HBaseController {
    
    @Resource
    private HBaseUtils hBaseUtils;
    
    @GetMapping("/getByRowKey")
    public String getByRowKey(String tableName, String rowKey) {
        Map<String, String> byRowKey = hBaseUtils.getByRowKey(tableName, rowKey);
        return JSONObject.toJSONString(byRowKey);
    }
}
