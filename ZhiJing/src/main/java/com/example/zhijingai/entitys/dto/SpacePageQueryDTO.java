package com.example.zhijingai.entitys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel("个人空间分页查询时传递前端的参数")
public class SpacePageQueryDTO implements Serializable {
    private Long id;

    @ApiModelProperty("页码")
    private int page;
    @ApiModelProperty("一页多少条数据")
    private int pageSize;

    @ApiModelProperty("模板名称")
    private String name;

    @ApiModelProperty("模板状态 1代表个人空间；2：草稿箱；3：回收站")
    private int status;

    @ApiModelProperty("图片路径")
    private String photo;
}
