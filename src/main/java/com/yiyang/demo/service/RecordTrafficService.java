package com.yiyang.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.yiyang.demo.model.RecordTrafficDO;
import com.yiyang.demo.model.User;
import lombok.Data;

import java.util.Date;


public interface RecordTrafficService {

    void save(RecordTrafficDO recordTrafficDO);

    JSONObject findByConditionPage(String startRow, String stopRow,
                                   String objKey, Integer page, Integer size);
}
