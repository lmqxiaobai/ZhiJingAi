package com.example.zhijingai.entitys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("管理端添加分类字段传递的参数")
public class ClassificationDTO {
    @ApiModelProperty("大分类字段名称")
    private String name;
    @ApiModelProperty("小分类字段名称")
    private String classificationName;
}
