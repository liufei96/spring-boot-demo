package com.yiyang.demo.service;

import com.yiyang.demo.model.DocBean;

import java.io.IOException;

public interface IElasticService {

    void save(DocBean docBean) throws IOException;
}
