package com.yiyang.demo.controller;

import com.yiyang.demo.model.User;
import com.yiyang.demo.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping
    public String save(@RequestBody User user) {
        userService.save(user);
        return "success";
    }
}
