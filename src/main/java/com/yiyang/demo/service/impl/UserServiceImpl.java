package com.yiyang.demo.service.impl;

import com.yiyang.demo.mapper.UserMapper;
import com.yiyang.demo.model.User;
import com.yiyang.demo.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;


    public void save(User user) {
        userMapper.insert(user);
    }
}
