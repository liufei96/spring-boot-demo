package com.yiyang.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yiyang.demo.model.DocBean;
import com.yiyang.demo.service.IElasticService;
import com.yiyang.demo.utils.ObjectToMapUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class IElasticServiceImpl implements IElasticService {

    @Resource
    private RestHighLevelClient client;

    private static final String NBA_INDEX = "record_traffic-2021-01-21";

    /**
     * 新增操作
     * @param docBean
     * @throws IOException
     */
    @Override
    public void save(DocBean docBean) throws IOException {
        IndexRequest request = new IndexRequest(NBA_INDEX).id(String.valueOf(docBean.getId())).source(ObjectToMapUtils.beanToMap(docBean));
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));
    }
}
