package com.example.zhijingai.service;

import com.example.zhijingai.entitys.dto.UserLoginDTO;
import com.example.zhijingai.entitys.dto.UserRegisterDTO;
import com.example.zhijingai.entitys.entity.User;

public interface UserService {
    /**
     * 用户登录
     */
    User login(UserLoginDTO userLoginDTO);

    Boolean queryMailbox(String mailbox);

    Boolean register(UserRegisterDTO userRegisterDTO);
}
