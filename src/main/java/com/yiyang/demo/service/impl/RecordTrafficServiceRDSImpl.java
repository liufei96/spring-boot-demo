package com.yiyang.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yiyang.demo.mapper.RecordTrafficMapper;
import com.yiyang.demo.model.RecordTrafficDO;
import com.yiyang.demo.service.RecordTrafficService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("rds")
@Primary
public class RecordTrafficServiceRDSImpl implements RecordTrafficService {

    @Resource
    private RecordTrafficMapper recordTrafficMapper;

    @Override
    public void save(RecordTrafficDO recordTrafficDO) {
        recordTrafficMapper.insert(recordTrafficDO);
    }

    @Override
    public JSONObject findByConditionPage(String startRow, String stopRow, String objKey, Integer page, Integer size) {
        return null;
    }

    @Override
    public int count() {
        return recordTrafficMapper.count();
    }


}
