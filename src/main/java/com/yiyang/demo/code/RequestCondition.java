package com.yiyang.demo.code;

import lombok.Data;

import java.util.Map;

@Data
public class RequestCondition {

    private Map<String, String> equalsCondition;
    private Map<String, String> likeCondition;
    private Map<String, String> inCondition;
    private String orderBy;
    private Boolean asc = false;
}
