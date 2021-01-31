package com.yiyang.demo.model;

import lombok.Data;

import java.util.Date;

@Data
public class DocBean {

    private Integer id;

    private String firstCode;

    private String secondCode;

    private String content;

    private Integer type;

    private Date createdAt;

    private Long startAt;

    public DocBean(Integer id, String firstCode, String secondCode, String content, Integer type) {
        this.id = id;
        this.firstCode = firstCode;
        this.secondCode = secondCode;
        this.content = content;
        this.type = type;
        this.createdAt = new Date();
        this.startAt = System.currentTimeMillis();
    }

    public DocBean() {

    }
}
