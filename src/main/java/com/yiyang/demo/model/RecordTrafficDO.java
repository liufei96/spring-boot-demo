package com.yiyang.demo.model;

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

    private Date createdAt;

    private Date updatedAt;
}
