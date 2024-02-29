package com.example.zhijingai.entitys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemePicture {

    //自增id
    private Integer id;

    //主题专属id
    private Integer pptId;

    //主题背景图片oss地址
    private String pictureAddress;

    //ppt背景图片分类，分为如下：‘主标题’，‘内容标题’，‘文本内容’，‘结尾’
    private String pictureCategory;

}
