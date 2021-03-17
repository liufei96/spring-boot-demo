package com.yiyang.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yiyang.demo.model.RecordTrafficDO;
import com.yiyang.demo.service.RecordTrafficService;
import com.yiyang.demo.utils.HBaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("hbase")
public class RecordTrafficServiceHBASEImpl implements RecordTrafficService {

    private static final Logger logger = LoggerFactory.getLogger(HBaseUtils.class);

    private String tableName = "record_traffic";

    private String[] smallField = {"id", "name", "appId", "appName", "envId", "envName", "createdAt", "updatedAt"};
    private String[] largeField = {"request", "response", "wrapperTraffic"};


    private String[] columnFamily = {"small_field", "large_field"};

    @Resource
    private HBaseUtils hBaseUtils;

    @Override
    public void save(RecordTrafficDO recordTrafficDO) {

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(recordTrafficDO));
        String rowId = recordTrafficDO.getAppId() + "_" + System.currentTimeMillis();
        jsonObject.put("id", rowId);
        List<String> smallValues = new ArrayList<>(smallField.length);
        for (String field : smallField) {
            smallValues.add(jsonObject.getString(field) == null ? "" : jsonObject.getString(field));
        }

        List<String> largeValues = new ArrayList<>(largeField.length);
        for (String field : largeField) {
            largeValues.add(jsonObject.getString(field) == null ? "" : jsonObject.getString(field));
        }
        try {
            hBaseUtils.insertRecords(tableName, rowId, columnFamily[0], smallField, smallValues.toArray(new String[0]));
            hBaseUtils.insertRecords(tableName, rowId, columnFamily[1], largeField, largeValues.toArray(new String[0]));
            logger.info("hbase添加数据成功，rowId：{}", rowId);
        } catch (IOException e) {
            logger.error("hbase添加数据失败： {}", e.getMessage());
        }
    }

    @Override
    public JSONObject findByConditionPage(String startRow, String stopRow, String objKey, Integer page, Integer size) {
        try {
            return hBaseUtils.findByConditionPage(tableName, startRow, stopRow, objKey, page, size);
        } catch (IOException e) {
            logger.error("hbase分页查询失败： {}", e.getMessage());
        }
        return new JSONObject();
    }

    @Override
    public int count() {
        return 0;
    }
}
