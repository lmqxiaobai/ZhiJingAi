package com.example.zhijingai.entitys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分页查询时前端传递的数据参数")
public class PageQueryDTO implements Serializable {
    @ApiModelProperty("页码")
    private int page;
    @ApiModelProperty("每页显示条数")
    private int pageSize;
}
