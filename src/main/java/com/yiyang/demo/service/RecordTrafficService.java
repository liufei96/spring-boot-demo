package com.yiyang.demo.service;

import com.yiyang.demo.model.RecordTrafficDO;
import com.yiyang.demo.model.User;
import lombok.Data;

import java.util.Date;


public interface RecordTrafficService {

    void save(RecordTrafficDO recordTrafficDO);
}
