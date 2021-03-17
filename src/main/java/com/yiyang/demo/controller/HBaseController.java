package com.yiyang.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.yiyang.demo.model.DocBean;
import com.yiyang.demo.service.RecordTrafficService;
import com.yiyang.demo.utils.HBaseUtils;
import org.jcodings.util.Hash;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/hbase")
public class HBaseController {

    @Resource
    private HBaseUtils hBaseUtils;

    @Resource(name = "hbase")
    private RecordTrafficService recordTrafficService;


    private String table = "record_traffic";

    private String[] columnFamily = new String[]{"record_info"};

    @PostMapping
    public void save(@RequestParam(defaultValue = "100") int size) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < size; i++) {
            String uuid = UUID.randomUUID().toString();
            DocBean docBean = new DocBean(uuid, "zs_" + UUID.randomUUID().toString().substring(0, 8), "first", "second", 1);
            docBean.setCreatedAt(null);
            Map<String, String> mapString = new HashMap<>();
            Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(docBean), Map.class);
            Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                mapString.put(next.getKey(), String.valueOf(next.getValue()));
            }
            Map<String, Map<String, String>> mapMap = new HashMap<>();
            mapMap.put("record_info", mapString);
            hBaseUtils.save(table, uuid, mapMap);
        }
        stopWatch.stop();
        System.out.println("单个插入耗时：" + stopWatch.getTotalTimeMillis());
    }

    @PostMapping("/batchSave")
    public void saveBatch(@RequestParam(defaultValue = "100") int size) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Map<String, Map<String, String>>> mapList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String uuid = UUID.randomUUID().toString();
            String rowKey = Long.MAX_VALUE - System.currentTimeMillis() + "_" + uuid.substring(0,8);
            DocBean docBean = new DocBean(rowKey, "zs_" + UUID.randomUUID().toString().substring(0, 8), "first", "second", 1);
            docBean.setCreatedAt(null);
            Map<String, String> mapString = new HashMap<>();
            Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(docBean), Map.class);
            Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                mapString.put(next.getKey(), String.valueOf(next.getValue()));
            }
            Map<String, Map<String, String>> mapMap = new HashMap<>();
            mapMap.put("record_info", mapString);
            mapList.add(mapMap);
        }
        hBaseUtils.batchSave(table, mapList);
        stopWatch.stop();
        System.out.println("单个插入耗时：" + stopWatch.getTotalTimeMillis());
    }

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
