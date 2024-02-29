package com.example.zhijingai.entitys.dto;

import lombok.Data;

@Data
public class ThemePageQueryDTO {

    private int page;

    private int pageSize;

    //ppt主题id
    private Integer id;

    //ppt主题名称
    private String pptName;

    //ppt主题启用、禁用状态, (启用0，禁用1)
    private Integer status;

    //是否为会员使用，(会员 : 非会员)
    private String vip;


}
