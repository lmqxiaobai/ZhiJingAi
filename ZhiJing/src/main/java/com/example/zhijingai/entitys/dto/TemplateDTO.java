package com.example.zhijingai.entitys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("管理端添加模板时传递的参数数据")
public class TemplateDTO implements Serializable {

    @ApiModelProperty("模板id")
    private Long id;
    @ApiModelProperty("模板名称")
    private String name;
    @ApiModelProperty("分类id集合")
    private List<Long> classificationId;
    @ApiModelProperty("图片路径")
    private String image;
    @ApiModelProperty("文档文件")
    private byte[] documentation;
    @ApiModelProperty("模板状态 1 可以编辑 0 不能编辑")
    private int status;
}
