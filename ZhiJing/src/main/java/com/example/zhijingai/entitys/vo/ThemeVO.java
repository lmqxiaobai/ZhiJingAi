package com.example.zhijingai.entitys.vo;

import com.example.zhijingai.entitys.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeVO {

    //ppt主题id
    private Integer id;

    //ppt主题名称
    private String pptName;

    //ppt主题启用、禁用状态, (启用0，禁用1)
    private Integer status;

    //是否为会员使用，(会员 : 非会员)
    private String vip;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

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
