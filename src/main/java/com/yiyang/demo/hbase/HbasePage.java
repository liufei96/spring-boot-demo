package com.yiyang.demo.hbase;

import lombok.Data;

@Data
public class HbasePage {
    /**
     * 分页大小
     */
    private int pageSize;
    /**
     * 开始key
     */
    private String startKey;

    /**
     * 结束key
     */
    private String stopKey;

    /**
     * 表名
     */
    private String tableName;

}
