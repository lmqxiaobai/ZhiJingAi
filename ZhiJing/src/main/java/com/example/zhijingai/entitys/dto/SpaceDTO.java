package com.example.zhijingai.entitys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@ApiModel("个人空间模板添加数据传递的参数")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceDTO implements Serializable {
    //id
    private Long id;
    //状态，1：个人空间；2：草稿箱；3：回收站
    @ApiModelProperty("状态识别 1代表个人空间；2：草稿箱；3：回收站")
    private Integer status;

    //文件路径
    @ApiModelProperty("访问文件路径")
    private String file;

    //文件名称
    @ApiModelProperty("文件名称")
    private String name;

    //图片路径
    @ApiModelProperty("图片路径")
    private String photo;
}
