package com.example.zhijingai.entitys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "员工登录时前端传递的数据模型")
public class UserLoginDTO implements Serializable {
    @ApiModelProperty("验证码")
    private String smsCode;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("QQ邮箱账号")
    private String mailbox;
}
