package com.yiyang.demo.mapper;

import com.yiyang.demo.model.RecordTrafficDO;
import org.apache.ibatis.annotations.Insert;

public interface RecordTrafficMapper {

    @Insert("INSERT INTO record_traffic(name,app_id,app_name,env_id,env_name,request,response,wrapper_traffic) " +
            "VALUES(#{name}, #{appId}, #{appName}, #{envId}, #{envName}, #{request}, #{response}, #{wrapperTraffic})")
    void insert(RecordTrafficDO recordTrafficDO);
}
