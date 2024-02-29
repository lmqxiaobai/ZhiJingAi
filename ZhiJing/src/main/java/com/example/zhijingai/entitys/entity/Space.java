package com.example.zhijingai.entitys.entity;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Space implements Serializable {

    //id
    private Long id;

    //用户id
    private Long userId;

    //状态，1：个人空间；2：草稿箱；3：回收站
    @ApiParam("1：个人空间；2：草稿箱；3：回收站")
    private Integer status;

    //文件路径
    private String file;

    //文件名称
    private String name;

    //图片路径
    private String photo;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}

