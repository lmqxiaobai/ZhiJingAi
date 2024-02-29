package entity;

import lombok.Data;

@Data
public class Parameters {
    // 图片生成的数量，支持1~4，默认1
    private String n;
    // 当ref_image_url不为空时生效。在图像引导的过程中添加随机变化，数值越大与参考图相似度越低，默认值300，取值范围【0，999】
    private String noise_level;
    // 仅当ref_image_url和ref_prompt同时输入时生效，该参数设定文本和图像引导的权重。默认值为0.5，取值范围【0，1】
    private String ref_prompt_weight;
}
