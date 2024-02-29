package com.example.zhijingai.entitys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "员工注册时前端传递的数据模型")
public class UserRegisterDTO implements Serializable {
    private Long id;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("QQ邮箱账号")
    private String mailbox;
    @ApiModelProperty("用户输入验证码")
    private String smsCode;

}
