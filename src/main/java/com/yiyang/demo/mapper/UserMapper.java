package com.yiyang.demo.mapper;

import com.yiyang.demo.model.User;
import org.apache.ibatis.annotations.Insert;

public interface UserMapper {

    @Insert("INSERT INTO user(username, remark) VALUES(#{username}, #{remark})")
    void insert(User user);
}
