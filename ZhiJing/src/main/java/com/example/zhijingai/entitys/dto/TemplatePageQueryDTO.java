package com.example.zhijingai.entitys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@ApiModel("模型分页查询时前端传递的数据参数")
public class TemplatePageQueryDTO implements Serializable {

    @ApiModelProperty("属性分类的id集合")
    private List<Long> classificationIds = new ArrayList<>();
    @ApiModelProperty("页码")
    private int page;
    @ApiModelProperty("每页显示条数")
    private int pageSize;

}
