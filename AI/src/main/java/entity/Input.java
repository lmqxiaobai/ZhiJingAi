package entity;

import lombok.Data;

@Data
public class Input {
    // 透明背景的主体图像URL
    private String base_image_url;
    // 引导图URL
    private String ref_image_url;
    // 引导文本提示词
    private String ref_prompt;
    // 负向提示词，不希望出现的内容
    private String neg_ref_prompt;
    // 图像上添加文字主标题
    private String title;
    // 图像上添加文字副标题
    private String sub_title;
}
