package com.example.zhijingai.entitys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeTitleStyle {

    //自增id
    private Integer id;

    //主题专属id
    private Integer pptId;

    //文本框距左边界的距离
    private Integer recX;

    //文本框距上边界的距离
    private Integer recY;

    //文本框的宽度
    private Integer recWidth;

    //文本框的高度
    private Integer recHeight;

    //标题内容的字体大小
    private double titleSize;

    //标题默认加粗 '0'，可选不加粗 '1'
    private String titleBold;

    //标题字体样式
    private String titleFamily;

    //标题颜色rgb格式(r)
    private Integer colorR;

    //标题颜色rgb格式(g)
    private Integer colorG;

    //标题颜色rgb格式(b)
    private Integer colorB;

}
