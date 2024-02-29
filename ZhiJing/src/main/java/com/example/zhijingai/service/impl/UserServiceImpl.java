package com.example.zhijingai.service.impl;

import com.example.zhijingai.entitys.dto.UserLoginDTO;
import com.example.zhijingai.entitys.dto.UserRegisterDTO;
import com.example.zhijingai.entitys.entity.User;
import com.example.zhijingai.mapper.UserMapper;
import com.example.zhijingai.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     */
    public User login(UserLoginDTO userLoginDTO){
        String mailbox = userLoginDTO.getMailbox();
        String password = userLoginDTO.getPassword();
        User user = userMapper.getByMailbox(mailbox);

        // 处理异常情况（用户不存在、密码不对）
        if(user == null){
            throw new RuntimeException("账号不存在");
        }
        if(!password.equals(user.getPassword())){
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    /**
     * 邮箱查询
     */
    public Boolean queryMailbox(String mailbox){
        User user = userMapper.getByMailbox(mailbox);
        if(user!=null){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 用户注册
     */
    public Boolean register(UserRegisterDTO userRegisterDTO){
        User user = new User();

        // 对象属性拷贝
        BeanUtils.copyProperties(userRegisterDTO,user);

        if (userMapper.getByMailbox(user.getMailbox())==null){
            userMapper.insert(user);
            return true;
        }else {
            return false;
        }

    }
}
