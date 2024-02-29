package com.example.zhijingai.entitys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Theme {

    //ppt主题id
    private Integer id;

    //ppt主题名称
    private String pptName;

    //ppt主题启用、禁用状态, (启用0，禁用1)
    private Integer status;

    //是否为会员使用，(会员 : 非会员)
    private String vip;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
