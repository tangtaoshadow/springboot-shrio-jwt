package com.tangtao.springbootshirojwt.controller;

import com.tangtao.springbootshirojwt.mapper.UserMapper;
import com.tangtao.springbootshirojwt.model.ResultMap;
import com.tangtao.springbootshirojwt.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final UserMapper userMapper;
    private final ResultMap resultMap;

    @Autowired
    public LoginController(UserMapper userMapper, ResultMap resultMap) {
        System.out.println(
                ">Autowired  LoginController"
        );
        this.userMapper = userMapper;
        this.resultMap = resultMap;
    }

    @GetMapping("/login")
    public ResultMap login(@RequestParam("username") String username,
                           @RequestParam("password") String password) {

        System.out.println(">username"+username+"\tpassword"+password);
        // 从mysql当中获取 password
        String realPassword = userMapper.getPassword(username);
        System.out.println(realPassword);
        if (realPassword == null) {
            return resultMap.fail().code(401).message("用户名错误");
        } else if (!realPassword.equals(password)) {
            return resultMap.fail().code(401).message("密码错误");
        } else {

            // 返回token
            return resultMap.success().code(200).message(JWTUtil.createToken(username));

            // return resultMap.success().code(200).message("验证");
        }

        // return "uuuu";

    }

}
