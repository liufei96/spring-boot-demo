package com.yiyang.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yiyang.demo.model.DocBean;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public interface IElasticService {

    void save(DocBean docBean) throws IOException;

    PageInfo<JSONObject> search(SearchSourceBuilder searchSourceBuilder, int pageNum, int pageSize) throws Exception;

    PageInfo<JSONObject> searchAfter(SearchSourceBuilder searchSourceBuilder, int pageNum, int pageSize) throws Exception;
}
