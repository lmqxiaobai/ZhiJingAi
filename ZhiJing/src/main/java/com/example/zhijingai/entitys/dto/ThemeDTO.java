package com.example.zhijingai.entitys.dto;

import com.example.zhijingai.entitys.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeDTO {

    //ppt主题id
    private Integer id;

    //ppt主题名称
    private String pptName;

    //ppt主题启用、禁用状态, (启用0，禁用1)
    private Integer status;

    //是否为会员使用，(免费 : 会员)
    private String vip;

    //主题背景图片
    private List<ThemePicture> themePictureList;

    //主题标题样式
    private List<ThemeTitleStyle> themeTitleStyleList;

    //主题内容标题样式
    private List<ThemeSubStyle> themeSubStyleList;

    //主题文本内容样式
    private List<ThemeTextStyle> themeTextStyleList;

    //主题结尾样式
    private List<ThemeEndStyle> themeEndStyleList;

}
