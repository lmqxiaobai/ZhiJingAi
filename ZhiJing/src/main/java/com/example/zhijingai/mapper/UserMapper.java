package com.example.zhijingai.mapper;

import com.example.zhijingai.demo.annotation.AutoFill;
import com.example.zhijingai.demo.enumeration.OperationType;
import com.example.zhijingai.entitys.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据邮箱查询用户
     */
    @Select("select * from user where mailbox = #{mailbox}")
    User getByMailbox(String mailbox);

    /**
     * 添加用户
     */
//    @Insert("insert into user (username, password, mailbox, create_time, update_time)"+
//    "values "+
//    "(#{username}, #{password}, #{mailbox}, #{createTime}, #{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insert(User user);
}
