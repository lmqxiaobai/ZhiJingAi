package com.example.zhijingai.entitys.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 用户登录响应返回的数据格式
 */
@Data
@Builder
public class UserLoginVO {
    private Long id;

    private String mailbox;
    private String password;

    private String token;
}
