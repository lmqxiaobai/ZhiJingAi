package com.example.zhijingai.entitys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
// 生成一个无参的构造方法
@NoArgsConstructor
// 生成一个包含所有类成员变量的参数的构造方法
@AllArgsConstructor
public class User implements Serializable {
    private Long id;
    private String password;
    // 用户邮箱
    private String mailbox;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
