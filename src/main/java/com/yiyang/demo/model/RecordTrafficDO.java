package com.yiyang.demo.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class RecordTrafficDO {

    private Long id;

    private String name;

    private Long appId;

    private String appName;

    private Long envId;

    private String envName;

    private String request;

    private String response;

    private String wrapperTraffic;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
}
